package net.raiver.sbw_combined_perk.mixin;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.perk.Perk;
import com.atsuishio.superbwarfare.perk.PerkInstance;
import net.raiver.sbw_combined_perk.compat.sbw.CombinedPerkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ClientEventHandler.class, remap = false)
public abstract class ClientEventHandlerCombinedPerkMixin {
    @Redirect(
            method = "shootClient",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/atsuishio/superbwarfare/perk/Perk;getModifiedCustomRPM(ILcom/atsuishio/superbwarfare/data/gun/GunData;Lcom/atsuishio/superbwarfare/perk/PerkInstance;)I"
            ),
            remap = false
    )
    private static int sbwCombinedPerk$applyExtraCustomRpm(Perk perk, int rpm, GunData data, PerkInstance instance) {
        int value = perk.getModifiedCustomRPM(rpm, data, instance);
        for (PerkInstance extra : CombinedPerkStorage.getExtraPerks(data, instance.perk().type)) {
            value = extra.perk().getModifiedCustomRPM(value, data, extra);
        }
        return value;
    }
}
