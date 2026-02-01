package com.example.layerbylayer.registry;

import com.example.layerbylayer.LayerByLayer;
import com.example.layerbylayer.item.PhysicsCoreItem;
import com.example.layerbylayer.physics.PhysicsLaw;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(LayerByLayer.MOD_ID);

    // === Physics Core Items (активируемые предметы) ===

    public static final DeferredItem<Item> GRAVITY_CORE =
            ITEMS.registerItem("gravity_core",
                    properties -> new PhysicsCoreItem(PhysicsLaw.GRAVITY, properties)
            );

    public static final DeferredItem<Item> COLLISION_CORE =
            ITEMS.registerItem("collision_core",
                    properties -> new PhysicsCoreItem(PhysicsLaw.COLLISION, properties)
            );

    public static final DeferredItem<Item> CONSERVATION_CORE =
            ITEMS.registerItem("conservation_core",
                    properties -> new PhysicsCoreItem(PhysicsLaw.CONSERVATION, properties)
            );

    public static final DeferredItem<Item> FLUID_DYNAMICS_CORE =
            ITEMS.registerItem("fluiddynamics_core",
                    properties -> new PhysicsCoreItem(PhysicsLaw.FLUID_DYNAMICS, properties)
            );

    public static final DeferredItem<Item> THERMODYNAMICS_CORE =
            ITEMS.registerItem("thermodynamics_core",
                    properties -> new PhysicsCoreItem(PhysicsLaw.THERMODYNAMICS, properties)
            );

    // === Block Items (чтобы блоки можно было держать в руках) ===

    public static final DeferredItem<?> GRAVITY_CORE_BLOCK_ITEM =
            ITEMS.registerSimpleBlockItem(
                    "gravity_core_block",
                    ModBlocks.GRAVITY_CORE_BLOCK
            );

    public static final DeferredItem<?> COLLISION_CORE_BLOCK_ITEM =
            ITEMS.registerSimpleBlockItem(
                    "collision_core_block",
                    ModBlocks.COLLISION_CORE_BLOCK
            );

    public static final DeferredItem<?> CONSERVATION_CORE_BLOCK_ITEM =
            ITEMS.registerSimpleBlockItem(
                    "conservation_core_block",
                    ModBlocks.CONSERVATION_CORE_BLOCK
            );

    public static final DeferredItem<?> FLUID_DYNAMICS_CORE_BLOCK_ITEM =
            ITEMS.registerSimpleBlockItem(
                    "fluiddynamics_core_block",
                    ModBlocks.FLUID_DYNAMICS_CORE_BLOCK
            );

    public static final DeferredItem<?> THERMODYNAMICS_CORE_BLOCK_ITEM =
            ITEMS.registerSimpleBlockItem(
                    "thermodynamics_core_block",
                    ModBlocks.THERMODYNAMICS_CORE_BLOCK
            );

    // === Other Items ===

    public static final DeferredItem<Item> REALITY_FRAGMENT =
            ITEMS.registerSimpleItem("reality_fragment");
}