package net.raiver.sbw_combined_perk.client;

import net.raiver.sbw_combined_perk.SbwCombinedPerk;
import net.raiver.sbw_combined_perk.client.tooltip.ClientCombinedPerkIconsComponent;
import net.raiver.sbw_combined_perk.client.tooltip.CombinedPerkIconsComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SbwCombinedPerk.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CombinedPerkClientModEvents {
    private CombinedPerkClientModEvents() {
    }

    @SubscribeEvent
    public static void registerTooltipFactory(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(CombinedPerkIconsComponent.class, ClientCombinedPerkIconsComponent::new);
    }
}
