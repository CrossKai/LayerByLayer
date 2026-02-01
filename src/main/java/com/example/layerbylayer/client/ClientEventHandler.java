package com.example.layerbylayer.client;

import com.example.layerbylayer.network.ImpulsePacket;
import com.example.layerbylayer.progression.PlayerProgression;
import com.example.layerbylayer.progression.ProgressionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class ClientEventHandler {

    private final Minecraft mc = Minecraft.getInstance();
    private final ImpulseVisualizer impulseVisualizer = new ImpulseVisualizer();
    private final LoreManager loreManager = new LoreManager();
    private float smoothProgress = 0f;
    private float pulseAnimation = 0f;
    private int lastImpulses = 0;

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        LocalPlayer player = mc.player;
        if (player == null) return;

        PlayerProgression prog = ProgressionManager.get(player);

        // Allow impulse generation before reaching 3D
        if (prog.getCurrentLevel() != PlayerProgression.DimensionLevel.THREE_D) {
            if (event.getKey() == GLFW.GLFW_KEY_SPACE &&
                    event.getAction() == GLFW.GLFW_PRESS) {
                PacketDistributor.sendToServer(new ImpulsePacket());
                pulseAnimation = 1.0f;

                // Client-side visual feedback
                impulseVisualizer.onImpulseGenerated(prog.getImpulses(), player);
                loreManager.checkAndDisplayLore(prog.getImpulses());
            }
        }
    }

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseButton.Pre event) {
        LocalPlayer player = mc.player;
        if (player == null) return;

        PlayerProgression prog = ProgressionManager.get(player);

        // Allow impulse generation before reaching 3D
        if (prog.getCurrentLevel() != PlayerProgression.DimensionLevel.THREE_D) {
            if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT &&
                    event.getAction() == GLFW.GLFW_PRESS) {
                PacketDistributor.sendToServer(new ImpulsePacket());
                pulseAnimation = 1.0f;

                // Client-side visual feedback
                impulseVisualizer.onImpulseGenerated(prog.getImpulses(), player);
                loreManager.checkAndDisplayLore(prog.getImpulses());
            }
        }
    }

    @SubscribeEvent
    public void onRenderFog(ViewportEvent.RenderFog event) {
        LocalPlayer player = mc.player;
        if (player == null) return;

        PlayerProgression prog = ProgressionManager.get(player);

        // Reduce fog distance in non-3D states for void effect
        if (prog.getCurrentLevel() != PlayerProgression.DimensionLevel.THREE_D) {
            float distance = switch (prog.getCurrentLevel()) {
                case ZERO_D -> 5.0f;
                case ONE_D -> 15.0f;
                case TWO_D -> 30.0f;
                default -> event.getFarPlaneDistance();
            };

            event.setNearPlaneDistance(0.5f);
            event.setFarPlaneDistance(distance);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGuiLayerEvent.Post event) {
        LocalPlayer player = mc.player;
        if (player == null) return;

        PlayerProgression prog = ProgressionManager.get(player);
        GuiGraphics graphics = event.getGuiGraphics();
        Font font = mc.font;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // Update pulse animation
        if (pulseAnimation > 0) {
            pulseAnimation -= 0.05f;
        }

        // Check for progression changes
        if (prog.getImpulses() != lastImpulses) {
            loreManager.checkAndDisplayLore(prog.getImpulses());
            lastImpulses = prog.getImpulses();
        }

        // 0D State: Full screen overlay
        if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.ZERO_D) {
            renderZeroDimensionOverlay(graphics, screenWidth, screenHeight);
        } else {
            // Reset lore when leaving 0D
            loreManager.reset();
        }

        // Progress information
        String levelText = "§7Reality State: §f" + getLevelName(prog.getCurrentLevel());
        String impulsesText = "§7Impulses: §e" + prog.getImpulses();

        graphics.drawString(font, levelText, 10, 10, 0xFFFFFF);
        graphics.drawString(font, impulsesText, 10, 22, 0xFFFFFF);

        // Hints
        renderHints(graphics, font, prog, screenWidth, screenHeight);

        // Progress bar
        drawProgressBar(graphics, prog, screenWidth, screenHeight);

        // Render lore message
        loreManager.renderLoreMessage(graphics, screenWidth, screenHeight);
    }

    private void renderZeroDimensionOverlay(GuiGraphics graphics, int width, int height) {
        // Pulsing white overlay
        float alpha = 0.8f + (Mth.sin(System.currentTimeMillis() / 1000f) * 0.1f);
        int alphaInt = (int) (alpha * 255);
        int color = (alphaInt << 24) | 0xFFFFFF;

        graphics.fill(0, 0, width, height, color);

        // Center text
        String title = "§f§lTHE VOID";
        String subtitle = "§7Press §fSPACE §7or §fCLICK §7to create reality";

        int titleWidth = mc.font.width(title);
        int subtitleWidth = mc.font.width(subtitle);

        graphics.drawString(mc.font, title,
                (width - titleWidth) / 2,
                (height / 2) - 20,
                0xFFFFFF);
        graphics.drawString(mc.font, subtitle,
                (width - subtitleWidth) / 2,
                (height / 2) + 10,
                0xAAAAAA);
    }

    private void renderHints(GuiGraphics graphics, Font font, PlayerProgression prog, int width, int height) {
        String hint = switch (prog.getCurrentLevel()) {
            case ZERO_D -> "§e§lPress SPACE or LEFT CLICK to generate impulses (10 needed)";
            case ONE_D -> "§a§l1D Mode: Move forward/backward only (50 impulses for 2D)";
            case TWO_D -> "§b§l2D Mode: Move on XZ plane (100 impulses for 3D)";
            case THREE_D -> "§a§l✓ Full 3D Reality Unlocked! Welcome to existence.";
        };

        int hintWidth = font.width(hint);
        graphics.drawString(font, hint,
                (width - hintWidth) / 2,
                height - 40,
                0xFFFFFF);
    }

    private void drawProgressBar(GuiGraphics graphics, PlayerProgression prog, int width, int height) {
        if (prog.getCurrentLevel() == PlayerProgression.DimensionLevel.THREE_D) {
            return;
        }

        int barWidth = 300;
        int barHeight = 20;
        int x = (width - barWidth) / 2;
        int y = height - 70;

        int target = switch (prog.getCurrentLevel()) {
            case ZERO_D -> 10;
            case ONE_D -> 50;
            case TWO_D -> 100;
            default -> 100;
        };

        float targetProgress = Math.min((float) prog.getImpulses() / target, 1.0f);
        smoothProgress += (targetProgress - smoothProgress) * 0.1f;

        graphics.fill(x - 2, y - 2, x + barWidth + 2, y + barHeight + 2, 0xFF000000);
        graphics.fill(x, y, x + barWidth, y + barHeight, 0xFF333333);

        int filledWidth = (int) (smoothProgress * barWidth);
        int color = switch (prog.getCurrentLevel()) {
            case ZERO_D -> 0xFFFFFF00;
            case ONE_D -> 0xFF00FF00;
            case TWO_D -> 0xFF00FFFF;
            default -> 0xFFFFFFFF;
        };

        if (pulseAnimation > 0) {
            int pulseColor = (int) (pulseAnimation * 255);
            color = (color & 0x00FFFFFF) | (Math.min(255, (color >> 24) + pulseColor) << 24);
        }

        graphics.fill(x, y, x + filledWidth, y + barHeight, color);

        if (smoothProgress > 0.99f) {
            int glowAlpha = (int) (128 + Mth.sin(System.currentTimeMillis() / 200f) * 64);
            int glowColor = (glowAlpha << 24) | (color & 0x00FFFFFF);
            graphics.fill(x - 3, y - 3, x + barWidth + 3, y + barHeight + 3, glowColor);
        }

        int currentImpulses = Math.min(prog.getImpulses(), target);
        String progressText = "§f§l" + currentImpulses + " §7/ §f§l" + target;
        int textWidth = mc.font.width(progressText);
        graphics.drawString(mc.font, progressText,
                x + (barWidth - textWidth) / 2,
                y + (barHeight - 8) / 2,
                0xFFFFFF);
    }

    private String getLevelName(PlayerProgression.DimensionLevel level) {
        return switch (level) {
            case ZERO_D -> "0D (Point)";
            case ONE_D -> "1D (Line)";
            case TWO_D -> "2D (Plane)";
            case THREE_D -> "3D (Reality)";
        };
    }
}