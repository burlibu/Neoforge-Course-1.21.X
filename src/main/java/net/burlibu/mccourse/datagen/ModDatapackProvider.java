package net.burlibu.mccourse.datagen;

import net.burlibu.mccourse.MCCourseMod;
import net.burlibu.mccourse.enchantment.ModEnchantments;
import net.burlibu.mccourse.trim.ModTrimMaterials;
import net.burlibu.mccourse.trim.ModTrimPatterns;
import net.burlibu.mccourse.MCCourseMod;
import net.burlibu.mccourse.trim.ModTrimPatterns;
import net.burlibu.mccourse.worldgen.ModBiomeModifiers;
import net.burlibu.mccourse.worldgen.ModConfiguredFeatures;
import net.burlibu.mccourse.worldgen.ModPlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModDatapackProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            //TRIM
            .add(Registries.TRIM_MATERIAL, ModTrimMaterials::bootstrap)
            .add(Registries.TRIM_PATTERN, ModTrimPatterns::bootstrap)
            //ENCHANTMENT
            .add(Registries.ENCHANTMENT, ModEnchantments::bootstrap) // stava nel registry
            //BIOME
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap);


    public ModDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(MCCourseMod.MOD_ID));
    }

    @Override
    public String getName() {
        return super.getName();
    }
}