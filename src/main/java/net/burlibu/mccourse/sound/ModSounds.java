package net.burlibu.mccourse.sound;

import net.burlibu.mccourse.MCCourseMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
// The sounds are located in the assets/../sounds folder and have to be .ogg and be mono! very important
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MCCourseMod.MOD_ID);


    // --------------------------------SOUNDS ------------
    public static final Supplier<SoundEvent> CHAINSAW_CUT = registerSoundEvent("chainsaw_cut");
    public static final Supplier<SoundEvent> CHAINSAW_PULL = registerSoundEvent("chainsaw_pull");
    public static final Supplier<SoundEvent> MAGIC_BLOCK_BREAK = registerSoundEvent("magic_block_break");
    public static final Supplier<SoundEvent> MAGIC_BLOCK_STEP = registerSoundEvent("magic_block_step");
    public static final Supplier<SoundEvent> MAGIC_BLOCK_PLACE = registerSoundEvent("magic_block_place");
    public static final Supplier<SoundEvent> MAGIC_BLOCK_HIT = registerSoundEvent("magic_block_hit");
    public static final Supplier<SoundEvent> MAGIC_BLOCK_FALL = registerSoundEvent("magic_block_fall");

    public static final DeferredSoundType MAGIC_BLOCK_SOUNDS = new DeferredSoundType(1f, 1f,
            ModSounds.MAGIC_BLOCK_BREAK, ModSounds.MAGIC_BLOCK_STEP, ModSounds.MAGIC_BLOCK_PLACE,
            ModSounds.MAGIC_BLOCK_HIT, ModSounds.MAGIC_BLOCK_FALL);
    // Helper
    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MCCourseMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
    public static final Supplier<SoundEvent> TREPALI = registerSoundEvent("trepali");
    public static final ResourceKey<JukeboxSong> TREPALI_KEY = createSong("trepali");

    private static ResourceKey<JukeboxSong> createSong(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(MCCourseMod.MOD_ID, name));
    }


    // Register in the event bus
    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}