package com.example.layerbylayer.progression;

import com.example.layerbylayer.LayerByLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Forces Adventure mode until 3D is unlocked
 */
public class GameModeRestrictor {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerProgression prog = ProgressionManager.get(player);

            // Force Adventure mode if not in 3D
            if (prog.getCurrentLevel() != PlayerProgression.DimensionLevel.THREE_D) {
                player.setGameMode(GameType.ADVENTURE);
                LayerByLayer.LOGGER.info("Forced {} to Adventure mode (Level: {})",
                        player.getName().getString(),
                        prog.getCurrentLevel());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerProgression prog = ProgressionManager.get(player);

            // Force Adventure mode on respawn
            if (prog.getCurrentLevel() != PlayerProgression.DimensionLevel.THREE_D) {
                player.setGameMode(GameType.ADVENTURE);
            }
        }
    }
}