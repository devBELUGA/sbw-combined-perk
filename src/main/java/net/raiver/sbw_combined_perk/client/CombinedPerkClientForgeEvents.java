package net.raiver.sbw_combined_perk.client;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.atsuishio.superbwarfare.perk.Perk;
import com.atsuishio.superbwarfare.perk.PerkInstance;
import com.mojang.datafixers.util.Either;
import java.util.List;
import net.raiver.sbw_combined_perk.SbwCombinedPerk;
import net.raiver.sbw_combined_perk.client.tooltip.CombinedPerkIconsComponent;
import net.raiver.sbw_combined_perk.compat.sbw.CombinedPerkStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SbwCombinedPerk.MOD_ID, value = Dist.CLIENT)
public final class CombinedPerkClientForgeEvents {
    private CombinedPerkClientForgeEvents() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        List<PerkInstance> stackPerks = CombinedPerkStorage.getPerkEntriesFromStack(stack);
        if (CombinedPerkStorage.isPerkFolderStack(stack) && !stackPerks.isEmpty()) {
            event.getToolTip().add(Component.empty());
            event.getToolTip().add(Component.translatable("tooltip.sbw_combined_perk.folder_title").withStyle(ChatFormatting.GOLD));
            for (PerkInstance instance : stackPerks) {
                event.getToolTip().add(perkLine(instance, ChatFormatting.DARK_GRAY));
            }
        } else if (CombinedPerkStorage.isCombinedPerkStack(stack)) {
            event.getToolTip().add(Component.empty());
            event.getToolTip().add(Component.translatable("tooltip.sbw_combined_perk.combined_title").withStyle(ChatFormatting.GOLD));
            for (PerkInstance instance : stackPerks) {
                event.getToolTip().add(perkLine(instance, ChatFormatting.DARK_GRAY));
            }
        }

        if (!(stack.getItem() instanceof GunItem)) {
            return;
        }

        GunData data = GunData.from(stack);
        List<PerkInstance> extras = CombinedPerkStorage.getAllExtraPerks(data);
        if (extras.isEmpty()) {
            return;
        }

        event.getToolTip().add(Component.empty());
        event.getToolTip().add(Component.translatable("tooltip.sbw_combined_perk.weapon_extra_title").withStyle(ChatFormatting.GOLD));
        for (Perk.Type type : Perk.Type.values()) {
            for (PerkInstance instance : CombinedPerkStorage.getExtraPerks(data, type)) {
                event.getToolTip().add(perkLine(instance, ChatFormatting.GRAY));
            }
        }
    }

    @SubscribeEvent
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();

        if ((CombinedPerkStorage.isCombinedPerkStack(stack) || CombinedPerkStorage.isPerkFolderStack(stack))
                && !CombinedPerkStorage.getPerkEntriesFromStack(stack).isEmpty()) {
            List<ItemStack> icons = CombinedPerkStorage.toIconStacks(CombinedPerkStorage.getPerkEntriesFromStack(stack));
            if (!icons.isEmpty()) {
                event.getTooltipElements().add(Either.<FormattedText, TooltipComponent>right(new CombinedPerkIconsComponent(icons)));
            }
        }

        if (!(stack.getItem() instanceof GunItem)) {
            return;
        }

        GunData data = GunData.from(stack);
        List<PerkInstance> extras = CombinedPerkStorage.getAllExtraPerks(data);
        if (extras.isEmpty()) {
            return;
        }

        List<ItemStack> icons = CombinedPerkStorage.toIconStacks(extras);
        if (!icons.isEmpty()) {
            event.getTooltipElements().add(Either.<FormattedText, TooltipComponent>right(new CombinedPerkIconsComponent(icons)));
        }
    }

    private static Component perkLine(PerkInstance instance, ChatFormatting prefixColor) {
        return Component.literal(" + ").withStyle(prefixColor)
                .append(Component.translatable("item.superbwarfare." + instance.perk().descriptionId).withStyle(instance.perk().type.getColor()))
                .append(Component.literal(" Lv." + instance.level()).withStyle(ChatFormatting.WHITE));
    }
}
