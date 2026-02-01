package com.example.layerbylayer.progression;

import com.example.layerbylayer.LayerByLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.CommandEvent;

/**
 * Blocks gamemode command until 3D
 */
public class CommandBlocker {

    @SubscribeEvent
    public void onCommand(CommandEvent event) {
        String command = event.getParseResults().getReader().getString().toLowerCase();

        // Block /gamemode command
        if (command.startsWith("gamemode") || command.startsWith("/gamemode")) {
            if (event.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayer player) {
                PlayerProgression prog = ProgressionManager.get(player);

                if (prog.getCurrentLevel() != PlayerProgression.DimensionLevel.THREE_D) {
                    event.setCanceled(true);
                    player.displayClientMessage(
                            Component.literal("§c§lGamemode is locked until you reach 3D Reality!"),
                            false
                    );
                    LayerByLayer.LOGGER.info("Blocked gamemode command for {} (Level: {})",
                            player.getName().getString(),
                            prog.getCurrentLevel());
                }
            }
        }
    }
}