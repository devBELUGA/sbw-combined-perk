package net.raiver.sbw_combined_perk;

import net.raiver.sbw_combined_perk.init.ModItems;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SbwCombinedPerk.MOD_ID)
public final class SbwCombinedPerk {
    public static final String MOD_ID = "sbw_combined_perk";

    public SbwCombinedPerk() {
        ModItems.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
