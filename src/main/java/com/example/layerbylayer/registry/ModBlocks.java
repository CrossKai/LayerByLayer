package com.example.layerbylayer.registry;

import com.example.layerbylayer.LayerByLayer;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(LayerByLayer.MOD_ID);

    // Используем registerSimpleBlock - это правильный способ для NeoForge 1.21.4+

    public static final DeferredBlock<?> GRAVITY_CORE_BLOCK = BLOCKS.registerSimpleBlock(
            "gravity_core_block",
            BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .lightLevel(state -> 10)
    );

    public static final DeferredBlock<?> COLLISION_CORE_BLOCK = BLOCKS.registerSimpleBlock(
            "collision_core_block",
            BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .lightLevel(state -> 10)
    );

    public static final DeferredBlock<?> CONSERVATION_CORE_BLOCK = BLOCKS.registerSimpleBlock(
            "conservation_core_block",
            BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .lightLevel(state -> 10)
    );

    public static final DeferredBlock<?> FLUID_DYNAMICS_CORE_BLOCK = BLOCKS.registerSimpleBlock(
            "fluiddynamics_core_block",
            BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .lightLevel(state -> 10)
    );

    public static final DeferredBlock<?> THERMODYNAMICS_CORE_BLOCK = BLOCKS.registerSimpleBlock(
            "thermodynamics_core_block",
            BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .lightLevel(state -> 10)
    );
}