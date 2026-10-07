package com.example.claudeclient.core;

import com.example.claudeclient.gui.ModuleSettingsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Module {
    public final String name, desc;
    protected boolean enabled;
    public final Map<String, Setting> settings = new LinkedHashMap<>();

    protected Module(String name, String desc, boolean defaultEnabled) {
        this.name = name; this.desc = desc; this.enabled = defaultEnabled;
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean v) { enabled = v; Config.save(); }
    public void toggle() { setEnabled(!isEnabled()); }

    public void renderHud(DrawContext ctx, MinecraftClient mc) {}
    public void onTick(MinecraftClient mc) {}

    /** หน้าตั้งค่าของโมดูล (null = ไม่มี) */
    public Screen settingsScreen(Screen parent) {
        return settings.isEmpty() ? null : new ModuleSettingsScreen(this, parent);
    }

    protected Setting bool(String n, boolean def) {
        Setting s = new Setting(n, true, def ? 1 : 0, 0, 1, 1);
        settings.put(n, s); return s;
    }
    protected Setting num(String n, double def, double min, double max, double step) {
        Setting s = new Setting(n, false, def, min, max, step);
        settings.put(n, s); return s;
    }
}
