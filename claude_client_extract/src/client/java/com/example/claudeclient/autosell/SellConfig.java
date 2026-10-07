package com.example.claudeclient.autosell;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SellConfig {
    public Set<String> selected = new LinkedHashSet<>();
    // ของหายาก: ต้องกดยืนยันก่อนเลือก (แก้รายการได้ในไฟล์ config/claudeclient-autosell.json)
    public Set<String> rare = new LinkedHashSet<>(List.of(
        "minecraft:elytra", "minecraft:dragon_egg", "minecraft:nether_star", "minecraft:beacon",
        "minecraft:enchanted_golden_apple", "minecraft:trident", "minecraft:heart_of_the_sea",
        "minecraft:totem_of_undying", "minecraft:dragon_head", "minecraft:wither_skeleton_skull",
        "minecraft:spawner", "minecraft:trial_spawner", "minecraft:shulker_shell",
        "minecraft:ancient_debris", "minecraft:mace", "minecraft:heavy_core", "minecraft:end_crystal"));
    public int clickDelay = 3;      // หน่วง tick ระหว่างย้ายของแต่ละช่อง
    public boolean autoClose = true; // ปิดกล่องเองหลังย้ายเสร็จ (false = ให้ปิดเอง)
    public boolean enabled = true;   // สวิตช์หลัก: ปิดแล้วปุ่มขาย/loop ไม่ทำงาน
    public boolean loop = false;     // ขายอัตโนมัติเมื่อมีของที่เลือกเข้าตัว
    public int loopCooldown = 40;    // tick พักระหว่างรอบของ loop (20 = 1 วินาที)

    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("claudeclient-autosell.json");
    public static SellConfig I = new SellConfig();

    public static void load() {
        try (Reader r = Files.newBufferedReader(FILE)) {
            SellConfig c = GSON.fromJson(r, SellConfig.class);
            if (c != null) I = c;
        } catch (Exception ignored) {}
        if (I.selected == null) I.selected = new LinkedHashSet<>();
        if (I.rare == null) I.rare = new LinkedHashSet<>();
        if (I.clickDelay < 1) I.clickDelay = 1;
        if (I.clickDelay > 10) I.clickDelay = 10;
        if (I.loopCooldown < 10) I.loopCooldown = 10;
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(FILE)) {
            GSON.toJson(I, w);
        } catch (Exception ignored) {}
    }

    public static boolean isRare(String id) {
        return I.rare.contains(id) || id.startsWith("minecraft:netherite_") || id.endsWith("shulker_box");
    }
}
