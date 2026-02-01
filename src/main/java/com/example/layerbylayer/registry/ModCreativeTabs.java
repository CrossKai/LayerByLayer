package com.example.layerbylayer.registry;

import com.example.layerbylayer.LayerByLayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LayerByLayer.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> LBL_TAB =
            CREATIVE_MODE_TABS.register("lbl_tab", () -> CreativeModeTab.builder()
                    .title(Component.literal("Layer by Layer"))
                    .icon(() -> new ItemStack(ModItems.REALITY_FRAGMENT.get()))
                    .displayItems((parameters, output) -> {
                        // Reality Fragment
                        output.accept(ModItems.REALITY_FRAGMENT.get());

                        // Physics Core Items
                        output.accept(ModItems.GRAVITY_CORE.get());
                        output.accept(ModItems.COLLISION_CORE.get());
                        output.accept(ModItems.CONSERVATION_CORE.get());
                        output.accept(ModItems.FLUID_DYNAMICS_CORE.get());
                        output.accept(ModItems.THERMODYNAMICS_CORE.get());

                        // Core Blocks
                        output.accept(ModBlocks.GRAVITY_CORE_BLOCK.get());
                        output.accept(ModBlocks.COLLISION_CORE_BLOCK.get());
                        output.accept(ModBlocks.CONSERVATION_CORE_BLOCK.get());
                        output.accept(ModBlocks.FLUID_DYNAMICS_CORE_BLOCK.get());
                        output.accept(ModBlocks.THERMODYNAMICS_CORE_BLOCK.get());
                    })
                    .build()
            );
}