package com.suschunkfinder.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("suschunkfinder.json").toFile();

    public static boolean susChunkEnabled = true;
    public static boolean playerDebugEnabled = true;
    public static boolean storageEspEnabled = true;

    public static int scanYLevel = 0; // scan below or equal this Y
    public static int scanRange = 8; // chunks radius
    public static int minChestsForSus = 2;
    public static int minRedstoneForSus = 5;

    // Colors (ARGB)
    public static int outlineColor = 0xFFFF0000; // red
    public static int fillColor = 0x40FF0000; // semi transparent red
    public static int playerChunkColor = 0xFF00FF00; // green
    public static int playerFillColor = 0x4000FF00;
    public static int storageEspColor = 0xFFFFFF00; // yellow
    public static int highPercentColor = 0xFFFF5500; // orange

    public static boolean showOutline = true;
    public static boolean showFill = true;
    public static boolean showPercent = true;
    public static float fillOpacity = 0.25f;

    public static boolean includeChests = true;
    public static boolean includeBarrels = true;
    public static boolean includeShulkers = true;
    public static boolean includeHoppers = true;
    public static boolean includeSpawners = true;
    public static boolean includeRedstone = true;

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                Data data = GSON.fromJson(reader, Data.class);
                if (data != null) {
                    susChunkEnabled = data.susChunkEnabled;
                    playerDebugEnabled = data.playerDebugEnabled;
                    storageEspEnabled = data.storageEspEnabled;
                    scanYLevel = data.scanYLevel;
                    scanRange = data.scanRange;
                    minChestsForSus = data.minChestsForSus;
                    minRedstoneForSus = data.minRedstoneForSus;
                    outlineColor = data.outlineColor;
                    fillColor = data.fillColor;
                    playerChunkColor = data.playerChunkColor;
                    playerFillColor = data.playerFillColor;
                    storageEspColor = data.storageEspColor;
                    highPercentColor = data.highPercentColor;
                    showOutline = data.showOutline;
                    showFill = data.showFill;
                    showPercent = data.showPercent;
                    fillOpacity = data.fillOpacity;
                    includeChests = data.includeChests;
                    includeBarrels = data.includeBarrels;
                    includeShulkers = data.includeShulkers;
                    includeHoppers = data.includeHoppers;
                    includeSpawners = data.includeSpawners;
                    includeRedstone = data.includeRedstone;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    public static void save() {
        Data data = new Data();
        data.susChunkEnabled = susChunkEnabled;
        data.playerDebugEnabled = playerDebugEnabled;
        data.storageEspEnabled = storageEspEnabled;
        data.scanYLevel = scanYLevel;
        data.scanRange = scanRange;
        data.minChestsForSus = minChestsForSus;
        data.minRedstoneForSus = minRedstoneForSus;
        data.outlineColor = outlineColor;
        data.fillColor = fillColor;
        data.playerChunkColor = playerChunkColor;
        data.playerFillColor = playerFillColor;
        data.storageEspColor = storageEspColor;
        data.highPercentColor = highPercentColor;
        data.showOutline = showOutline;
        data.showFill = showFill;
        data.showPercent = showPercent;
        data.fillOpacity = fillOpacity;
        data.includeChests = includeChests;
        data.includeBarrels = includeBarrels;
        data.includeShulkers = includeShulkers;
        data.includeHoppers = includeHoppers;
        data.includeSpawners = includeSpawners;
        data.includeRedstone = includeRedstone;

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Data {
        boolean susChunkEnabled;
        boolean playerDebugEnabled;
        boolean storageEspEnabled;
        int scanYLevel;
        int scanRange;
        int minChestsForSus;
        int minRedstoneForSus;
        int outlineColor;
        int fillColor;
        int playerChunkColor;
        int playerFillColor;
        int storageEspColor;
        int highPercentColor;
        boolean showOutline;
        boolean showFill;
        boolean showPercent;
        float fillOpacity;
        boolean includeChests;
        boolean includeBarrels;
        boolean includeShulkers;
        boolean includeHoppers;
        boolean includeSpawners;
        boolean includeRedstone;
    }
}
