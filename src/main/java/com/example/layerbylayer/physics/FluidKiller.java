package com.example.layerbylayer.physics;

import com.example.layerbylayer.LayerByLayer;
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
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Kills all flowing fluids every tick
 */
public class FluidKiller {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        // Check if ANY player has Fluid Dynamics locked
        boolean shouldKillFluids = false;

        for (ServerPlayer player : level.players()) {
            PlayerProgression prog = ProgressionManager.get(player);

            if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
                if (!PhysicsManager.isUnlocked(player, PhysicsLaw.FLUID_DYNAMICS)) {
                    shouldKillFluids = true;
                    break;
                }
            }
        }

        if (!shouldKillFluids) return;

        // Collect all flowing liquid positions
        List<BlockPos> flowingLiquids = new ArrayList<>();

        for (ServerPlayer player : level.players()) {
            BlockPos playerPos = player.blockPosition();

            // Scan around each player (smaller radius for performance)
            for (int x = -24; x <= 24; x++) {
                for (int y = -12; y <= 12; y++) {
                    for (int z = -24; z <= 24; z++) {
                        BlockPos checkPos = playerPos.offset(x, y, z);
                        BlockState state = level.getBlockState(checkPos);

                        // Check if it's a flowing liquid
                        if (state.getBlock() instanceof LiquidBlock) {
                            if (state.hasProperty(LiquidBlock.LEVEL)) {
                                int levelValue = state.getValue(LiquidBlock.LEVEL);

                                // Level 0 = source block, > 0 = flowing
                                if (levelValue > 0) {
                                    flowingLiquids.add(checkPos.immutable());
                                }
                            }
                        }
                    }
                }
            }
        }

        // Remove all flowing liquids at once
        if (!flowingLiquids.isEmpty()) {
            for (BlockPos pos : flowingLiquids) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2 | 16); // Flag 2 = no update, 16 = no re-render
            }

            LayerByLayer.LOGGER.debug("Removed {} flowing liquid blocks", flowingLiquids.size());
        }
    }
}