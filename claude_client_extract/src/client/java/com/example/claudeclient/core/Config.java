package com.example.claudeclient.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class Config {
    static class Data {
        Map<String, Boolean> enabled = new LinkedHashMap<>();
        Map<String, Map<String, Double>> values = new LinkedHashMap<>();
    }

    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("claudeclient.json");

    public static void load() {
        Data d = new Data();
        try (Reader r = Files.newBufferedReader(FILE)) {
            Data read = GSON.fromJson(r, Data.class);
            if (read != null) d = read;
        } catch (Exception ignored) {}
        if (d.enabled == null) d.enabled = new LinkedHashMap<>();
        if (d.values == null) d.values = new LinkedHashMap<>();

        for (Module m : Modules.ALL) {
            Boolean en = d.enabled.get(m.name);
            if (en != null) m.enabled = en;
            Map<String, Double> vals = d.values.get(m.name);
            if (vals == null) continue;
            for (Setting s : m.settings.values()) {
                Double v = vals.get(s.name);
                if (v != null) s.value = Math.max(s.min, Math.min(s.max, v));
            }
        }
    }

    public static void save() {
        Data d = new Data();
        for (Module m : Modules.ALL) {
            d.enabled.put(m.name, m.isEnabled());
            Map<String, Double> vals = new LinkedHashMap<>();
            for (Setting s : m.settings.values()) vals.put(s.name, s.value);
            d.values.put(m.name, vals);
        }
        try (Writer w = Files.newBufferedWriter(FILE)) {
            GSON.toJson(d, w);
        } catch (Exception ignored) {}
    }
}
