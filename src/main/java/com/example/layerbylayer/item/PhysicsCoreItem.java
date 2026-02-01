package com.example.layerbylayer.item;

import com.example.layerbylayer.physics.PhysicsLaw;
import com.example.layerbylayer.physics.PhysicsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Core item that unlocks a physics law when used
 */
public class PhysicsCoreItem extends Item {

    private final PhysicsLaw law;

    // ВАЖНО: Конструктор теперь принимает Properties снаружи
    public PhysicsCoreItem(PhysicsLaw law, Properties properties) {
        super(properties
                .stacksTo(1)
                .fireResistant()
        );
        this.law = law;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (PhysicsManager.isUnlocked(player, law)) {
                // Already unlocked
                player.displayClientMessage(
                        Component.literal("§e" + law.getDisplayName() + " §7is already active!"),
                        true
                );
                return InteractionResult.FAIL;
            } else {
                // Unlock the law
                PhysicsManager.unlock(serverPlayer, law);

                // Consume the item
                if (!player.isCreative()) {
                    stack.shrink(1);
                }

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal(""));
        tooltipComponents.add(Component.literal(law.getDisplayName()).withStyle(law.getFormatting()));
        tooltipComponents.add(Component.literal(""));
        tooltipComponents.add(Component.literal(law.getDescription()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.literal(""));
        tooltipComponents.add(Component.literal("§eRight-click to activate").withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));
        tooltipComponents.add(Component.literal(""));
        tooltipComponents.add(law.getComponent().append(Component.literal(" will be permanently unlocked")).withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Glowing effect
    }

    public PhysicsLaw getLaw() {
        return law;
    }
}