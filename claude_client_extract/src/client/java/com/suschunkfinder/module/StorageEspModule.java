package com.suschunkfinder.module;

import com.suschunkfinder.config.ModConfig;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;

public class StorageEspModule {

    public static final List<BlockPos> storagePositions = new ArrayList<>();
    private static int tickCounter = 0;

    public static void tick(MinecraftClient client) {
        if (!ModConfig.storageEspEnabled || client.world == null || client.player == null) {
            storagePositions.clear();
            return;
        }

        tickCounter++;
        if (tickCounter < 20) return;
        tickCounter = 0;

        storagePositions.clear();
        ChunkPos playerChunk = client.player.getChunkPos();
        int range = Math.min(ModConfig.scanRange, 6); // limit for ESP performance

        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                WorldChunk chunk = client.world.getChunk(playerChunk.x + dx, playerChunk.z + dz);
                if (chunk == null) continue;

                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (isStorage(be)) {
                        storagePositions.add(be.getPos());
                    }
                }
            }
        }
    }

    private static boolean isStorage(BlockEntity be) {
        if (ModConfig.includeChests && be instanceof ChestBlockEntity) return true;
        if (ModConfig.includeBarrels && be instanceof BarrelBlockEntity) return true;
        if (ModConfig.includeShulkers && be instanceof ShulkerBoxBlockEntity) return true;
        if (ModConfig.includeHoppers && be instanceof HopperBlockEntity) return true;
        if (be instanceof DropperBlockEntity || be instanceof DispenserBlockEntity) return true;
        return false;
    }
}
