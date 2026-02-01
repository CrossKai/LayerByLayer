package com.example.layerbylayer.physics;

import com.example.layerbylayer.progression.PlayerProgression;
import com.example.layerbylayer.progression.ProgressionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * Additional fluid spread blocking - blocks neighbor updates
 */
public class FluidSpreadBlocker {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        BlockState state = event.getState();

        // Block neighbor updates from liquids
        if (state.is(Blocks.WATER) || state.is(Blocks.LAVA)) {
            for (ServerPlayer player : level.players()) {
                PlayerProgression prog = ProgressionManager.get(player);

                if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
                    if (!PhysicsManager.isUnlocked(player, PhysicsLaw.FLUID_DYNAMICS)) {
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }
    }
}