package com.example.layerbylayer.client;

import java.util.HashMap;
import java.util.Map;

public class ImpulseVisualizer {
    private static final int[] MILESTONE_IMPULSES = {3, 5, 9, 10};
    private Map<Integer, String> soundEffects;
    private int currentImpulse;

    public ImpulseVisualizer() {
        soundEffects = new HashMap<>();
        initializeSoundEffects();
        currentImpulse = 0;
    }

    private void initializeSoundEffects() {
        soundEffects.put(3, "sound_effect_milestone3.wav");
        soundEffects.put(5, "sound_effect_milestone5.wav");
        soundEffects.put(9, "sound_effect_milestone9.wav");
        soundEffects.put(10, "sound_effect_milestone10.wav");
    }

    public void receiveImpulse(int impulse) {
        currentImpulse = impulse;
        manageVisualEffects();
        playSoundFeedback();
        spawnParticles();
    }

    private void manageVisualEffects() {
        if (isMilestone(currentImpulse)) {
            // Implement visual effects for milestone impulses
            System.out.println("Visual effect triggered for impulse " + currentImpulse);
        }
    }

    private void playSoundFeedback() {
        if (soundEffects.containsKey(currentImpulse)) {
            String soundFile = soundEffects.get(currentImpulse);
            // Logic to play the sound
            System.out.println("Playing sound: " + soundFile);
        }
    }

    private void spawnParticles() {
        // Logic to spawn particles based on currentImpulse
        System.out.println("Spawning particles for impulse " + currentImpulse);
    }

    private boolean isMilestone(int impulse) {
        for (int milestone : MILESTONE_IMPULSES) {
            if (impulse == milestone) {
                return true;
            }
        }
        return false;
    }
}