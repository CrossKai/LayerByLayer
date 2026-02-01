package com.example.layerbylayer.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/**
 * Manages all visual and audio feedback for impulse generation in 0D mode
 */
public class ImpulseVisualizer {

    private final Minecraft mc = Minecraft.getInstance();
    private float lastFlashAlpha = 0f;
    private long lastImpulseTime = 0;

    // Milestone impulses with special effects
    private static final int[] MILESTONES = {3, 5, 9, 10};
    private static final String[] MILESTONE_MESSAGES = {
            "§7Геометрия зовёт...",
            "§6Форма обретается...",
            "§cРеальность на пороге...",
            "§a§l✓ 1D РАЗБЛОКИРОВАНА"
    };

    public void onImpulseGenerated(int currentImpulse, LocalPlayer player) {
        lastImpulseTime = System.currentTimeMillis();
        lastFlashAlpha = 1.0f;

        // Always play base effects
        playBaseEffect(player, currentImpulse);

        // Check for milestones
        checkMilestone(player, currentImpulse);
    }

    private void playBaseEffect(LocalPlayer player, int impulse) {
        if (mc.level == null) return;

        double x = player.getX();
        double y = player.getY() + 1;
        double z = player.getZ();

        // Particles: scale with impulse count
        int particleCount = Math.min(5 + impulse, 20);
        float particleSpeed = 0.05f + (impulse * 0.01f);

        // Spawn center particle
        mc.level.addParticle(
                ParticleTypes.END_ROD,
                x, y, z,
                0, 0, 0
        );

        // Spawn surrounding particles
        for (int i = 0; i < particleCount; i++) {
            double vx = (Math.random() - 0.5) * particleSpeed;
            double vy = (Math.random() - 0.5) * particleSpeed;
            double vz = (Math.random() - 0.5) * particleSpeed;

            mc.level.addParticle(
                    ParticleTypes.END_ROD,
                    x + (Math.random() - 0.5) * 0.5,
                    y + (Math.random() - 0.5) * 0.5,
                    z + (Math.random() - 0.5) * 0.5,
                    vx, vy, vz
            );
        }

        // Sound: pitch and volume scale with impulse
        float pitch = 0.8f + (impulse * 0.05f);
        float volume = 0.2f + (impulse * 0.02f);

        mc.level.playSound(
                player,
                x, y, z,
                SoundEvents.EXPERIENCE_ORB_PICKUP,
                SoundSource.PLAYERS,
                volume,
                pitch
        );
    }

    private void checkMilestone(LocalPlayer player, int impulse) {
        if (mc.level == null) return;

        for (int i = 0; i < MILESTONES.length; i++) {
            if (impulse == MILESTONES[i]) {
                playMilestoneEffect(player, i, impulse);
                return;
            }
        }
    }

    private void playMilestoneEffect(LocalPlayer player, int milestoneIndex, int impulse) {
        if (mc.level == null) return;

        double x = player.getX();
        double y = player.getY() + 1;
        double z = player.getZ();

        // Bigger particle burst using loop
        for (int i = 0; i < 30; i++) {
            double vx = (Math.random() - 0.5) * 0.5;
            double vy = (Math.random() - 0.5) * 0.5;
            double vz = (Math.random() - 0.5) * 0.5;

            mc.level.addParticle(
                    ParticleTypes.FIREWORK,
                    x + (Math.random() - 0.5),
                    y + (Math.random() - 0.5),
                    z + (Math.random() - 0.5),
                    vx, vy, vz
            );
        }

        // Stronger sound
        float pitch = 1.0f + (milestoneIndex * 0.2f);
        mc.level.playSound(
                player,
                x, y, z,
                SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                SoundSource.PLAYERS,
                0.8f,
                pitch
        );

        // Display milestone message
        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(MILESTONE_MESSAGES[milestoneIndex]),
                true // actionbar
        );
    }

    public void renderFlashOverlay(float partialTicks) {
        if (lastFlashAlpha <= 0) return;

        // Fade flash out over 0.1 seconds
        long timeSinceImpulse = System.currentTimeMillis() - lastImpulseTime;
        lastFlashAlpha = Math.max(0, 1f - (timeSinceImpulse / 100f));
    }

    public float getFlashAlpha() {
        return lastFlashAlpha;
    }
}