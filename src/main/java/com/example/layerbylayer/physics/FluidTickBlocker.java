package com.example.layerbylayer.physics;

import com.example.layerbylayer.progression.PlayerProgression;
import com.example.layerbylayer.progression.ProgressionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * Blocks liquid placement and changes
 */
public class FluidTickBlocker {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        BlockState placedState = event.getPlacedBlock();

        // Check if it's a liquid
        if (placedState.getBlock() instanceof LiquidBlock ||
                placedState.is(Blocks.WATER) ||
                placedState.is(Blocks.LAVA)) {

            // Check ALL players in this dimension
            for (ServerPlayer player : level.players()) {
                PlayerProgression prog = ProgressionManager.get(player);

                if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
                    if (!PhysicsManager.isUnlocked(player, PhysicsLaw.FLUID_DYNAMICS)) {
                        // Only allow if player is placing it directly (within 6 blocks)
                        if (event.getEntity() instanceof ServerPlayer placer) {
                            double distance = placer.distanceToSqr(
                                    event.getPos().getX(),
                                    event.getPos().getY(),
                                    event.getPos().getZ()
                            );

                            // If player is too far, it's likely fluid spreading, not player placement
                            if (distance > 36.0) { // 6 blocks squared
                                event.setCanceled(true);
                                return;
                            }
                        } else {
                            // Not placed by a player = spreading
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }
    }
}