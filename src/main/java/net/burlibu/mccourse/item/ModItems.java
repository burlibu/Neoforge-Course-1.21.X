package net.burlibu.mccourse.item;

import net.burlibu.mccourse.MCCourseMod;
import net.burlibu.mccourse.item.custom.Chainsawitem;
import net.burlibu.mccourse.item.custom.FuelItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MCCourseMod.MOD_ID);

    public static final DeferredItem<Item> BLACK_OPAL = ITEMS.registerSimpleItem("black_opal");
    public static final DeferredItem<Item> RAW_BLACK_OPAL = ITEMS.registerItem("raw_black_opal", Item::new, new Item.Properties());
    public static final DeferredItem<Item> CHAINSAW = ITEMS.registerItem("chainsaw", Chainsawitem::new, new Item.Properties().durability(32));
    public static final DeferredItem<Item> TOMATO = ITEMS.registerItem("tomato", Item::new,new Item.Properties().food(ModFoodProperties.TOMATO));
    public static final DeferredItem<Item> FROSTFIRE_ICE = ITEMS.registerItem("frostfire_ice", properties -> new FuelItem(properties,800),new Item.Properties().food(ModFoodProperties.TOMATO));

    public static void register(IEventBus eventbus){
        ITEMS.register(eventbus);
    }
}
