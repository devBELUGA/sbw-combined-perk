package net.raiver.sbw_combined_perk.init;

import net.raiver.sbw_combined_perk.SbwCombinedPerk;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SbwCombinedPerk.MOD_ID);

    public static final RegistryObject<Item> PERK_FOLDER =
            ITEMS.register("perk_folder", () -> new Item(new Item.Properties()));

    private ModItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
