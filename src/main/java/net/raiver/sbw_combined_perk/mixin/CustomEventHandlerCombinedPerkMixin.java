package net.raiver.sbw_combined_perk.mixin;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.event.CustomEventHandler;
import com.atsuishio.superbwarfare.perk.Perk;
import com.atsuishio.superbwarfare.perk.PerkInstance;
import net.raiver.sbw_combined_perk.compat.sbw.CombinedPerkStorage;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CustomEventHandler.class, remap = false)
public abstract class CustomEventHandlerCombinedPerkMixin {
    @Redirect(
            method = "onPreReload",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/atsuishio/superbwarfare/perk/Perk;preReload(Lcom/atsuishio/superbwarfare/data/gun/GunData;Lcom/atsuishio/superbwarfare/perk/PerkInstance;Lnet/minecraft/world/entity/Entity;)V"
            ),
            remap = false
    )
    private static void sbwCombinedPerk$applyExtraPreReload(Perk perk, GunData data, PerkInstance instance, Entity shooter) {
        perk.preReload(data, instance, shooter);
        for (PerkInstance extra : CombinedPerkStorage.getExtraPerks(data, instance.perk().type)) {
            extra.perk().preReload(data, extra, shooter);
        }
    }

    @Redirect(
            method = "onPostReload",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/atsuishio/superbwarfare/perk/Perk;postReload(Lcom/atsuishio/superbwarfare/data/gun/GunData;Lcom/atsuishio/superbwarfare/perk/PerkInstance;Lnet/minecraft/world/entity/Entity;)V"
            ),
            remap = false
    )
    private static void sbwCombinedPerk$applyExtraPostReload(Perk perk, GunData data, PerkInstance instance, Entity shooter) {
        perk.postReload(data, instance, shooter);
        for (PerkInstance extra : CombinedPerkStorage.getExtraPerks(data, instance.perk().type)) {
            extra.perk().postReload(data, extra, shooter);
        }
    }

    @Redirect(
            method = "onProjectileHitEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/atsuishio/superbwarfare/perk/Perk;onHit(Lnet/minecraft/world/entity/LivingEntity;Lcom/atsuishio/superbwarfare/data/gun/GunData;Lcom/atsuishio/superbwarfare/perk/PerkInstance;Lnet/minecraft/world/entity/Entity;)V"
            ),
            remap = false
    )
    private static void sbwCombinedPerk$applyExtraOnHit(Perk perk, LivingEntity attacker, GunData data, PerkInstance instance, Entity target) {
        perk.onHit(attacker, data, instance, target);
        for (PerkInstance extra : CombinedPerkStorage.getExtraPerks(data, instance.perk().type)) {
            extra.perk().onHit(attacker, data, extra, target);
        }
    }
}
