package com.example.claudeclient.modules;

import com.example.claudeclient.core.Module;

public class LagCut extends Module {
    public static LagCut I;

    public LagCut() {
        super("Lag Cut", "ตัดพาร์ติเคิลระเบิดและไฟบนจอเพื่อลดแลค", true);
        bool("Explosion particles off", true);
        bool("Fire overlay off", true);
        I = this;
    }

    public static boolean cutParticles() {
        return I != null && I.isEnabled() && I.settings.get("Explosion particles off").on();
    }
    public static boolean cutFire() {
        return I != null && I.isEnabled() && I.settings.get("Fire overlay off").on();
    }
}
