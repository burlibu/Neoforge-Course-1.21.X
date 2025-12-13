package net.burlibu.mccourse.sound;

import net.burlibu.mccourse.MCCourseMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
// The sounds are located in the assets/../sounds folder and have to be .ogg and be mono! very important
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MCCourseMod.MOD_ID);


    // --------------------------------SOUNDS ------------
    public static final Supplier<SoundEvent> CHAINSAW_CUT = registerSoundEvent("chainsaw_cut");
    public static final Supplier<SoundEvent> CHAINSAW_PULL = registerSoundEvent("chainsaw_pull");

    // Helper
    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MCCourseMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
    // Register in the event bus
    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}