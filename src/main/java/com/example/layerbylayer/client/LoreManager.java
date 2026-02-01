package com.example.layerbylayer.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages lore messages and atmospheric dialogue for void stage (0D)
 */
public class LoreManager {

    private static final Map<Integer, String> VOID_STAGE_LORE = new HashMap<>();
    private static final long DISPLAY_DURATION_MS = 3000; // 3 seconds
    private static final long FADE_DURATION_MS = 500; // 0.5 seconds

    static {
        // Initialize lore messages at specific impulse thresholds
        VOID_STAGE_LORE.put(0, "§7§oТы... пробуждаешься?");
        VOID_STAGE_LORE.put(2, "§7§oЧто-то появляется из пустоты...");
        VOID_STAGE_LORE.put(5, "§6§oФорма начинает приобретать смысл...");
        VOID_STAGE_LORE.put(7, "§e§oЭхо пустоты отступает...");
        VOID_STAGE_LORE.put(10, "§a§l✓ ГЕОМЕТРИЯ РАЗБЛОКИРОВАНА\n§7§oВы можете двигаться вперёд.");
    }

    private final Minecraft mc = Minecraft.getInstance();
    private long lastMessageTime = 0;
    private String currentMessage = "";
    private float messageAlpha = 0f;
    private int lastShownImpulse = -1;

    /**
     * Check if a lore message should be displayed at this impulse count
     */
    public void checkAndDisplayLore(int currentImpulse) {
        if (VOID_STAGE_LORE.containsKey(currentImpulse) && currentImpulse != lastShownImpulse) {
            displayLoreMessage(VOID_STAGE_LORE.get(currentImpulse));
            lastShownImpulse = currentImpulse;
        }
    }

    /**
     * Display a lore message with fade-in/fade-out effect
     */
    private void displayLoreMessage(String message) {
        LocalPlayer player = mc.player;
        if (player == null) return;

        currentMessage = message;
        lastMessageTime = System.currentTimeMillis();
        messageAlpha = 0f;

        // Also display in actionbar for emphasis
        player.displayClientMessage(
                Component.literal(message),
                true
        );
    }

    /**
     * Render lore message with fade animation
     */
    public void renderLoreMessage(GuiGraphics graphics, int screenWidth, int screenHeight) {
        if (currentMessage.isEmpty()) return;

        long timeSinceDisplay = System.currentTimeMillis() - lastMessageTime;

        // Calculate alpha based on time
        if (timeSinceDisplay < FADE_DURATION_MS) {
            // Fade in
            messageAlpha = (float) timeSinceDisplay / FADE_DURATION_MS;
        } else if (timeSinceDisplay < DISPLAY_DURATION_MS - FADE_DURATION_MS) {
            // Full display
            messageAlpha = 1f;
        } else if (timeSinceDisplay < DISPLAY_DURATION_MS) {
            // Fade out
            long fadeOutTime = timeSinceDisplay - (DISPLAY_DURATION_MS - FADE_DURATION_MS);
            messageAlpha = 1f - ((float) fadeOutTime / FADE_DURATION_MS);
        } else {
            // Message done
            currentMessage = "";
            messageAlpha = 0f;
            return;
        }

        // Draw message in center of screen
        int color = 0xFFFFFF | ((int) (messageAlpha * 255) << 24);
        int textX = screenWidth / 2;
        int textY = screenHeight / 2 + 30;

        String[] lines = currentMessage.split("\n");
        for (int i = 0; i < lines.length; i++) {
            int lineWidth = mc.font.width(lines[i]);
            graphics.drawString(
                    mc.font,
                    lines[i],
                    textX - lineWidth / 2,
                    textY + (i * 12),
                    color
            );
        }
    }

    /**
     * Reset lore manager (called when entering 1D)
     */
    public void reset() {
        currentMessage = "";
        messageAlpha = 0f;
        lastShownImpulse = -1;
    }

    /**
     * Get lore text for a specific impulse (for tooltips, etc.)
     */
    public String getLoreForImpulse(int impulse) {
        return VOID_STAGE_LORE.getOrDefault(impulse, "");
    }
}