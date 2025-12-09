package net.burlibu.mccourse.util;

import net.burlibu.mccourse.MCCourseMod;
import net.burlibu.mccourse.component.ModDataComponentTypes;
import net.burlibu.mccourse.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class ModItemProperties {
    public static void addCustomItemProperties() {
        ItemProperties.register(ModItems.DATA_TABLET.get(), ResourceLocation.fromNamespaceAndPath(MCCourseMod.MOD_ID, "on"),
                (pStack, pLevel, pEntity, pSeed) -> pStack.get(ModDataComponentTypes.FOUND_BLOCK) != null ? 1f : 0f);
    }
}
