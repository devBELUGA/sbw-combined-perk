package net.raiver.sbw_combined_perk.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemCombinerMenu.class)
public interface ItemCombinerMenuAccessor {
    @Accessor("inputSlots")
    Container sbwCombinedPerk$getInputSlots();

    @Accessor("access")
    ContainerLevelAccess sbwCombinedPerk$getAccess();
}
