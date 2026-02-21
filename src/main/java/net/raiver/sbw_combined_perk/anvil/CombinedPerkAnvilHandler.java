package net.raiver.sbw_combined_perk.anvil;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.atsuishio.superbwarfare.perk.Perk;
import com.atsuishio.superbwarfare.perk.PerkInstance;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import net.raiver.sbw_combined_perk.SbwCombinedPerk;
import net.raiver.sbw_combined_perk.compat.sbw.CombinedPerkStorage;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SbwCombinedPerk.MOD_ID)
public final class CombinedPerkAnvilHandler {
    private static final int LEVEL_COST_PER_ADDED_PERK = 5;
    private static final int LEVEL_COST_PER_SPLIT_PERK = 2;

    private CombinedPerkAnvilHandler() {
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (right.isEmpty()) {
            return;
        }

        if (left.isEmpty()) {
            trySplitPerkItems(right, event);
            return;
        }

        if (tryApplyPerkContainerToGun(left, right, event)) {
            return;
        }

        tryCombinePerkItems(left, right, event);
    }

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        ItemStack output = event.getOutput();
        int refundLevels = CombinedPerkStorage.getSplitRefundLevels(output);
        List<PerkInstance> extras = CombinedPerkStorage.consumeSplitExtras(output);
        if (extras.isEmpty()) {
            return;
        }

        Player player = event.getEntity();
        if (refundLevels > 0 && !player.getAbilities().instabuild) {
            player.giveExperienceLevels(refundLevels);
        }
        for (PerkInstance extra : extras) {
            giveOrDrop(player, CombinedPerkStorage.toPerkStack(extra));
        }
    }

    private static boolean trySplitPerkItems(ItemStack right, AnvilUpdateEvent event) {
        if (!CombinedPerkStorage.isPerkContainerStack(right)) {
            return false;
        }

        List<PerkInstance> rightEntries = CombinedPerkStorage.getPerkEntriesFromStack(right);
        if (rightEntries.size() <= 1) {
            return false;
        }

        PerkInstance mainFromRight = rightEntries.get(0);
        List<PerkInstance> extras = new ArrayList<>();
        extras.addAll(rightEntries.subList(1, rightEntries.size()));

        ItemStack output = CombinedPerkStorage.toPerkStack(mainFromRight);
        int splitCost = Math.max(1, LEVEL_COST_PER_SPLIT_PERK * extras.size());
        CombinedPerkStorage.attachSplitExtras(output, extras, splitCost);

        event.setOutput(output);
        event.setMaterialCost(1);
        event.setCost(splitCost);
        return true;
    }

    private static boolean tryCombinePerkItems(ItemStack left, ItemStack right, AnvilUpdateEvent event) {
        if (!CombinedPerkStorage.isPerkContainerStack(left) || !CombinedPerkStorage.isPerkContainerStack(right)) {
            return false;
        }

        List<PerkInstance> leftEntries = CombinedPerkStorage.getPerkEntriesFromStack(left);
        List<PerkInstance> rightEntries = CombinedPerkStorage.getPerkEntriesFromStack(right);
        if (leftEntries.isEmpty() || rightEntries.isEmpty()) {
            return false;
        }

        LinkedHashMap<String, PerkInstance> merged = CombinedPerkStorage.toUniqueMap(leftEntries);
        int added = CombinedPerkStorage.mergeUnique(merged, rightEntries);
        if (added <= 0) {
            return false;
        }

        List<PerkInstance> mergedEntries = new ArrayList<>(merged.values());
        ItemStack output;
        if (CombinedPerkStorage.getSingleType(mergedEntries) != null) {
            ItemStack icon = CombinedPerkStorage.isPerkStack(left) ? left : right;
            output = CombinedPerkStorage.writeCombinedPerkStack(icon, mergedEntries);
        } else {
            output = CombinedPerkStorage.writePerkFolderStack(mergedEntries);
        }

        if (output.isEmpty()) {
            return false;
        }

        event.setOutput(output);
        event.setMaterialCost(1);
        event.setCost(LEVEL_COST_PER_ADDED_PERK * added);
        return true;
    }

    private static boolean tryApplyPerkContainerToGun(ItemStack left, ItemStack right, AnvilUpdateEvent event) {
        if (!(left.getItem() instanceof GunItem) || !CombinedPerkStorage.isPerkFolderStack(right)) {
            return false;
        }

        List<PerkInstance> entries = CombinedPerkStorage.getPerkEntriesFromStack(right);
        if (entries.isEmpty()) {
            return false;
        }

        ItemStack output = left.copy();
        output.setCount(1);
        GunData data = GunData.from(output);

        int applied = 0;
        boolean changed = false;
        for (PerkInstance entry : entries) {
            if (entry == null || entry.perk() == null || !data.canApplyPerk(entry.perk())) {
                continue;
            }

            Perk.Type type = entry.perk().type;
            short entryLevel = (short) Math.max(entry.level(), 1);
            Perk current = data.perk.get(type);

            if (current == null) {
                data.perk.set(entry.perk(), entryLevel);
                applied++;
                changed = true;
                continue;
            }

            if (current.name.equals(entry.perk().name)) {
                short currentLevel = data.perk.getLevel(type);
                if (entryLevel > currentLevel) {
                    data.perk.set(entry.perk(), entryLevel);
                    changed = true;
                }
                applied++;
                continue;
            }

            LinkedHashMap<String, PerkInstance> extras = CombinedPerkStorage.toUniqueMap(CombinedPerkStorage.getExtraPerks(data, type));
            extras.remove(current.name);
            int before = extras.size();
            extras.putIfAbsent(entry.perk().name, new PerkInstance(entry.perk(), entryLevel));
            if (extras.size() != before) {
                CombinedPerkStorage.setExtraPerks(data, type, new ArrayList<>(extras.values()));
                applied++;
                changed = true;
            }
        }

        if (!changed || applied <= 0) {
            return false;
        }

        data.save();
        event.setOutput(output);
        event.setMaterialCost(1);
        event.setCost(Math.max(1, LEVEL_COST_PER_ADDED_PERK * applied));
        return true;
    }

    private static void giveOrDrop(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (!player.addItem(stack)) {
            player.drop(stack, false);
        }
    }
}
