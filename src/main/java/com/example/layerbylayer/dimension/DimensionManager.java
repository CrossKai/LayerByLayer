package com.example.layerbylayer.dimension;

import com.example.layerbylayer.LayerByLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class DimensionManager {

    private static final int VOID_X = 0;
    private static final int VOID_Y = 250;
    private static final int VOID_Z = 0;
    private static final int PLATFORM_SIZE = 5;

    public static void teleportToVoid(ServerPlayer player) {
        ServerLevel overworld = player.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) {
            LayerByLayer.LOGGER.error("Overworld not found!");
            return;
        }

        // Create void platform
        createVoidPlatform(overworld);

        // Teleport player
        player.teleportTo(
                VOID_X + 0.5,
                VOID_Y + 1,
                VOID_Z + 0.5
        );

        // Reset velocity
        player.setDeltaMovement(0, 0, 0);
        player.fallDistance = 0;

        // Effects
        overworld.playSound(null, player.blockPosition(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS,
                1.0f, 0.5f);

        // Particles
        overworld.sendParticles(
                ParticleTypes.PORTAL,
                VOID_X + 0.5, VOID_Y + 1, VOID_Z + 0.5,
                50, 0.5, 0.5, 0.5, 0.1
        );

        // Message
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(
                        "§7You awaken in §f§lTHE VOID§7... Reality does not exist yet."),
                false
        );

        LayerByLayer.LOGGER.info("Player {} teleported to Void Space at {}, {}, {}",
                player.getName().getString(), VOID_X, VOID_Y, VOID_Z);
    }

    private static void createVoidPlatform(ServerLevel world) {
        // Create platform
        int halfSize = PLATFORM_SIZE / 2;
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int z = -halfSize; z <= halfSize; z++) {
                BlockPos pos = new BlockPos(VOID_X + x, VOID_Y, VOID_Z + z);
                world.setBlock(pos, Blocks.WHITE_CONCRETE.defaultBlockState(), 3);
            }
        }

        // Add invisible barriers around the platform
        for (int x = -(halfSize + 1); x <= (halfSize + 1); x++) {
            for (int z = -(halfSize + 1); z <= (halfSize + 1); z++) {
                if (Math.abs(x) == (halfSize + 1) || Math.abs(z) == (halfSize + 1)) {
                    for (int y = 1; y <= 3; y++) {
                        BlockPos pos = new BlockPos(VOID_X + x, VOID_Y + y, VOID_Z + z);
                        world.setBlock(pos, Blocks.BARRIER.defaultBlockState(), 3);
                    }
                }
            }
        }

        // Clear the area above the platform
        for (int x = -(halfSize + 2); x <= (halfSize + 2); x++) {
            for (int z = -(halfSize + 2); z <= (halfSize + 2); z++) {
                for (int y = 1; y <= 15; y++) {
                    BlockPos pos = new BlockPos(VOID_X + x, VOID_Y + y, VOID_Z + z);
                    if (world.getBlockState(pos).getBlock() != Blocks.BARRIER) {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }

        // Add light sources (hidden)
        BlockPos lightPos1 = new BlockPos(VOID_X, VOID_Y + 10, VOID_Z);
        BlockPos lightPos2 = new BlockPos(VOID_X + 5, VOID_Y + 10, VOID_Z + 5);
        BlockPos lightPos3 = new BlockPos(VOID_X - 5, VOID_Y + 10, VOID_Z - 5);

        world.setBlock(lightPos1, Blocks.LIGHT.defaultBlockState(), 3);
        world.setBlock(lightPos2, Blocks.LIGHT.defaultBlockState(), 3);
        world.setBlock(lightPos3, Blocks.LIGHT.defaultBlockState(), 3);

        LayerByLayer.LOGGER.info("Void platform created at {}, {}, {}", VOID_X, VOID_Y, VOID_Z);
    }

    public static void teleportToOverworld(ServerPlayer player) {
        ServerLevel overworld = player.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        BlockPos spawnPos = overworld.getSharedSpawnPos();

        player.teleportTo(
                spawnPos.getX() + 0.5,
                spawnPos.getY() + 1,
                spawnPos.getZ() + 0.5
        );

        // Reset velocity
        player.setDeltaMovement(0, 0, 0);
        player.fallDistance = 0;

        // Effects
        overworld.playSound(null, player.blockPosition(),
                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS,
                1.0f, 1.0f);

        // Particles
        overworld.sendParticles(
                ParticleTypes.TOTEM_OF_UNDYING,
                spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5,
                100, 0.5, 1.0, 0.5, 0.2
        );

        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(
                        "§a§l✓ Welcome to Reality!"),
                false
        );

        LayerByLayer.LOGGER.info("Player {} teleported to Overworld spawn",
                player.getName().getString());
    }
}