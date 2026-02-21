package net.raiver.sbw_combined_perk.mixin;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.atsuishio.superbwarfare.perk.Perk;
import com.atsuishio.superbwarfare.perk.PerkInstance;
import net.raiver.sbw_combined_perk.compat.sbw.CombinedPerkStorage;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GunItem.class, remap = false)
public abstract class GunItemCombinedPerkMixin {
    @Redirect(
            method = "shootBullet(Lcom/atsuishio/superbwarfare/data/gun/ShootParameters;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/atsuishio/superbwarfare/perk/Perk;modifyProjectile(Lcom/atsuishio/superbwarfare/data/gun/GunData;Lcom/atsuishio/superbwarfare/perk/PerkInstance;Lnet/minecraft/world/entity/Entity;)V"
            ),
            remap = false
    )
    private void sbwCombinedPerk$applyExtraProjectileModifiers(Perk perk, GunData data, PerkInstance instance, Entity projectileEntity) {
        perk.modifyProjectile(data, instance, projectileEntity);
        for (PerkInstance extra : CombinedPerkStorage.getExtraPerks(data, instance.perk().type)) {
            extra.perk().modifyProjectile(data, extra, projectileEntity);
        }
    }
}
