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
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Ultimate fluid blocking - removes flowing fluids every tick
 */
public class UltimateFluidBlocker {

    private int tickCounter = 0;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        // Only check every 5 ticks for performance
        tickCounter++;
        if (tickCounter < 5) return;
        tickCounter = 0;

        // Check if ANY player has Fluid Dynamics locked
        boolean fluidDynamicsLocked = false;

        for (ServerPlayer player : level.players()) {
            PlayerProgression prog = ProgressionManager.get(player);

            if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
                if (!PhysicsManager.isUnlocked(player, PhysicsLaw.FLUID_DYNAMICS)) {
                    fluidDynamicsLocked = true;
                    break;
                }
            }
        }

        if (!fluidDynamicsLocked) return;

        // Scan for flowing liquids and remove them
        for (ServerPlayer player : level.players()) {
            BlockPos playerPos = player.blockPosition();

            // Check 32 block radius around player
            for (int x = -32; x <= 32; x++) {
                for (int y = -16; y <= 16; y++) {
                    for (int z = -32; z <= 32; z++) {
                        BlockPos checkPos = playerPos.offset(x, y, z);
                        BlockState state = level.getBlockState(checkPos);

                        // Check if it's a flowing liquid
                        if (state.is(Blocks.WATER) || state.is(Blocks.LAVA)) {
                            if (state.hasProperty(LiquidBlock.LEVEL)) {
                                int levelValue = state.getValue(LiquidBlock.LEVEL);

                                // If it's not a source block (level > 0), remove it
                                if (levelValue > 0) {
                                    level.setBlock(checkPos, Blocks.AIR.defaultBlockState(), 3);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}