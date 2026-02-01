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
 * Prevents fluids from flowing without Fluid Dynamics law
 */
public class FluidDynamicsHandler {

    /**
     * Prevent fluid blocks from being placed (flow)
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onFluidPlace(BlockEvent.FluidPlaceBlockEvent event) {
        if (event.getLevel().isClientSide()) return;

        ServerLevel level = (ServerLevel) event.getLevel();

        // Find nearest player
        ServerPlayer nearestPlayer = (ServerPlayer) level.getNearestPlayer(
                event.getPos().getX(),
                event.getPos().getY(),
                event.getPos().getZ(),
                16.0,
                false
        );

        if (nearestPlayer != null) {
            PlayerProgression prog = ProgressionManager.get(nearestPlayer);

            if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
                if (!PhysicsManager.isUnlocked(nearestPlayer, PhysicsLaw.FLUID_DYNAMICS)) {
                    // Cancel fluid flow
                    event.setCanceled(true);

                    // Show message occasionally
                    if (nearestPlayer.getRandom().nextFloat() < 0.1f) {
                        nearestPlayer.displayClientMessage(
                                net.minecraft.network.chat.Component.literal(
                                        "§9Fluid Dynamics §7is required for liquids to flow!"),
                                true
                        );
                    }
                }
            }
        }
    }

    /**
     * Block any liquid block changes (additional safety)
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        PlayerProgression prog = ProgressionManager.get(player);

        if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
            if (!PhysicsManager.isUnlocked(player, PhysicsLaw.FLUID_DYNAMICS)) {
                BlockState placedState = event.getPlacedBlock();

                // Block liquid placement
                if (placedState.getBlock() instanceof LiquidBlock ||
                        placedState.is(Blocks.WATER) ||
                        placedState.is(Blocks.LAVA)) {

                    event.setCanceled(true);
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal(
                                    "§9Fluid Dynamics §7is required for liquids!"),
                            true
                    );
                }
            }
        }
    }
}