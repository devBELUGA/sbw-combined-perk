package net.raiver.sbw_combined_perk.client.tooltip;

import java.util.List;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record CombinedPerkIconsComponent(List<ItemStack> icons) implements TooltipComponent {
}
