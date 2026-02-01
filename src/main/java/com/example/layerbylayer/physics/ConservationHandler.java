package com.example.layerbylayer.physics;

import com.example.layerbylayer.progression.PlayerProgression;
import com.example.layerbylayer.progression.ProgressionManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

/**
 * Prevents items from spawning without Conservation law
 */
public class ConservationHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemSpawn(EntityJoinLevelEvent event) {
        // Only handle ItemEntity spawns
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) return;

        // Only on server side
        if (event.getLevel().isClientSide()) return;

        // Find the nearest player (who likely caused this drop)
        Player nearestPlayer = event.getLevel().getNearestPlayer(
                itemEntity.getX(),
                itemEntity.getY(),
                itemEntity.getZ(),
                10.0, // Search radius
                false
        );

        if (nearestPlayer instanceof ServerPlayer player) {
            PlayerProgression prog = ProgressionManager.get(player);

            // In 3D mode, check if Conservation is unlocked
            if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
                if (!PhysicsManager.isUnlocked(player, PhysicsLaw.CONSERVATION)) {
                    // Cancel the item spawn - it vanishes into the void
                    event.setCanceled(true);
                }
            }
        }
    }
}