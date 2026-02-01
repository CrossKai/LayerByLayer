package com.example.layerbylayer.physics;

import com.example.layerbylayer.LayerByLayer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum PhysicsLaw {
    GRAVITY(
            "Gravity",
            "§6Law of Gravity",
            "Allows objects to fall naturally",
            "§7Without this GRAVITY law, blocks float in defiance of nature",
            0xFFD700,
            ChatFormatting.GOLD
    ),

    COLLISION(
            "Collision",
            "§bLaw of Collision",
            "Makes matter solid and impenetrable",
            "§7Without this COLLISION law, you phase through the world like a ghost",
            0x00BFFF,
            ChatFormatting.AQUA
    ),

    CONSERVATION(
            "Conservation",
            "§aLaw of Conservation",
            "Preserves matter when blocks are broken",
            "§7Without this CONSERVATION law, destroyed blocks vanish into nothingness",
            0x00FF00,
            ChatFormatting.GREEN
    ),

    FLUID_DYNAMICS(
            "FluidDynamics",
            "§9Law of Fluid Dynamics",
            "Allows liquids to flow and spread",
            "§7Without this FLUID DYNAMICS law, water and lava remain frozen in place",
            0x0000FF,
            ChatFormatting.BLUE
    ),

    THERMODYNAMICS(
            "Thermodynamics",
            "§cLaw of Thermodynamics",
            "Enables heat transfer and combustion",
            "§7Without this THERMODYNAMICS law, furnaces are cold and fires won't spread",
            0xFF4500,
            ChatFormatting.RED
    );

    private final String id;
    private final String displayName;
    private final String description;
    private final String warningText;
    private final int color;
    private final ChatFormatting formatting;

    PhysicsLaw(String id, String displayName, String description, String warningText, int color, ChatFormatting formatting) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.warningText = warningText;
        this.color = color;
        this.formatting = formatting;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getWarningText() {
        return warningText;
    }

    public int getColor() {
        return color;
    }

    public ChatFormatting getFormatting() {
        return formatting;
    }

    public MutableComponent getComponent() {
        return Component.literal(displayName);
    }

    public MutableComponent getDescriptionComponent() {
        return Component.literal(description).withStyle(ChatFormatting.GRAY);
    }

    public String getCoreItemName() {
        return id.toLowerCase() + "_core";
    }

    public String getCoreName() {
        return displayName + " Core";
    }
}