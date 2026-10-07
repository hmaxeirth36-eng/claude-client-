package com.example.claudeclient.gui;

import com.example.claudeclient.core.Config;
import com.example.claudeclient.core.Module;
import com.example.claudeclient.core.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ModuleSettingsScreen extends Screen {
    final Module module;
    final Screen parent;

    public ModuleSettingsScreen(Module module, Screen parent) {
        super(Text.literal(module.name));
        this.module = module; this.parent = parent;
    }

    static Text boolLabel(Setting s) { return Text.literal(s.name + ": " + (s.on() ? "ON" : "OFF")); }

    class SettingSlider extends SliderWidget {
        final Setting s;
        SettingSlider(int x, int y, int w, Setting s) {
            super(x, y, w, 20, Text.empty(), (s.value - s.min) / (s.max - s.min));
            this.s = s;
            updateMessage();
        }
        double cur() {
            double v = s.min + value * (s.max - s.min);
            return Math.round(v / s.step) * s.step;
        }
        @Override protected void updateMessage() {
            if (s == null) return;
            double v = cur();
            setMessage(Text.literal(s.name + ": " + (s.step >= 1 ? String.valueOf((int) v) : String.format("%.1f", v))));
        }
        @Override protected void applyValue() { s.value = cur(); }
    }

    @Override
    protected void init() {
        int x = width / 2 - 100, y = 36;
        for (Setting s : module.settings.values()) {
            if (s.bool) {
                addDrawableChild(ButtonWidget.builder(boolLabel(s), b -> {
                    s.value = s.on() ? 0 : 1; b.setMessage(boolLabel(s));
                }).dimensions(x, y, 200, 20).build());
            } else {
                addDrawableChild(new SettingSlider(x, y, 200, s));
            }
            y += 24;
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> close())
                .dimensions(width / 2 - 50, y + 8, 100, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, module.name, width / 2, 14, 0xFF7FD4FF);
    }

    @Override public void close() { client.setScreen(parent); }
    @Override public void removed() { Config.save(); }
}
