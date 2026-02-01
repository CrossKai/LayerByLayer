package com.example.layerbylayer.physics;

import com.example.layerbylayer.progression.PlayerProgression;
import com.example.layerbylayer.progression.ProgressionManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

/**
 * More precise control over block drops
 */
public class BlockDropHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockDrops(BlockDropsEvent event) {
        // Get the breaker
        if (!(event.getBreaker() instanceof ServerPlayer player)) return;

        PlayerProgression prog = ProgressionManager.get(player);

        // Only in 3D mode
        if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
            if (!PhysicsManager.isUnlocked(player, PhysicsLaw.CONSERVATION)) {
                // Clear all drops - items vanish
                event.getDrops().clear();
            }
        }
    }
}