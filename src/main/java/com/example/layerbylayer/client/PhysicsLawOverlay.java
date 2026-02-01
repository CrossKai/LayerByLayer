package com.example.layerbylayer.client;

import com.example.layerbylayer.physics.PhysicsLaw;
import com.example.layerbylayer.physics.PhysicsManager;
import com.example.layerbylayer.progression.PlayerProgression;
import com.example.layerbylayer.progression.ProgressionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

/**
 * Displays active physics laws on screen
 */
public class PhysicsLawOverlay {

    private final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public void onRenderOverlay(RenderGuiLayerEvent.Post event) {
        LocalPlayer player = mc.player;
        if (player == null) return;

        PlayerProgression prog = ProgressionManager.get(player);

        // Only show in 3D mode
        if (prog.getCurrentLevel() != PlayerProgression.DimensionLevel.THREE_D) {
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();
        Font font = mc.font;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int x = screenWidth - 150;
        int y = 10;

        // Title
        String title = "§7Physics Laws:";
        graphics.drawString(font, title, x, y, 0xFFFFFF);
        y += 12;

        // Laws status
        int unlocked = 0;
        for (PhysicsLaw law : PhysicsLaw.values()) {
            boolean isUnlocked = PhysicsManager.isUnlocked(player, law);
            if (isUnlocked) unlocked++;

            String status = isUnlocked ? "§a✓" : "§c✗";
            String name = isUnlocked ?
                    "§a" + law.getId() :
                    "§8" + law.getId();

            graphics.drawString(font, status + " " + name, x, y, 0xFFFFFF);
            y += 10;
        }

        // Total
        y += 2;
        String total = "§7Total: §f" + unlocked + "§7/§f" + PhysicsLaw.values().length;
        graphics.drawString(font, total, x, y, 0xFFFFFF);

        // Warning if not all unlocked
        if (unlocked < PhysicsLaw.values().length) {
            y += 12;
            String warning = "§c⚠ Reality Broken";
            graphics.drawString(font, warning, x, y, 0xFF5555);
        } else {
            y += 12;
            String complete = "§a✓ Reality OK";
            graphics.drawString(font, complete, x, y, 0x55FF55);
        }
    }
}