package com.example.claudeclient.modules;

import com.example.claudeclient.core.Module;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;

public class AnchorHud extends Module {
    public AnchorHud() {
        super("Anchor HUD", "แสดงจำนวน glowstone และประจุของ respawn anchor ที่เล็งอยู่", true);
        bool("Glowstone count", true);
        bool("Anchor charge", true);
    }

    @Override
    public void renderHud(DrawContext ctx, MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;
        int x = mc.getWindow().getScaledWidth() / 2 + 12;
        int y = mc.getWindow().getScaledHeight() / 2 + 20;

        if (settings.get("Glowstone count").on()) {
            int glow = 0;
            var inv = mc.player.getInventory();
            for (int i = 0; i < inv.size(); i++) {
                ItemStack s = inv.getStack(i);
                if (s.isOf(Items.GLOWSTONE)) glow += s.getCount();
            }
            ctx.drawTextWithShadow(mc.textRenderer, "Glowstone: " + glow, x, y, 0xFFFFD966);
        }
        if (settings.get("Anchor charge").on() && mc.crosshairTarget instanceof BlockHitResult hit) {
            var state = mc.world.getBlockState(hit.getBlockPos());
            if (state.isOf(Blocks.RESPAWN_ANCHOR)) {
                int c = state.get(RespawnAnchorBlock.CHARGES);
                int color = c == 0 ? 0xFFAAAAAA : (c < 4 ? 0xFFFFAA00 : 0xFF55FF55);
                ctx.drawTextWithShadow(mc.textRenderer, "Anchor: " + c + "/4", x, y + 12, color);
            }
        }
    }
}
