package com.suschunkfinder.gui;

import com.suschunkfinder.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {

    private final Screen parent;
    private int scrollOffset = 0;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Sus Chunk Finder Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 40;

        // Toggles
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Sus Chunk: " + (ModConfig.susChunkEnabled ? "ON" : "OFF")),
                btn -> {
                    ModConfig.susChunkEnabled = !ModConfig.susChunkEnabled;
                    btn.setMessage(Text.literal("Sus Chunk: " + (ModConfig.susChunkEnabled ? "ON" : "OFF")));
                }).dimensions(centerX - 100, y, 200, 20).build());
        y += 24;

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Player Debug: " + (ModConfig.playerDebugEnabled ? "ON" : "OFF")),
                btn -> {
                    ModConfig.playerDebugEnabled = !ModConfig.playerDebugEnabled;
                    btn.setMessage(Text.literal("Player Debug: " + (ModConfig.playerDebugEnabled ? "ON" : "OFF")));
                }).dimensions(centerX - 100, y, 200, 20).build());
        y += 24;

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Storage ESP: " + (ModConfig.storageEspEnabled ? "ON" : "OFF")),
                btn -> {
                    ModConfig.storageEspEnabled = !ModConfig.storageEspEnabled;
                    btn.setMessage(Text.literal("Storage ESP: " + (ModConfig.storageEspEnabled ? "ON" : "OFF")));
                }).dimensions(centerX - 100, y, 200, 20).build());
        y += 24;

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Show Outline: " + (ModConfig.showOutline ? "ON" : "OFF")),
                btn -> {
                    ModConfig.showOutline = !ModConfig.showOutline;
                    btn.setMessage(Text.literal("Show Outline: " + (ModConfig.showOutline ? "ON" : "OFF")));
                }).dimensions(centerX - 100, y, 200, 20).build());
        y += 24;

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Show Fill: " + (ModConfig.showFill ? "ON" : "OFF")),
                btn -> {
                    ModConfig.showFill = !ModConfig.showFill;
                    btn.setMessage(Text.literal("Show Fill: " + (ModConfig.showFill ? "ON" : "OFF")));
                }).dimensions(centerX - 100, y, 200, 20).build());
        y += 24;

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Show %: " + (ModConfig.showPercent ? "ON" : "OFF")),
                btn -> {
                    ModConfig.showPercent = !ModConfig.showPercent;
                    btn.setMessage(Text.literal("Show %: " + (ModConfig.showPercent ? "ON" : "OFF")));
                }).dimensions(centerX - 100, y, 200, 20).build());
        y += 28;

        // Y Level buttons
        addDrawableChild(ButtonWidget.builder(Text.literal("Y Level: " + ModConfig.scanYLevel + "  [-]"),
                btn -> {
                    ModConfig.scanYLevel = Math.max(-64, ModConfig.scanYLevel - 1);
                    btn.setMessage(Text.literal("Y Level: " + ModConfig.scanYLevel + "  [-]"));
                }).dimensions(centerX - 100, y, 95, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("[+]"),
                btn -> {
                    ModConfig.scanYLevel = Math.min(320, ModConfig.scanYLevel + 1);
                    // update the other button is hard, just leave
                }).dimensions(centerX + 5, y, 95, 20).build());
        y += 24;

        // Range
        addDrawableChild(ButtonWidget.builder(Text.literal("Scan Range: " + ModConfig.scanRange + "  [-]"),
                btn -> {
                    ModConfig.scanRange = Math.max(1, ModConfig.scanRange - 1);
                    btn.setMessage(Text.literal("Scan Range: " + ModConfig.scanRange + "  [-]"));
                }).dimensions(centerX - 100, y, 95, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("[+]"),
                btn -> {
                    ModConfig.scanRange = Math.min(16, ModConfig.scanRange + 1);
                }).dimensions(centerX + 5, y, 95, 20).build());
        y += 28;

        // Include toggles
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Chests: " + (ModConfig.includeChests ? "ON" : "OFF")),
                btn -> {
                    ModConfig.includeChests = !ModConfig.includeChests;
                    btn.setMessage(Text.literal("Chests: " + (ModConfig.includeChests ? "ON" : "OFF")));
                }).dimensions(centerX - 100, y, 95, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Barrels: " + (ModConfig.includeBarrels ? "ON" : "OFF")),
                btn -> {
                    ModConfig.includeBarrels = !ModConfig.includeBarrels;
                    btn.setMessage(Text.literal("Barrels: " + (ModConfig.includeBarrels ? "ON" : "OFF")));
                }).dimensions(centerX + 5, y, 95, 20).build());
        y += 24;

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Shulkers: " + (ModConfig.includeShulkers ? "ON" : "OFF")),
                btn -> {
                    ModConfig.includeShulkers = !ModConfig.includeShulkers;
                    btn.setMessage(Text.literal("Shulkers: " + (ModConfig.includeShulkers ? "ON" : "OFF")));
                }).dimensions(centerX - 100, y, 95, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Spawners: " + (ModConfig.includeSpawners ? "ON" : "OFF")),
                btn -> {
                    ModConfig.includeSpawners = !ModConfig.includeSpawners;
                    btn.setMessage(Text.literal("Spawners: " + (ModConfig.includeSpawners ? "ON" : "OFF")));
                }).dimensions(centerX + 5, y, 95, 20).build());
        y += 24;

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Redstone: " + (ModConfig.includeRedstone ? "ON" : "OFF")),
                btn -> {
                    ModConfig.includeRedstone = !ModConfig.includeRedstone;
                    btn.setMessage(Text.literal("Redstone: " + (ModConfig.includeRedstone ? "ON" : "OFF")));
                }).dimensions(centerX - 100, y, 200, 20).build());
        y += 28;

        // Save & Done
        addDrawableChild(ButtonWidget.builder(Text.literal("Save & Done"), btn -> {
            ModConfig.save();
            this.client.setScreen(parent);
        }).dimensions(centerX - 100, y, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Press \\ to open this menu"), this.width / 2, this.height - 20, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        ModConfig.save();
        this.client.setScreen(parent);
    }
}
