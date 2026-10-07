package com.example.claudeclient.modules;

import com.example.claudeclient.autosell.SellConfig;
import com.example.claudeclient.autosell.SellMenuScreen;
import com.example.claudeclient.core.Module;
import net.minecraft.client.gui.screen.Screen;

public class AutoSell extends Module {
    public AutoSell() {
        super("Auto Sell", "เลือกของแล้วกดปุ่มเพื่อขายผ่าน /sell (มี loop ให้เลือก)", true);
    }

    // สถานะเปิด/ปิดใช้ร่วมกับค่าของ Auto Sell เอง
    @Override public boolean isEnabled() { return SellConfig.I.enabled; }
    @Override public void setEnabled(boolean v) { SellConfig.I.enabled = v; SellConfig.save(); }

    @Override
    public Screen settingsScreen(Screen parent) { return new SellMenuScreen(parent); }
}
