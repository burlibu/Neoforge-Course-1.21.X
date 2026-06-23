package net.burlibu.mccourse.worldgen.biome;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public class ModSurfaceRules {
    private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource RED_TERRACOTTA = makeStateRule(Blocks.RED_TERRACOTTA);
    private static final SurfaceRules.RuleSource BLUE_TERRACOTTA = makeStateRule(Blocks.BLUE_TERRACOTTA);
    private static final SurfaceRules.RuleSource GREEN_TERRACOTTA = makeStateRule(Blocks.GREEN_TERRACOTTA);
    private static final SurfaceRules.RuleSource PURPLE_TERRACOTTA = makeStateRule(Blocks.PURPLE_TERRACOTTA);
    private static final SurfaceRules.RuleSource PINK_TERRACOTTA = makeStateRule(Blocks.PINK_TERRACOTTA); // Aggiunta per il rosa
    private static final SurfaceRules.RuleSource ORANGE_TERRACOTTA = makeStateRule(Blocks.ORANGE_TERRACOTTA); // Aggiunta per l'arancione
    private static final SurfaceRules.RuleSource WHITE_TERRACOTTA = makeStateRule(Blocks.WHITE_TERRACOTTA);
    private static final SurfaceRules.RuleSource CYAN_TERRACOTTA = makeStateRule(Blocks.CYAN_TERRACOTTA);
    private static final SurfaceRules.RuleSource LIGHT_BLUE_GLAZED_TERRACOTTA = makeStateRule(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
    private static final SurfaceRules.RuleSource LIGHT_BLUE_TERRACOTTA = makeStateRule(Blocks.LIGHT_BLUE_TERRACOTTA);

    private static final SurfaceRules.RuleSource OBSIDIAN = makeStateRule(Blocks.OBSIDIAN);
    private static final SurfaceRules.RuleSource END_STONE = makeStateRule(Blocks.END_STONE);

    private static final SurfaceRules.RuleSource GLOWSTONE = makeStateRule(Blocks.GLOWSTONE);
    private static final SurfaceRules.RuleSource NETHERRACK = makeStateRule(Blocks.NETHERRACK);
    private static final SurfaceRules.RuleSource BEDROCK = makeStateRule(Blocks.BEDROCK);

    public static SurfaceRules.RuleSource GLASS_RULE = SurfaceRules.state(Blocks.GLASS.defaultBlockState());

    public static SurfaceRules.RuleSource makeKaupenValleyRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.KAUPEN_VALLEY),
                        SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, RED_TERRACOTTA), BLUE_TERRACOTTA)),
                // Default to green terracotta
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, GREEN_TERRACOTTA)
        );
    }

    public static SurfaceRules.RuleSource makeGlowstonePlainsRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK),
                SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK),

                // Then apply biome-specific rules
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(ModBiomes.GLOWSTONE_PLAIN),
                        SurfaceRules.sequence(
                                // Obsidian on the undersides of ceilings
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_CEILING, GLASS_RULE),
                                // Obsidian on the undersides of floors (though less common in Nether caves)
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, GLOWSTONE),
                                SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, OBSIDIAN),
                                // Default to glowstone if not under a ceiling or floor
                                GLOWSTONE))
        );
    }

    public static SurfaceRules.RuleSource makeEndRotRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.END_ROT), OBSIDIAN),
                // Default to end stone
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, END_STONE)
        );
    }

    public static SurfaceRules.RuleSource makeSpiceBadlandsRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(ModBiomes.SPICE_BADLANDS),
                        SurfaceRules.sequence(
                                // Applica i blocchi solo se siamo effettivamente sul blocco di superficie (ON_FLOOR)
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.sequence(
                                                // Y = 115+ : Vetta estrema (Terracotta Bianca)
                                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(115), 0), WHITE_TERRACOTTA),
                                                // Y = 110-114 : Grigio Chiaro
                                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(110), 0), makeStateRule(Blocks.LIGHT_GRAY_TERRACOTTA)),
                                                // Y = 105-109 : Grigio Scuro
                                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(105), 0), makeStateRule(Blocks.GRAY_TERRACOTTA)),
                                                // Y = 100-104 : Nero
                                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(100), 0), makeStateRule(Blocks.BLACK_TERRACOTTA)),
                                                // Y = 95-99 : Marrone
                                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(95), 0), makeStateRule(Blocks.BROWN_TERRACOTTA)),
                                                // Y = 90-94 : Rosso
                                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(90), 0), RED_TERRACOTTA),
                                                // Y = 85-89 : Arancione
                                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(85), 0), ORANGE_TERRACOTTA),
                                                // Y = 80-84 : Giallo
                                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(80), 0), makeStateRule(Blocks.YELLOW_TERRACOTTA)),
                                                // Y = 75-79 : Lime (Verde Chiaro)
                                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(75), 0), makeStateRule(Blocks.LIME_TERRACOTTA)),
                                                // Y = 70-74 : Verde Scuro
                                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(70), 0), GREEN_TERRACOTTA),
                                                // Y = 65-69 : Cyan
                                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(65), 0), CYAN_TERRACOTTA),
                                                // Y = 60-64 : Azzurro (Light Blue)
                                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(60), 0), LIGHT_BLUE_TERRACOTTA),
                                                // Y = 55-59 : Blu Classico
                                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(55), 0), BLUE_TERRACOTTA),
                                                // Y = 50-54 : Viola
                                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(50), 0), PURPLE_TERRACOTTA),
                                                // Y = 45-49 : Rosa
                                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(45), 0), PINK_TERRACOTTA),

                                                // Sotto Y = 45 (Base o valli profonde delle Badlands)
                                                makeStateRule(Blocks.MAGENTA_TERRACOTTA)
                                        )
                                ),
                                // Se non siamo in superficie (quindi siamo sotto terra profondi), usa la terracotta normale non colorata come riempimento
                                makeStateRule(Blocks.TERRACOTTA)
                        )
                )
        );
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}