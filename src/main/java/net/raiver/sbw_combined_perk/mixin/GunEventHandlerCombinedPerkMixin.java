package net.raiver.sbw_combined_perk.mixin;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.event.GunEventHandler;
import com.atsuishio.superbwarfare.perk.Perk;
import com.atsuishio.superbwarfare.perk.PerkInstance;
import net.raiver.sbw_combined_perk.compat.sbw.CombinedPerkStorage;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GunEventHandler.class, remap = false)
public abstract class GunEventHandlerCombinedPerkMixin {
    @Redirect(
            method = "tickPerk",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/atsuishio/superbwarfare/perk/Perk;tick(Lcom/atsuishio/superbwarfare/data/gun/GunData;Lcom/atsuishio/superbwarfare/perk/PerkInstance;Lnet/minecraft/world/entity/Entity;)V"
            ),
            remap = false
    )
    private static void sbwCombinedPerk$applyExtraTickPerks(Perk perk, GunData data, PerkInstance instance, Entity shooter) {
        perk.tick(data, instance, shooter);
        for (PerkInstance extra : CombinedPerkStorage.getExtraPerks(data, instance.perk().type)) {
            extra.perk().tick(data, extra, shooter);
        }
    }
}
