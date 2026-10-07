package com.suschunkfinder.module;

import com.suschunkfinder.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.WorldChunk;

import java.util.*;

public class SusChunkModule {

    public static class ChunkData {
        public final ChunkPos pos;
        public int chestCount;
        public int redstoneCount;
        public int spawnerCount;
        public int percent;
        public boolean isSus;

        public ChunkData(ChunkPos pos) {
            this.pos = pos;
        }
    }

    private static final Map<Long, ChunkData> susChunks = new HashMap<>();
    private static int tickCounter = 0;

    public static Map<Long, ChunkData> getSusChunks() {
        return susChunks;
    }

    public static void tick(MinecraftClient client) {
        if (!ModConfig.susChunkEnabled || client.world == null || client.player == null) {
            susChunks.clear();
            return;
        }

        tickCounter++;
        if (tickCounter < 40) return; // scan every 2 seconds
        tickCounter = 0;

        susChunks.clear();
        ChunkPos playerChunk = client.player.getChunkPos();
        int range = ModConfig.scanRange;

        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                int cx = playerChunk.x + dx;
                int cz = playerChunk.z + dz;
                WorldChunk chunk = client.world.getChunk(cx, cz);
                if (chunk == null) continue;

                ChunkData data = scanChunk(chunk, client);
                if (data.isSus) {
                    susChunks.put(ChunkPos.toLong(cx, cz), data);
                }
            }
        }
    }

    private static ChunkData scanChunk(WorldChunk chunk, MinecraftClient client) {
        ChunkPos pos = chunk.getPos();
        ChunkData data = new ChunkData(pos);

        int minY = client.world.getBottomY();
        int maxY = Math.min(
                ModConfig.scanYLevel,
                client.world.getTopY(Heightmap.Type.MOTION_BLOCKING, pos.getStartX() + 8, pos.getStartZ() + 8)
        );

        // Scan block entities (more efficient)
        for (BlockEntity be : chunk.getBlockEntities().values()) {
            BlockPos bp = be.getPos();
            if (bp.getY() > maxY) continue;

            if (ModConfig.includeChests && be instanceof ChestBlockEntity) {
                data.chestCount++;
            } else if (ModConfig.includeBarrels && be instanceof BarrelBlockEntity) {
                data.chestCount++;
            } else if (ModConfig.includeShulkers && be instanceof ShulkerBoxBlockEntity) {
                data.chestCount++;
            } else if (ModConfig.includeHoppers && be instanceof HopperBlockEntity) {
                data.chestCount++;
            } else if (ModConfig.includeSpawners && be instanceof MobSpawnerBlockEntity) {
                data.spawnerCount++;
            }
        }

        // Scan for redstone components (sample some positions for performance)
        if (ModConfig.includeRedstone) {
            for (int x = 0; x < 16; x += 2) {
                for (int z = 0; z < 16; z += 2) {
                    for (int y = minY; y <= maxY; y += 4) {
                        BlockPos bp = new BlockPos(pos.getStartX() + x, y, pos.getStartZ() + z);
                        Block block = client.world.getBlockState(bp).getBlock();
                        if (isRedstone(block)) {
                            data.redstoneCount++;
                        }
                    }
                }
            }
        }

        // Calculate percent
        int score = data.chestCount * 15 + data.redstoneCount * 3 + data.spawnerCount * 25;
        data.percent = Math.min(100, score);
        data.isSus = data.chestCount >= ModConfig.minChestsForSus
                || data.redstoneCount >= ModConfig.minRedstoneForSus
                || data.spawnerCount > 0
                || data.percent >= 20;

        return data;
    }

    private static boolean isRedstone(Block block) {
        return block == Blocks.REDSTONE_WIRE
                || block == Blocks.REPEATER
                || block == Blocks.COMPARATOR
                || block == Blocks.REDSTONE_TORCH
                || block == Blocks.REDSTONE_BLOCK
                || block == Blocks.PISTON
                || block == Blocks.STICKY_PISTON
                || block == Blocks.OBSERVER
                || block == Blocks.HOPPER
                || block == Blocks.DROPPER
                || block == Blocks.DISPENSER
                || block == Blocks.TARGET
                || block == Blocks.NOTE_BLOCK
                || block == Blocks.DAYLIGHT_DETECTOR;
    }
}
