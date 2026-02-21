package net.raiver.sbw_combined_perk.mixin;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.item.PerkItem;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.atsuishio.superbwarfare.menu.ReforgingTableMenu;
import com.atsuishio.superbwarfare.perk.Perk;
import com.atsuishio.superbwarfare.perk.PerkInstance;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.raiver.sbw_combined_perk.compat.sbw.CombinedPerkStorage;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ReforgingTableMenu.class, remap = false)
public abstract class ReforgingTableMenuCombinedPerkMixin {
    @Shadow
    protected Container container;

    @Unique
    private final Map<Perk.Type, ItemStack> sbwCombinedPerk$inputPerkStacks = new EnumMap<>(Perk.Type.class);

    @Unique
    private Perk.Type sbwCombinedPerk$takenPerkType;

    @Inject(method = "generateResult", at = @At("HEAD"))
    private void sbwCombinedPerk$captureReforgeInputs(CallbackInfo ci) {
        sbwCombinedPerk$inputPerkStacks.clear();
        captureSlot(1, Perk.Type.AMMO);
        captureSlot(2, Perk.Type.FUNCTIONAL);
        captureSlot(3, Perk.Type.DAMAGE);
    }

    @Inject(method = "generateResult", at = @At("TAIL"))
    private void sbwCombinedPerk$applyCombinedPerksOnResult(CallbackInfo ci) {
        ItemStack result = this.container.getItem(4);
        if (result.isEmpty() || !(result.getItem() instanceof GunItem)) {
            sbwCombinedPerk$inputPerkStacks.clear();
            return;
        }

        GunData data = GunData.from(result);
        boolean changed = false;

        for (Map.Entry<Perk.Type, ItemStack> entry : sbwCombinedPerk$inputPerkStacks.entrySet()) {
            Perk.Type type = entry.getKey();
            int slot = slotByType(type);

            if (!this.container.getItem(slot).isEmpty()) {
                continue;
            }

            List<PerkInstance> entries = CombinedPerkStorage.getPerkEntriesFromStack(entry.getValue());
            if (entries.isEmpty()) {
                continue;
            }

            Perk base = data.perk.get(type);
            if (base == null) {
                continue;
            }

            LinkedHashMap<String, PerkInstance> unique = CombinedPerkStorage.toUniqueMap(entries);
            unique.remove(base.name);
            CombinedPerkStorage.setExtraPerks(data, type, new ArrayList<>(unique.values()));
            changed = true;
        }

        if (changed) {
            data.save();
        }
        sbwCombinedPerk$inputPerkStacks.clear();
    }

    @Inject(method = "onPlaceGun", at = @At("TAIL"))
    private void sbwCombinedPerk$showCombinedPerkItems(ItemStack stack, CallbackInfo ci) {
        if (!(stack.getItem() instanceof GunItem)) {
            return;
        }

        GunData data = GunData.from(stack);
        boolean changed = false;

        for (Perk.Type type : Perk.Type.values()) {
            Perk base = data.perk.get(type);
            if (base == null) {
                continue;
            }

            List<PerkInstance> merged = CombinedPerkStorage.getMergedPerksForType(data, type);
            if (merged.size() <= 1) {
                continue;
            }

            int slot = slotByType(type);
            ItemStack icon = this.container.getItem(slot);
            if (icon.isEmpty()) {
                Item item = base.getItem().get();
                icon = item.getDefaultInstance();
            }

            this.container.setItem(slot, CombinedPerkStorage.writeCombinedPerkStack(icon, merged));
            changed = true;
        }

        if (changed) {
            this.container.setChanged();
        }
    }

    @Inject(method = "onTakePerk", at = @At("HEAD"))
    private void sbwCombinedPerk$captureTakenPerk(ItemStack perk, CallbackInfo ci) {
        this.sbwCombinedPerk$takenPerkType = null;
        if (perk.getItem() instanceof PerkItem perkItem) {
            this.sbwCombinedPerk$takenPerkType = perkItem.getPerk().type;
        }
    }

    @Inject(method = "onTakePerk", at = @At("TAIL"))
    private void sbwCombinedPerk$clearExtraPerksWhenTaken(ItemStack perk, CallbackInfo ci) {
        if (this.sbwCombinedPerk$takenPerkType == null) {
            return;
        }

        ItemStack gun = this.container.getItem(0);
        if (gun.getItem() instanceof GunItem) {
            GunData data = GunData.from(gun);
            if (data.perk.get(this.sbwCombinedPerk$takenPerkType) == null) {
                CombinedPerkStorage.setExtraPerks(data, this.sbwCombinedPerk$takenPerkType, List.of());
                data.save();
            }
        }

        this.sbwCombinedPerk$takenPerkType = null;
    }

    @Unique
    private void captureSlot(int slot, Perk.Type type) {
        ItemStack stack = this.container.getItem(slot);
        if (stack.isEmpty() || !(stack.getItem() instanceof PerkItem)) {
            return;
        }
        sbwCombinedPerk$inputPerkStacks.put(type, stack.copy());
    }

    @Unique
    private static int slotByType(Perk.Type type) {
        return switch (type) {
            case AMMO -> 1;
            case FUNCTIONAL -> 2;
            case DAMAGE -> 3;
        };
    }
}
