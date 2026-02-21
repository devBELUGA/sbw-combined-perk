package net.raiver.sbw_combined_perk.mixin;

import com.atsuishio.superbwarfare.data.DefaultDataSupplier;
import com.atsuishio.superbwarfare.data.gun.DamageReduce;
import com.atsuishio.superbwarfare.data.gun.DefaultGunData;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.perk.Perk;
import com.atsuishio.superbwarfare.perk.PerkInstance;
import net.raiver.sbw_combined_perk.compat.sbw.CombinedPerkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GunData.class, remap = false)
public abstract class GunDataCombinedPerkMixin {
    @Redirect(
            method = "compute(Z)Lcom/atsuishio/superbwarfare/data/gun/DefaultGunData;",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/atsuishio/superbwarfare/perk/Perk;computeProperties(Lcom/atsuishio/superbwarfare/data/DefaultDataSupplier;Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            remap = false
    )
    private Object sbwCombinedPerk$applyExtraComputeProperties(Perk perk, DefaultDataSupplier<?> supplier, Object rawData) {
        GunData data = (GunData) supplier;
        DefaultGunData value = perk.computeProperties(data, (DefaultGunData) rawData);
        for (PerkInstance extra : CombinedPerkStorage.getExtraPerks(data, perk.type)) {
            value = extra.perk().computeProperties(data, value);
        }
        return value;
    }

    @Redirect(
            method = "getDamageReduceRate",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/atsuishio/superbwarfare/perk/Perk;getModifiedDamageReduceRate(Lcom/atsuishio/superbwarfare/data/gun/DamageReduce;)D"
            ),
            remap = false
    )
    private double sbwCombinedPerk$applyExtraDamageReduceRate(Perk perk, DamageReduce reduce) {
        GunData data = (GunData) (Object) this;
        double value = perk.getModifiedDamageReduceRate(reduce);
        for (PerkInstance extra : CombinedPerkStorage.getExtraPerks(data, perk.type)) {
            value = extra.perk().getModifiedDamageReduceRate(new DamageReduce(value, reduce.getMinDistance()));
        }
        return value;
    }

    @Redirect(
            method = "getDamageReduceMinDistance",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/atsuishio/superbwarfare/perk/Perk;getModifiedDamageReduceMinDistance(Lcom/atsuishio/superbwarfare/data/gun/DamageReduce;)D"
            ),
            remap = false
    )
    private double sbwCombinedPerk$applyExtraDamageReduceMinDistance(Perk perk, DamageReduce reduce) {
        GunData data = (GunData) (Object) this;
        double value = perk.getModifiedDamageReduceMinDistance(reduce);
        for (PerkInstance extra : CombinedPerkStorage.getExtraPerks(data, perk.type)) {
            value = extra.perk().getModifiedDamageReduceMinDistance(new DamageReduce(reduce.getRate(), value));
        }
        return value;
    }
}
