package com.example.layerbylayer.physics;

import com.example.layerbylayer.LayerByLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages physics laws for each player
 */
public class PhysicsManager {

    private static final Map<UUID, EnumSet<PhysicsLaw>> playerLaws = new HashMap<>();
    private static final String NBT_KEY = "PhysicsLaws";

    /**
     * Get unlocked laws for a player
     */
    public static EnumSet<PhysicsLaw> getLaws(Player player) {
        return playerLaws.computeIfAbsent(player.getUUID(),
                uuid -> EnumSet.noneOf(PhysicsLaw.class));
    }

    /**
     * Check if a specific law is unlocked
     */
    public static boolean isUnlocked(Player player, PhysicsLaw law) {
        return getLaws(player).contains(law);
    }

    /**
     * Unlock a physics law
     */
    public static void unlock(ServerPlayer player, PhysicsLaw law) {
        EnumSet<PhysicsLaw> laws = getLaws(player);
        if (!laws.contains(law)) {
            laws.add(law);
            onLawUnlocked(player, law);
            save(player);
        }
    }

    /**
     * Lock a physics law (for testing/admin commands)
     */
    public static void lock(ServerPlayer player, PhysicsLaw law) {
        EnumSet<PhysicsLaw> laws = getLaws(player);
        laws.remove(law);
        save(player);
    }

    /**
     * Reset all laws
     */
    public static void reset(ServerPlayer player) {
        getLaws(player).clear();
        save(player);
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(
                        "§c§lAll physics laws have been revoked! Reality is broken."),
                false
        );
    }

    /**
     * Check if all laws are unlocked
     */
    public static boolean areAllUnlocked(Player player) {
        return getLaws(player).size() == PhysicsLaw.values().length;
    }

    /**
     * Get number of unlocked laws
     */
    public static int getUnlockedCount(Player player) {
        return getLaws(player).size();
    }

    /**
     * Called when a law is unlocked
     */
    private static void onLawUnlocked(ServerPlayer player, PhysicsLaw law) {
        // Visual effects
        player.serverLevel().sendParticles(
                net.minecraft.core.particles.ParticleTypes.FLASH,
                player.getX(), player.getY() + 1, player.getZ(),
                1, 0, 0, 0, 0
        );

        player.serverLevel().sendParticles(
                net.minecraft.core.particles.ParticleTypes.END_ROD,
                player.getX(), player.getY() + 1, player.getZ(),
                50, 0.5, 0.5, 0.5, 0.3
        );

        // Sound
        player.playNotifySound(
                net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                net.minecraft.sounds.SoundSource.PLAYERS,
                1.0f, 1.5f
        );

        // Message
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(
                        "§e§l⚡ PHYSICS LAW ACTIVATED!\n" +
                                law.getDisplayName() + "\n§7" +
                                law.getDescription()
                ),
                false
        );

        LayerByLayer.LOGGER.info("Player {} unlocked physics law: {}",
                player.getName().getString(), law.getId());

        // Check if all laws unlocked
        if (areAllUnlocked(player)) {
            onAllLawsUnlocked(player);
        }
    }

    /**
     * Called when all laws are unlocked
     */
    private static void onAllLawsUnlocked(ServerPlayer player) {
        player.serverLevel().sendParticles(
                net.minecraft.core.particles.ParticleTypes.FIREWORK,
                player.getX(), player.getY() + 1, player.getZ(),
                100, 1.0, 1.0, 1.0, 0.3
        );

        player.playNotifySound(
                net.minecraft.sounds.SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                net.minecraft.sounds.SoundSource.PLAYERS,
                1.0f, 1.0f
        );

        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(
                        "§a§l✓ REALITY RESTORED!\n" +
                                "§aAll physics laws are now active.\n" +
                                "§aThe world functions as it should."
                ),
                false
        );

        LayerByLayer.LOGGER.info("Player {} has unlocked all physics laws!",
                player.getName().getString());
    }

    /**
     * Save laws to player data
     */
    private static void save(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        CompoundTag modData = data.getCompound(LayerByLayer.MOD_ID);

        CompoundTag lawsTag = new CompoundTag();
        for (PhysicsLaw law : getLaws(player)) {
            lawsTag.putBoolean(law.getId(), true);
        }

        modData.put(NBT_KEY, lawsTag);
        data.put(LayerByLayer.MOD_ID, modData);
    }

    /**
     * Load laws from player data
     */
    private static void load(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        CompoundTag modData = data.getCompound(LayerByLayer.MOD_ID);

        EnumSet<PhysicsLaw> laws = EnumSet.noneOf(PhysicsLaw.class);

        if (modData.contains(NBT_KEY)) {
            CompoundTag lawsTag = modData.getCompound(NBT_KEY);
            for (PhysicsLaw law : PhysicsLaw.values()) {
                if (lawsTag.getBoolean(law.getId())) {
                    laws.add(law);
                }
            }
        }

        playerLaws.put(player.getUUID(), laws);

        LayerByLayer.LOGGER.info("Loaded {} physics laws for {}",
                laws.size(), player.getName().getString());
    }

    // Event handlers
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            load(player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            save(player);
            playerLaws.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath() && event.getEntity() instanceof ServerPlayer player) {
            EnumSet<PhysicsLaw> oldLaws = playerLaws.get(event.getOriginal().getUUID());
            if (oldLaws != null) {
                playerLaws.put(player.getUUID(), EnumSet.copyOf(oldLaws));
            }
        }
    }
}