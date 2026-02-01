package com.example.layerbylayer.progression;

import com.example.layerbylayer.LayerByLayer;
import com.example.layerbylayer.dimension.DimensionManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.GameType;

public class PlayerProgression {

    public enum DimensionLevel {
        ZERO_D,   // Point - cannot move
        ONE_D,    // Line - only forward-backward
        TWO_D,    // Plane - X and Z
        THREE_D   // Full 3D
    }

    private DimensionLevel currentLevel = DimensionLevel.ZERO_D;
    private int impulses = 0;

    private static final int IMPULSES_FOR_1D = 10;
    private static final int IMPULSES_FOR_2D = 50;
    private static final int IMPULSES_FOR_3D = 100;

    public void addImpulse(ServerPlayer player) {
        impulses++;

        // Visual and audio feedback
        ServerLevel level = player.serverLevel();

        // Particles
        level.sendParticles(
                ParticleTypes.END_ROD,
                player.getX(), player.getY() + 1, player.getZ(),
                5, 0.3, 0.3, 0.3, 0.05
        );

        // Sound
        level.playSound(null, player.blockPosition(),
                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS,
                0.3f, 1.5f + (impulses % 10) * 0.1f);

        LayerByLayer.LOGGER.info("Player {} generated impulse. Total: {}",
                player.getName().getString(), impulses);

        checkUnlock(player);
    }

    private void checkUnlock(ServerPlayer player) {
        DimensionLevel oldLevel = currentLevel;

        if (currentLevel == DimensionLevel.ZERO_D && impulses >= IMPULSES_FOR_1D) {
            currentLevel = DimensionLevel.ONE_D;
        } else if (currentLevel == DimensionLevel.ONE_D && impulses >= IMPULSES_FOR_2D) {
            currentLevel = DimensionLevel.TWO_D;
        } else if (currentLevel == DimensionLevel.TWO_D && impulses >= IMPULSES_FOR_3D) {
            currentLevel = DimensionLevel.THREE_D;
        }

        if (oldLevel != currentLevel) {
            onUnlock(player);
        }
    }

    private void onUnlock(ServerPlayer player) {
        ServerLevel level = player.serverLevel();

        // Unlock effects
        level.playSound(null, player.blockPosition(),
                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS,
                1.0f, 0.8f + currentLevel.ordinal() * 0.2f);

        // Big particle effect
        level.sendParticles(
                ParticleTypes.FIREWORK,
                player.getX(), player.getY() + 1, player.getZ(),
                50, 1.0, 1.0, 1.0, 0.2
        );

        String message = switch (currentLevel) {
            case ONE_D -> "§6§l⚡ GEOMETRY UNLOCKED!\n§eYou can now move in 1 dimension.";
            case TWO_D -> "§6§l⚡ PLANE UNLOCKED!\n§eYou can now move in 2 dimensions.";
            case THREE_D -> {
                // Teleport to overworld when unlocking 3D
                DimensionManager.teleportToOverworld(player);

                player.setGameMode(GameType.SURVIVAL);

                yield "§a§l✓ REALITY UNLOCKED!\n§aFull 3D movement enabled. Welcome to existence!";
            }
            default -> "";
        };

        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(message),
                false
        );

        LayerByLayer.LOGGER.info("Player {} unlocked level: {}",
                player.getName().getString(), currentLevel.name());
    }

    public void tick(ServerPlayer player) {
        applyRestrictions(player);
    }

    private void applyRestrictions(ServerPlayer player) {
        if (currentLevel == DimensionLevel.ZERO_D) {
            // Freeze completely - point in space
            player.setPos(VOID_X + 0.5, VOID_Y + 1, VOID_Z + 0.5);
            player.setDeltaMovement(0, 0, 0);
            player.setNoGravity(true);
            player.hurtMarked = true;

        } else if (currentLevel == DimensionLevel.ONE_D) {
            // Only X axis movement - FIXED
            double x = player.getX();
            double currentY = player.getY();
            double currentZ = player.getZ();

            // Force Z to stay at 0.5
            player.setPos(x, VOID_Y + 1, VOID_Z + 0.5);

            // Only allow X movement
            player.setDeltaMovement(player.getDeltaMovement().x, 0, 0);
            player.setNoGravity(true);
            player.hurtMarked = true;

        } else if (currentLevel == DimensionLevel.TWO_D) {
            // X and Z axes (horizontal plane)
            double x = player.getX();
            double z = player.getZ();
            player.setPos(x, VOID_Y + 1, z);
            player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);
            player.setNoGravity(true);
            player.hurtMarked = true;

        } else if (currentLevel == DimensionLevel.THREE_D) {
            // Full freedom - restore gravity
            player.setNoGravity(false);
        }
    }

    // Void platform coordinates
    private static final int VOID_X = 0;
    private static final int VOID_Y = 250;
    private static final int VOID_Z = 0;

    public DimensionLevel getCurrentLevel() {
        return currentLevel;
    }

    public int getImpulses() {
        return impulses;
    }

    public void setLevel(DimensionLevel level) {
        this.currentLevel = level;
    }

    public void reset() {
        this.currentLevel = DimensionLevel.ZERO_D;
        this.impulses = 0;
    }

    // Serialization
    public void serialize(CompoundTag tag) {
        tag.putString("Level", currentLevel.name());
        tag.putInt("Impulses", impulses);
    }

    public void deserialize(CompoundTag tag) {
        if (tag.contains("Level")) {
            try {
                currentLevel = DimensionLevel.valueOf(tag.getString("Level"));
            } catch (IllegalArgumentException e) {
                currentLevel = DimensionLevel.ZERO_D;
            }
        }
        if (tag.contains("Impulses")) {
            impulses = tag.getInt("Impulses");
        }
    }
}