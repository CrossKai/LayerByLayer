package com.example.layerbylayer.registry;

import com.example.layerbylayer.LayerByLayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBiomes {

    public static final DeferredRegister<Biome> BIOMES =
            DeferredRegister.create(Registries.BIOME, LayerByLayer.MOD_ID);

    public static final DeferredHolder<Biome, Biome> VOID_BIOME = BIOMES.register("void_biome",
            ModBiomes::createVoidBiome);

    private static Biome createVoidBiome() {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        BiomeGenerationSettings.PlainBuilder generationBuilder = new BiomeGenerationSettings.PlainBuilder();

        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .fogColor(0xFFFFFF)
                .skyColor(0xFFFFFF)
                .waterColor(0xFFFFFF)
                .waterFogColor(0xFFFFFF)
                .grassColorOverride(0xFFFFFF)
                .foliageColorOverride(0xFFFFFF)
                .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                .build();

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(0.5F)
                .downfall(0.0F)
                .specialEffects(effects)
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(generationBuilder.build())
                .build();
    }
}