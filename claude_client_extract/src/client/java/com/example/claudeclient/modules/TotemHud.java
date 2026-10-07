package com.example.claudeclient.modules;

import com.example.claudeclient.core.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class TotemHud extends Module {
    public TotemHud() {
        super("Totem HUD", "ตารางย่อโชว์เฉพาะโทเท็ม ไฮไลต์ 2 อันที่ใกล้สุด", true);
        num("Scale", 1.0, 0.5, 4.0, 0.1);
        num("Position X %", 100, 0, 100, 1);
        num("Position Y %", 50, 0, 100, 1);
        bool("Highlight nearest 2", true);
    }

    static void border(DrawContext ctx, int x, int y, int w, int c) {
        ctx.fill(x, y, x + w, y + 1, c);
        ctx.fill(x, y + w - 1, x + w, y + w, c);
        ctx.fill(x, y, x + 1, y + w, c);
        ctx.fill(x + w - 1, y, x + w, y + w, c);
    }

    @Override
    public void renderHud(DrawContext ctx, MinecraftClient mc) {
        if (mc.player == null || mc.currentScreen != null) return;
        var inv = mc.player.getInventory();

        List<Integer> near = new ArrayList<>();
        for (int i = 0; i <= 8 && near.size() < 2; i++)
            if (inv.getStack(i).isOf(Items.TOTEM_OF_UNDYING)) near.add(i);
        for (int i = 9; i <= 35 && near.size() < 2; i++)
            if (inv.getStack(i).isOf(Items.TOTEM_OF_UNDYING)) near.add(i);

        float scale = settings.get("Scale").f();
        int w = mc.getWindow().getScaledWidth(), h = mc.getWindow().getScaledHeight();
        float gridW = 162 * scale, gridH = 76 * scale;
        float x = (w - gridW) * settings.get("Position X %").f() / 100f;
        float y = (h - gridH) * settings.get("Position Y %").f() / 100f;
        boolean hi = settings.get("Highlight nearest 2").on();

        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().translate(x, y);
        ctx.getMatrices().scale(scale, scale);
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 9; col++) {
                int idx = row < 3 ? 9 + row * 9 + col : col;
                int px = col * 18, py = row * 18 + (row == 3 ? 4 : 0);
                ctx.fill(px, py, px + 16, py + 16, 0x33FFFFFF);
                ItemStack s = inv.getStack(idx);
                if (s.isOf(Items.TOTEM_OF_UNDYING)) {
                    ctx.drawItem(s, px, py);
                    int n = near.indexOf(idx);
                    if (hi && n >= 0) border(ctx, px - 1, py - 1, 18, n == 0 ? 0xFF00FF66 : 0xFFFFDD33);
                }
            }
        }
        ctx.getMatrices().popMatrix();
    }
}
