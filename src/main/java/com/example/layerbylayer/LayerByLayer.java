package com.example.layerbylayer;

import com.example.layerbylayer.client.ClientEventHandler;
import com.example.layerbylayer.command.LBLCommands;
import com.example.layerbylayer.network.ImpulsePacket;
import com.example.layerbylayer.physics.*;
import com.example.layerbylayer.progression.CommandBlocker;
import com.example.layerbylayer.progression.GameModeRestrictor;
import com.example.layerbylayer.progression.ProgressionManager;
import com.example.layerbylayer.registry.ModBlocks;
import com.example.layerbylayer.registry.ModCreativeTabs;
import com.example.layerbylayer.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(LayerByLayer.MOD_ID)
public class LayerByLayer {

    public static final String MOD_ID = "layerbylayer";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Void Dimension Key
    public static final ResourceKey<Level> VOID_DIMENSION =
            ResourceKey.create(Registries.DIMENSION,
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "void_space"));

    public LayerByLayer(IEventBus modEventBus) {
        LOGGER.info("═══════════════════════════════════════");
        LOGGER.info("  Layer by Layer - Reality Architect  ");
        LOGGER.info("  Starting initialization...          ");
        LOGGER.info("═══════════════════════════════════════");

        // Register blocks FIRST
        ModBlocks.BLOCKS.register(modEventBus);
        LOGGER.info("✓ Blocks registered");

        // Register items
        ModItems.ITEMS.register(modEventBus);
        LOGGER.info("✓ Items registered");

        // Register creative tabs
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        LOGGER.info("✓ Creative tabs registered");

        // Register progression system
        NeoForge.EVENT_BUS.register(new ProgressionManager());
        LOGGER.info("✓ Progression system registered");

        // Register game mode restrictions
        NeoForge.EVENT_BUS.register(new GameModeRestrictor());
        LOGGER.info("✓ Game mode restrictor registered");

        // Register command blocker
        NeoForge.EVENT_BUS.register(new CommandBlocker());
        LOGGER.info("✓ Command blocker registered");

        // Register physics system
        NeoForge.EVENT_BUS.register(new PhysicsManager());
        LOGGER.info("✓ Physics system registered");

        // Register physics restrictions
        NeoForge.EVENT_BUS.register(new PhysicsRestrictions());
        LOGGER.info("✓ Physics restrictions registered");

        // Register all physics law handlers
        NeoForge.EVENT_BUS.register(new ConservationHandler());
        LOGGER.info("✓ Conservation handler registered");

        NeoForge.EVENT_BUS.register(new BlockDropHandler());
        LOGGER.info("✓ Block drop handler registered");

        NeoForge.EVENT_BUS.register(new GravityHandler());
        LOGGER.info("✓ Gravity handler registered");

        NeoForge.EVENT_BUS.register(new FluidDynamicsHandler());
        LOGGER.info("✓ Fluid Dynamics handler registered");

        NeoForge.EVENT_BUS.register(new FluidSpreadBlocker());
        LOGGER.info("✓ Fluid Spread blocker registered");

        NeoForge.EVENT_BUS.register(new FluidTickBlocker());
        LOGGER.info("✓ Fluid Tick blocker registered");

        NeoForge.EVENT_BUS.register(new LiquidBlockMonitor());
        LOGGER.info("✓ Liquid Block monitor registered");

        NeoForge.EVENT_BUS.register(new UltimateFluidBlocker());
        LOGGER.info("✓ Ultimate Fluid blocker registered");

        NeoForge.EVENT_BUS.register(new FluidKiller());
        LOGGER.info("✓ Fluid Killer registered");

        // Register commands
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        LOGGER.info("✓ Commands registered");

        // Register client events
        modEventBus.addListener(this::clientSetup);

        // Register networking
        modEventBus.addListener(this::registerNetworking);
        LOGGER.info("✓ Networking registered");

        LOGGER.info("═══════════════════════════════════════");
        LOGGER.info("  Layer by Layer - Ready!             ");
        LOGGER.info("═══════════════════════════════════════");
    }

    private void clientSetup(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new ClientEventHandler());
        LOGGER.info("✓ Client events registered");
    }

    private void registerNetworking(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                ImpulsePacket.TYPE,
                ImpulsePacket.STREAM_CODEC,
                ImpulsePacket::handle
        );
    }

    private void registerCommands(RegisterCommandsEvent event) {
        LBLCommands.register(event.getDispatcher());
        LOGGER.info("✓ Commands registered");
    }
}