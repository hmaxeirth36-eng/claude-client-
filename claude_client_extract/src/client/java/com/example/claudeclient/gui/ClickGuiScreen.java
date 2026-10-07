package com.example.claudeclient.gui;

import com.example.claudeclient.core.Module;
import com.example.claudeclient.core.Modules;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public class ClickGuiScreen extends Screen {
    final Map<Module, ButtonWidget> toggles = new HashMap<>();

    public ClickGuiScreen() { super(Text.literal("Claude Client")); }

    static Text label(Module m) { return Text.literal(m.name + ": " + (m.isEnabled() ? "ON" : "OFF")); }

    @Override
    protected void init() {
        int cx = width / 2, y = 36;
        for (Module m : Modules.ALL) {
            ButtonWidget t = ButtonWidget.builder(label(m), b -> { m.toggle(); b.setMessage(label(m)); })
                    .dimensions(cx - 110, y, 150, 20).build();
            t.setTooltip(Tooltip.of(Text.literal(m.desc)));
            addDrawableChild(t);

            ButtonWidget s = ButtonWidget.builder(Text.literal("Settings"), b -> {
                Screen next = m.settingsScreen(this);
                if (next != null) client.setScreen(next);
            }).dimensions(cx + 44, y, 66, 20).build();
            s.active = !m.settings.isEmpty() || m.name.equals("Auto Sell");
            addDrawableChild(s);
            y += 24;
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), b -> close())
                .dimensions(cx - 50, y + 8, 100, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, "Claude Client", width / 2, 14, 0xFF7FD4FF);
    }

    @Override
    public void removed() { com.example.claudeclient.core.Config.save(); }
}
