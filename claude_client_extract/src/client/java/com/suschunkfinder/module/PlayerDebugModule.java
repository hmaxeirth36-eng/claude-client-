package com.suschunkfinder.module;

import com.suschunkfinder.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.ChunkPos;

import java.util.HashSet;
import java.util.Set;

public class PlayerDebugModule {

    private static final Set<Long> playerChunks = new HashSet<>();

    public static Set<Long> getPlayerChunks() {
        return playerChunks;
    }

    public static void tick(MinecraftClient client) {
        playerChunks.clear();
        if (!ModConfig.playerDebugEnabled || client.world == null || client.player == null) {
            return;
        }

        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            if (player == client.player) continue;
            if (player.getY() <= ModConfig.scanYLevel) {
                ChunkPos cp = player.getChunkPos();
                playerChunks.add(ChunkPos.toLong(cp.x, cp.z));
            }
        }
    }
}
