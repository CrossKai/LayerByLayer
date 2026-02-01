package com.example.layerbylayer.physics;

import com.example.layerbylayer.progression.PlayerProgression;
import com.example.layerbylayer.progression.ProgressionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Handles physics restrictions based on unlocked laws
 */
public class PhysicsRestrictions {

    /**
     * CONSERVATION LAW: Show warnings when breaking blocks
     */
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        PlayerProgression prog = ProgressionManager.get(player);

        if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
            if (!PhysicsManager.isUnlocked(player, PhysicsLaw.CONSERVATION)) {
                if (player.getRandom().nextFloat() < 0.15f) {
                    player.displayClientMessage(
                            Component.literal("§7" + PhysicsLaw.CONSERVATION.getWarningText()),
                            true
                    );
                }
            }
        }
    }

    /**
     * THERMODYNAMICS LAW: Prevent furnace usage
     * FLUID_DYNAMICS LAW: Prevent bucket usage
     */
    @SubscribeEvent
    public void onBlockUse(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        PlayerProgression prog = ProgressionManager.get(player);

        if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
            BlockPos pos = event.getPos();
            BlockState state = event.getLevel().getBlockState(pos);

            // Block furnace usage
            if (!PhysicsManager.isUnlocked(player, PhysicsLaw.THERMODYNAMICS)) {
                if (state.is(Blocks.FURNACE) ||
                        state.is(Blocks.BLAST_FURNACE) ||
                        state.is(Blocks.SMOKER)) {

                    event.setCanceled(true);
                    player.displayClientMessage(
                            Component.literal("§c" + PhysicsLaw.THERMODYNAMICS.getDisplayName() +
                                    " §7is required to use furnaces!"),
                            true
                    );
                }
            }

            // Block ALL bucket usage without Fluid Dynamics
            if (!PhysicsManager.isUnlocked(player, PhysicsLaw.FLUID_DYNAMICS)) {
                String itemName = event.getItemStack().getItem().toString();
                if (itemName.contains("bucket")) {
                    event.setCanceled(true);
                    player.displayClientMessage(
                            Component.literal("§c" + PhysicsLaw.FLUID_DYNAMICS.getDisplayName() +
                                    " §7is required to use buckets!"),
                            true
                    );
                }
            }
        }
    }

    /**
     * FLUID_DYNAMICS LAW: Block bucket use in air (right click without block)
     */
    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        PlayerProgression prog = ProgressionManager.get(player);

        if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
            if (!PhysicsManager.isUnlocked(player, PhysicsLaw.FLUID_DYNAMICS)) {
                String itemName = event.getItemStack().getItem().toString();
                if (itemName.contains("bucket") && !itemName.contains("empty")) {
                    event.setCanceled(true);
                    player.displayClientMessage(
                            Component.literal("§c" + PhysicsLaw.FLUID_DYNAMICS.getDisplayName() +
                                    " §7is required to place liquids!"),
                            true
                    );
                }
            }
        }
    }

    /**
     * GRAVITY LAW: Warn when placing gravity-affected blocks
     */
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        PlayerProgression prog = ProgressionManager.get(player);

        if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
            if (!PhysicsManager.isUnlocked(player, PhysicsLaw.GRAVITY)) {
                BlockState state = event.getPlacedBlock();

                if (state.getBlock() instanceof FallingBlock) {
                    if (player.getRandom().nextFloat() < 0.5f) {
                        player.displayClientMessage(
                                Component.literal("§e⚠ This block should fall, but gravity doesn't exist yet!"),
                                true
                        );
                    }
                }
            }
        }
    }

    /**
     * THERMODYNAMICS LAW: Prevent fire placement
     */
    @SubscribeEvent
    public void onFirePlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        PlayerProgression prog = ProgressionManager.get(player);

        if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
            if (!PhysicsManager.isUnlocked(player, PhysicsLaw.THERMODYNAMICS)) {
                BlockState state = event.getPlacedBlock();

                if (state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)) {
                    event.setCanceled(true);
                    player.displayClientMessage(
                            Component.literal("§c" + PhysicsLaw.THERMODYNAMICS.getDisplayName() +
                                    " §7is required to create fire!"),
                            true
                    );
                }
            }
        }
    }
}