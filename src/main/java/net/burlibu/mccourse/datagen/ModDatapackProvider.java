package net.burlibu.mccourse.datagen;

import net.burlibu.mccourse.MCCourseMod;
import net.burlibu.mccourse.enchantment.ModEnchantments;
import net.burlibu.mccourse.trim.ModTrimMaterials;
import net.burlibu.mccourse.trim.ModTrimPatterns;
import net.burlibu.mccourse.MCCourseMod;
import net.burlibu.mccourse.trim.ModTrimPatterns;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModDatapackProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.TRIM_MATERIAL, ModTrimMaterials::bootstrap)
            .add(Registries.TRIM_PATTERN, ModTrimPatterns::bootstrap)
            .add(Registries.ENCHANTMENT, ModEnchantments::bootstrap); // stava nel registry

    public ModDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(MCCourseMod.MOD_ID));
    }
}