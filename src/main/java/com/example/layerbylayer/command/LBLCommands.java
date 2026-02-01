package com.example.layerbylayer.command;

import com.example.layerbylayer.dimension.DimensionManager;
import com.example.layerbylayer.physics.PhysicsLaw;
import com.example.layerbylayer.physics.PhysicsManager;
import com.example.layerbylayer.progression.PlayerProgression;
import com.example.layerbylayer.progression.ProgressionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class LBLCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("lbl")
                        .requires(source -> source.hasPermission(2))
                        // Dimension commands
                        .then(Commands.literal("void")
                                .executes(LBLCommands::teleportToVoid))
                        .then(Commands.literal("spawn")
                                .executes(LBLCommands::teleportToSpawn))

                        // Progression commands
                        .then(Commands.literal("reset")
                                .executes(LBLCommands::resetProgress))
                        .then(Commands.literal("setlevel")
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, 3))
                                        .executes(LBLCommands::setLevel)))
                        .then(Commands.literal("addimpulses")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(LBLCommands::addImpulses)))

                        // Physics commands
                        .then(Commands.literal("physics")
                                .then(Commands.literal("unlock")
                                        .then(Commands.argument("law", StringArgumentType.string())
                                                .executes(LBLCommands::unlockPhysics)))
                                .then(Commands.literal("lock")
                                        .then(Commands.argument("law", StringArgumentType.string())
                                                .executes(LBLCommands::lockPhysics)))
                                .then(Commands.literal("unlockall")
                                        .executes(LBLCommands::unlockAllPhysics))
                                .then(Commands.literal("reset")
                                        .executes(LBLCommands::resetPhysics))
                                .then(Commands.literal("list")
                                        .executes(LBLCommands::listPhysics)))

                        // Info command
                        .then(Commands.literal("info")
                                .executes(LBLCommands::showInfo))
        );
    }

    private static int teleportToVoid(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            DimensionManager.teleportToVoid(player);
            return 1;
        }
        return 0;
    }

    private static int teleportToSpawn(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            DimensionManager.teleportToOverworld(player);
            return 1;
        }
        return 0;
    }

    private static int resetProgress(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            PlayerProgression prog = ProgressionManager.get(player);
            prog.reset();
            player.displayClientMessage(
                    Component.literal("§e§lProgress reset! Reality collapsed."), false);
            DimensionManager.teleportToVoid(player);
            return 1;
        }
        return 0;
    }

    private static int setLevel(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            int level = IntegerArgumentType.getInteger(context, "level");
            PlayerProgression prog = ProgressionManager.get(player);

            PlayerProgression.DimensionLevel newLevel = switch (level) {
                case 0 -> PlayerProgression.DimensionLevel.ZERO_D;
                case 1 -> PlayerProgression.DimensionLevel.ONE_D;
                case 2 -> PlayerProgression.DimensionLevel.TWO_D;
                case 3 -> PlayerProgression.DimensionLevel.THREE_D;
                default -> PlayerProgression.DimensionLevel.ZERO_D;
            };

            prog.setLevel(newLevel);
            player.displayClientMessage(
                    Component.literal("§a§lLevel set to: " + newLevel.name()), false);
            return 1;
        }
        return 0;
    }

    private static int addImpulses(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            int amount = IntegerArgumentType.getInteger(context, "amount");
            PlayerProgression prog = ProgressionManager.get(player);

            for (int i = 0; i < amount; i++) {
                prog.addImpulse(player);
            }

            player.displayClientMessage(
                    Component.literal("§a§lAdded " + amount + " impulses!"), false);
            return 1;
        }
        return 0;
    }

    private static int unlockPhysics(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            String lawName = StringArgumentType.getString(context, "law").toUpperCase();

            try {
                PhysicsLaw law = PhysicsLaw.valueOf(lawName);
                PhysicsManager.unlock(player, law);
                return 1;
            } catch (IllegalArgumentException e) {
                player.displayClientMessage(
                        Component.literal("§c§lInvalid law name! Available: GRAVITY, COLLISION, CONSERVATION, FLUID_DYNAMICS, THERMODYNAMICS"),
                        false);
                return 0;
            }
        }
        return 0;
    }

    private static int lockPhysics(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            String lawName = StringArgumentType.getString(context, "law").toUpperCase();

            try {
                PhysicsLaw law = PhysicsLaw.valueOf(lawName);
                PhysicsManager.lock(player, law);
                player.displayClientMessage(
                        Component.literal("§c§l" + law.getDisplayName() + " has been locked!"),
                        false);
                return 1;
            } catch (IllegalArgumentException e) {
                player.displayClientMessage(
                        Component.literal("§c§lInvalid law name!"),
                        false);
                return 0;
            }
        }
        return 0;
    }

    private static int unlockAllPhysics(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            for (PhysicsLaw law : PhysicsLaw.values()) {
                PhysicsManager.unlock(player, law);
            }
            return 1;
        }
        return 0;
    }

    private static int resetPhysics(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            PhysicsManager.reset(player);
            return 1;
        }
        return 0;
    }

    private static int listPhysics(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            player.displayClientMessage(Component.literal("§7§m                                    "), false);
            player.displayClientMessage(Component.literal("§e§lPhysics Laws Status"), false);

            for (PhysicsLaw law : PhysicsLaw.values()) {
                boolean unlocked = PhysicsManager.isUnlocked(player, law);
                String status = unlocked ? "§a✓ ACTIVE" : "§c✗ INACTIVE";
                player.displayClientMessage(
                        Component.literal(status + " §7- " + law.getDisplayName()),
                        false);
            }

            player.displayClientMessage(Component.literal("§7§m                                    "), false);
            return 1;
        }
        return 0;
    }

    private static int showInfo(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            PlayerProgression prog = ProgressionManager.get(player);

            player.displayClientMessage(Component.literal("§7§m                                    "), false);
            player.displayClientMessage(Component.literal("§e§lLayer by Layer - Progress Info"), false);
            player.displayClientMessage(Component.literal("§7Current Level: §f" + prog.getCurrentLevel().name()), false);
            player.displayClientMessage(Component.literal("§7Impulses: §f" + prog.getImpulses()), false);
            player.displayClientMessage(Component.literal("§7Physics Laws: §f" +
                    PhysicsManager.getUnlockedCount(player) + "/" + PhysicsLaw.values().length), false);
            player.displayClientMessage(Component.literal("§7§m                                    "), false);
            return 1;
        }
        return 0;
    }
}