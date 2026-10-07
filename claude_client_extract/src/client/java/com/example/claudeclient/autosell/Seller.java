package com.example.claudeclient.autosell;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;

/** /sell -> รอกล่อง -> ย้ายของที่เลือก -> ปิดกล่อง  (loop: ทำซ้ำเมื่อมีของที่เลือกเข้าตัวมาอีก) */
public class Seller {
    enum State { IDLE, WAIT_GUI, MOVING }
    static State state = State.IDLE;
    static boolean auto;   // รอบนี้มาจาก loop (ข้ามของหายากเสมอ)
    static int timer, waited, moved, lastSlot, lastCount, cooldown, failures, scan;
    static final Set<Integer> failed = new HashSet<>();

    static void msg(MinecraftClient mc, String s) {
        if (mc.player != null) mc.player.sendMessage(Text.literal("[AutoSell] " + s), true);
    }

    static boolean sellable(ItemStack st) {
        if (st.isEmpty()) return false;
        String id = Registries.ITEM.getId(st.getItem()).toString();
        if (!SellConfig.I.selected.contains(id)) return false;
        return !(auto && SellConfig.isRare(id));
    }

    /** กดปุ่มขายเอง (1 รอบ) หรือกดซ้ำเพื่อยกเลิก */
    public static void start(MinecraftClient mc) { start(mc, false); }

    static void start(MinecraftClient mc, boolean fromLoop) {
        if (mc.player == null) return;
        if (state != State.IDLE) {
            state = State.IDLE;
            if (SellConfig.I.loop) { SellConfig.I.loop = false; SellConfig.save(); }
            msg(mc, "ยกเลิก (loop ปิด)");
            return;
        }
        if (!SellConfig.I.enabled) { msg(mc, "สวิตช์หลักปิดอยู่"); return; }
        if (SellConfig.I.selected.isEmpty()) { msg(mc, "ยังไม่ได้เลือกของ (กด \\ เพื่อเปิดเมนู)"); return; }
        auto = fromLoop;
        mc.player.networkHandler.sendChatCommand("sell");
        state = State.WAIT_GUI;
        waited = 0; moved = 0; lastSlot = -1; failed.clear();
    }

    static void finish(MinecraftClient mc, boolean ok) {
        state = State.IDLE;
        if (ok) { failures = 0; cooldown = SellConfig.I.loopCooldown; }
        else {
            cooldown = 200;
            if (auto && ++failures >= 3) {
                SellConfig.I.loop = false; SellConfig.save(); failures = 0;
                msg(mc, "ขายไม่สำเร็จ 3 ครั้งติด loop ปิดอัตโนมัติ");
            }
        }
    }

    /** loop: ถ้ามีของที่เลือกอยู่ในตัวและไม่ได้เปิดหน้าจออะไร ให้เริ่มรอบใหม่ */
    public static void autoCheck(MinecraftClient mc) {
        var c = SellConfig.I;
        if (!c.enabled || !c.loop || state != State.IDLE || mc.player == null || mc.currentScreen != null) return;
        if (cooldown > 0) { cooldown--; return; }
        if (++scan < 10) return;
        scan = 0;
        auto = true;
        var inv = mc.player.getInventory();
        for (int i = 0; i < 36; i++) {
            if (sellable(inv.getStack(i))) { start(mc, true); return; }
        }
    }

    public static void tick(MinecraftClient mc) {
        if (state == State.IDLE || mc.player == null) return;
        if (!SellConfig.I.enabled) { state = State.IDLE; return; }

        if (state == State.WAIT_GUI) {
            if (mc.currentScreen instanceof GenericContainerScreen) {
                state = State.MOVING;
                timer = SellConfig.I.clickDelay * 2;
            } else if (++waited > 100) {
                msg(mc, "กล่องขายไม่เปิด");
                finish(mc, false);
            }
            return;
        }

        if (!(mc.currentScreen instanceof GenericContainerScreen gcs)) { finish(mc, moved > 0); return; }
        if (timer-- > 0) return;
        timer = SellConfig.I.clickDelay;

        var handler = gcs.getScreenHandler();

        if (lastSlot >= 0) {
            var st = handler.slots.get(lastSlot).getStack();
            if (!st.isEmpty() && st.getCount() >= lastCount) failed.add(lastSlot);
        }

        for (Slot s : handler.slots) {
            if (!(s.inventory instanceof PlayerInventory) || failed.contains(s.id)) continue;
            if (!sellable(s.getStack())) continue;
            lastSlot = s.id;
            lastCount = s.getStack().getCount();
            mc.interactionManager.clickSlot(handler.syncId, s.id, 0, SlotActionType.QUICK_MOVE, mc.player);
            if (++moved > 120) break;
            return;
        }

        boolean ok = moved > 0;
        if (SellConfig.I.autoClose) {
            mc.player.closeHandledScreen();
            msg(mc, "ปิดกล่องแล้ว (ย้าย " + moved + " ช่อง)");
        } else {
            msg(mc, "ย้ายเสร็จ ปิดกล่องเพื่อขาย");
        }
        finish(mc, ok);
    }
}
