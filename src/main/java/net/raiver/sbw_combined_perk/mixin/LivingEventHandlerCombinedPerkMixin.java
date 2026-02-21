package net.raiver.sbw_combined_perk.mixin;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.event.LivingEventHandler;
import com.atsuishio.superbwarfare.perk.Perk;
import com.atsuishio.superbwarfare.perk.PerkInstance;
import net.raiver.sbw_combined_perk.compat.sbw.CombinedPerkStorage;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LivingEventHandler.class, remap = false)
public abstract class LivingEventHandlerCombinedPerkMixin {
    @Redirect(
            method = "handleChangeSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/atsuishio/superbwarfare/perk/Perk;onChangeSlot(Lcom/atsuishio/superbwarfare/data/gun/GunData;Lcom/atsuishio/superbwarfare/perk/PerkInstance;Lnet/minecraft/world/entity/Entity;)V"
            ),
            remap = false
    )
    private static void sbwCombinedPerk$applyExtraOnChangeSlot(Perk perk, GunData data, PerkInstance instance, Entity living) {
        perk.onChangeSlot(data, instance, living);
        for (PerkInstance extra : CombinedPerkStorage.getExtraPerks(data, instance.perk().type)) {
            extra.perk().onChangeSlot(data, extra, living);
        }
    }

    @Redirect(
            method = "handleGunPerksWhenHurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/atsuishio/superbwarfare/perk/Perk;getModifiedDamage(FLcom/atsuishio/superbwarfare/data/gun/GunData;Lcom/atsuishio/superbwarfare/perk/PerkInstance;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)F"
            ),
            remap = false
    )
    private static float sbwCombinedPerk$applyExtraModifiedDamage(Perk perk, float damage, GunData data, PerkInstance instance, Entity target, DamageSource source) {
        float value = perk.getModifiedDamage(damage, data, instance, target, source);
        for (PerkInstance extra : CombinedPerkStorage.getExtraPerks(data, instance.perk().type)) {
            value = extra.perk().getModifiedDamage(value, data, extra, target, source);
        }
        return value;
    }

    @Redirect(
            method = "handleGunPerksWhenHurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/atsuishio/superbwarfare/perk/Perk;onHurtEntity(FLcom/atsuishio/superbwarfare/data/gun/GunData;Lcom/atsuishio/superbwarfare/perk/PerkInstance;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)V"
            ),
            remap = false
    )
    private static void sbwCombinedPerk$applyExtraOnHurtEntity(Perk perk, float damage, GunData data, PerkInstance instance, Entity target, DamageSource source) {
        perk.onHurtEntity(damage, data, instance, target, source);
        for (PerkInstance extra : CombinedPerkStorage.getExtraPerks(data, instance.perk().type)) {
            extra.perk().onHurtEntity(damage, data, extra, target, source);
        }
    }

    @Redirect(
            method = "handleGunPerksWhenHurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/atsuishio/superbwarfare/perk/Perk;onMeleeAttack(Lcom/atsuishio/superbwarfare/data/gun/GunData;Lcom/atsuishio/superbwarfare/perk/PerkInstance;Lnet/minecraft/world/entity/Entity;)V"
            ),
            remap = false
    )
    private static void sbwCombinedPerk$applyExtraOnMeleeAttack(Perk perk, GunData data, PerkInstance instance, Entity target) {
        perk.onMeleeAttack(data, instance, target);
        for (PerkInstance extra : CombinedPerkStorage.getExtraPerks(data, instance.perk().type)) {
            extra.perk().onMeleeAttack(data, extra, target);
        }
    }

    @Redirect(
            method = "handleGunPerksWhenDeath",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/atsuishio/superbwarfare/perk/Perk;onKill(Lcom/atsuishio/superbwarfare/data/gun/GunData;Lcom/atsuishio/superbwarfare/perk/PerkInstance;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)V"
            ),
            remap = false
    )
    private static void sbwCombinedPerk$applyExtraOnKill(Perk perk, GunData data, PerkInstance instance, Entity target, DamageSource source) {
        perk.onKill(data, instance, target, source);
        for (PerkInstance extra : CombinedPerkStorage.getExtraPerks(data, instance.perk().type)) {
            extra.perk().onKill(data, extra, target, source);
        }
    }
}
