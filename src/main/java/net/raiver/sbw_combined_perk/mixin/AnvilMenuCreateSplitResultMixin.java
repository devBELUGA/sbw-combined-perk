package net.raiver.sbw_combined_perk.mixin;

import com.atsuishio.superbwarfare.perk.PerkInstance;
import java.util.ArrayList;
import java.util.List;
import net.raiver.sbw_combined_perk.compat.sbw.CombinedPerkStorage;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuCreateSplitResultMixin {
    @Unique
    private static final int SBW_COMBINED_PERK_SPLIT_COST_PER_PERK = 2;

    @Inject(method = "createResult()V", at = @At("HEAD"), cancellable = true)
    private void sbwCombinedPerk$createSplitOutputWhenLeftEmpty(CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu) (Object) this;

        ItemStack left = menu.getSlot(0).getItem();
        if (!left.isEmpty()) {
            return;
        }

        ItemStack right = menu.getSlot(1).getItem();
        if (!CombinedPerkStorage.isPerkContainerStack(right)) {
            return;
        }

        List<PerkInstance> rightEntries = CombinedPerkStorage.getPerkEntriesFromStack(right);
        if (rightEntries.size() <= 1) {
            return;
        }

        PerkInstance main = rightEntries.get(0);
        List<PerkInstance> extras = new ArrayList<>(rightEntries.subList(1, rightEntries.size()));

        ItemStack output = CombinedPerkStorage.toPerkStack(main);
        int splitCost = Math.max(1, SBW_COMBINED_PERK_SPLIT_COST_PER_PERK * extras.size());
        CombinedPerkStorage.attachSplitExtras(output, extras, splitCost);

        menu.getSlot(2).set(output);
        menu.repairItemCountCost = 1;
        menu.setMaximumCost(splitCost);
        menu.broadcastChanges();
        ci.cancel();
    }
}
