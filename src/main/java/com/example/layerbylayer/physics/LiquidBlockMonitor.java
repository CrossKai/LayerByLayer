package com.example.layerbylayer.physics;

import com.example.layerbylayer.progression.PlayerProgression;
import com.example.layerbylayer.progression.ProgressionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * Monitors and removes flowing liquids
 */
public class LiquidBlockMonitor {

    /**
     * Intercept any block change that involves liquids
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockChange(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer)) return;

        BlockState placedState = event.getPlacedBlock();

        // Check if it's a liquid
        if (placedState.getBlock() instanceof LiquidBlock ||
                placedState.is(Blocks.WATER) ||
                placedState.is(Blocks.LAVA)) {

            ServerLevel level = (ServerLevel) event.getLevel();

            // Check all players
            for (ServerPlayer player : level.players()) {
                PlayerProgression prog = ProgressionManager.get(player);

                if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
                    if (!PhysicsManager.isUnlocked(player, PhysicsLaw.FLUID_DYNAMICS)) {
                        // Allow only if it's placed by a player directly
                        // But cancel if it's spreading
                        if (!isDirectPlayerPlacement(event.getPos(), player)) {
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean isDirectPlayerPlacement(BlockPos pos, ServerPlayer player) {
        // Check if player is close enough (within 6 blocks) to have placed it manually
        double distance = player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());
        return distance < 36.0; // 6 blocks squared
    }
}