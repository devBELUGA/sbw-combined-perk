package net.raiver.sbw_combined_perk.mixin;

import net.raiver.sbw_combined_perk.compat.sbw.CombinedPerkStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class AnvilMenuSplitPickupMixin {
    @Inject(
        method = "clicked(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void sbwCombinedPerk$requireShiftLeftClick(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
        if (!((Object) this instanceof AnvilMenu anvilMenu)) {
            return;
        }
        if (slotId != 2 || slotId < 0 || slotId >= anvilMenu.slots.size()) {
            return;
        }

        Slot slot = anvilMenu.slots.get(slotId);
        if (slot == null || !slot.hasItem()) {
            return;
        }

        ItemStack output = slot.getItem();
        if (!CombinedPerkStorage.hasSplitExtras(output)) {
            return;
        }

        boolean isAllowedPickup = clickType == ClickType.QUICK_MOVE && button == 0;
        if (isAllowedPickup) {
            return;
        }

        if (!player.level().isClientSide) {
            player.displayClientMessage(Component.translatable("message.sbw_combined_perk.split_pickup_hint"), false);
        }
        ci.cancel();
    }
}
