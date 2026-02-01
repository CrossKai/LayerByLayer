package com.example.layerbylayer.physics;

import com.example.layerbylayer.progression.PlayerProgression;
import com.example.layerbylayer.progression.ProgressionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * Prevents gravity-affected blocks from falling
 */
public class GravityHandler {

    /**
     * Cancel FallingBlockEntity spawns if gravity is locked
     */
    @SubscribeEvent
    public void onFallingBlockSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof FallingBlockEntity)) return;
        if (event.getLevel().isClientSide()) return;

        FallingBlockEntity fallingBlock = (FallingBlockEntity) event.getEntity();
        ServerLevel level = (ServerLevel) event.getLevel();

        // Check ALL players in the world, not just nearest
        boolean shouldBlock = false;

        for (ServerPlayer player : level.players()) {
            PlayerProgression prog = ProgressionManager.get(player);

            if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
                if (!PhysicsManager.isUnlocked(player, PhysicsLaw.GRAVITY)) {
                    shouldBlock = true;
                    break;
                }
            }
        }

        if (shouldBlock) {
            // Get the exact position where the block started falling from
            BlockPos startPos = new BlockPos(
                    (int) Math.floor(fallingBlock.getX()),
                    (int) Math.floor(fallingBlock.getY()),
                    (int) Math.floor(fallingBlock.getZ())
            );

            BlockState state = fallingBlock.getBlockState();

            // Cancel the falling entity
            event.setCanceled(true);

            // Place the block back
            level.setBlock(startPos, state, 3);
        }
    }

    /**
     * Warn when placing gravity-affected blocks
     */
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        PlayerProgression prog = ProgressionManager.get(player);

        if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
            if (!PhysicsManager.isUnlocked(player, PhysicsLaw.GRAVITY)) {
                BlockState state = event.getPlacedBlock();

                if (state.getBlock() instanceof FallingBlock) {
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal(
                                    "§e⚠ This block should fall, but gravity doesn't exist yet!"),
                            true
                    );
                }
            }
        }
    }
}