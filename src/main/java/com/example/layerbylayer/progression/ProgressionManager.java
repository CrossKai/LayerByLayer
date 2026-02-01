package com.example.layerbylayer.progression;

import com.example.layerbylayer.LayerByLayer;
import com.example.layerbylayer.dimension.DimensionManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProgressionManager {

    private static final Map<UUID, PlayerProgression> progressions = new HashMap<>();
    private static final Map<UUID, Integer> teleportQueue = new HashMap<>();

    public static PlayerProgression get(Player player) {
        return progressions.computeIfAbsent(player.getUUID(),
                uuid -> new PlayerProgression());
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            load(player);

            PlayerProgression prog = get(player);

            // Queue teleportation to void if not in 3D (after 1 second = 20 ticks)
            if (prog.getCurrentLevel() != PlayerProgression.DimensionLevel.THREE_D) {
                teleportQueue.put(player.getUUID(), 20);
                LayerByLayer.LOGGER.info("Player {} will be teleported to Void in 20 ticks",
                        player.getName().getString());
            }

            LayerByLayer.LOGGER.info("Loaded progression for {}: Level={}, Impulses={}",
                    player.getName().getString(),
                    prog.getCurrentLevel().name(),
                    prog.getImpulses());
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            save(player);
            progressions.remove(player.getUUID());
            teleportQueue.remove(player.getUUID());

            LayerByLayer.LOGGER.info("Saved and removed progression for {}",
                    player.getName().getString());
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath() && event.getEntity() instanceof ServerPlayer player) {
            // Preserve progression on death
            PlayerProgression oldProg = progressions.get(event.getOriginal().getUUID());
            if (oldProg != null) {
                PlayerProgression newProg = get(player);
                CompoundTag tag = new CompoundTag();
                oldProg.serialize(tag);
                newProg.deserialize(tag);

                LayerByLayer.LOGGER.info("Cloned progression for {} on death",
                        player.getName().getString());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Process teleportation queue
            Integer ticksLeft = teleportQueue.get(player.getUUID());
            if (ticksLeft != null) {
                if (ticksLeft <= 0) {
                    LayerByLayer.LOGGER.info("Teleporting {} to Void now",
                            player.getName().getString());
                    DimensionManager.teleportToVoid(player);
                    teleportQueue.remove(player.getUUID());
                } else {
                    teleportQueue.put(player.getUUID(), ticksLeft - 1);
                }
            }

            // Apply progression restrictions every tick
            get(player).tick(player);
        }
    }

    private void load(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        CompoundTag modData = data.getCompound(LayerByLayer.MOD_ID);

        if (modData.isEmpty()) {
            // New player - create fresh progression
            progressions.put(player.getUUID(), new PlayerProgression());
            LayerByLayer.LOGGER.info("Created new progression for {}",
                    player.getName().getString());
        } else {
            // Load existing progression
            PlayerProgression prog = new PlayerProgression();
            prog.deserialize(modData);
            progressions.put(player.getUUID(), prog);
        }
    }

    private void save(ServerPlayer player) {
        PlayerProgression prog = progressions.get(player.getUUID());
        if (prog != null) {
            CompoundTag data = player.getPersistentData();
            CompoundTag modData = new CompoundTag();
            prog.serialize(modData);
            data.put(LayerByLayer.MOD_ID, modData);

            LayerByLayer.LOGGER.info("Saved progression for {}: Level={}, Impulses={}",
                    player.getName().getString(),
                    prog.getCurrentLevel().name(),
                    prog.getImpulses());
        }
    }
}