package net.raiver.sbw_combined_perk.compat.sbw;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.init.ModPerks;
import com.atsuishio.superbwarfare.item.PerkItem;
import com.atsuishio.superbwarfare.perk.Perk;
import com.atsuishio.superbwarfare.perk.PerkInstance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.raiver.sbw_combined_perk.init.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public final class CombinedPerkStorage {
    public static final String ITEM_COMBINED_TAG = "SbwCombinedPerk";
    public static final String ITEM_SPLIT_TAG = "SbwCombinedPerkSplit";
    public static final String GUN_COMBINED_TAG = "SbwCombinedPerkExtras";

    private static final String TAG_KIND = "Kind";
    private static final String TAG_TYPE = "Type";
    private static final String TAG_PERKS = "Perks";
    private static final String TAG_REFUND_LEVELS = "RefundLevels";
    private static final String TAG_NAME = "Name";
    private static final String TAG_LEVEL = "Level";

    private static final String KIND_COMBINED = "combined";
    private static final String KIND_FOLDER = "folder";

    private CombinedPerkStorage() {
    }

    public static boolean isPerkStack(ItemStack stack) {
        return stack.getItem() instanceof PerkItem;
    }

    public static boolean isPerkFolderStack(ItemStack stack) {
        return !stack.isEmpty() && ModItems.PERK_FOLDER.isPresent() && stack.is(ModItems.PERK_FOLDER.get());
    }

    public static boolean isPerkContainerStack(ItemStack stack) {
        return isPerkStack(stack) || isPerkFolderStack(stack);
    }

    public static boolean isCombinedPerkStack(ItemStack stack) {
        return isPerkStack(stack) && getPerkEntriesFromStack(stack).size() > 1;
    }

    @Nullable
    public static Perk.Type getPerkType(ItemStack stack) {
        if (isPerkFolderStack(stack)) {
            return getSingleType(getPerkEntriesFromStack(stack));
        }
        if (!(stack.getItem() instanceof PerkItem perkItem)) {
            return null;
        }
        return perkItem.getPerk().type;
    }

    public static List<PerkInstance> getPerkEntriesFromStack(ItemStack stack) {
        if (isPerkFolderStack(stack)) {
            CompoundTag root = stack.getTagElement(ITEM_COMBINED_TAG);
            if (root == null || !root.contains(TAG_PERKS, Tag.TAG_LIST)) {
                return List.of();
            }
            return readPerkList(root.getList(TAG_PERKS, Tag.TAG_COMPOUND), null);
        }

        if (!(stack.getItem() instanceof PerkItem perkItem)) {
            return List.of();
        }

        Perk basePerk = perkItem.getPerk();
        short baseLevel = normalizeLevel((short) Math.max(stack.getCount(), 1));
        CompoundTag root = stack.getTagElement(ITEM_COMBINED_TAG);
        if (root == null || !root.contains(TAG_PERKS, Tag.TAG_LIST)) {
            return List.of(new PerkInstance(basePerk, baseLevel));
        }

        List<PerkInstance> parsed = readPerkList(root.getList(TAG_PERKS, Tag.TAG_COMPOUND), basePerk.type);
        if (parsed.isEmpty()) {
            return List.of(new PerkInstance(basePerk, baseLevel));
        }
        return parsed;
    }

    public static ItemStack writeCombinedPerkStack(ItemStack iconStack, List<PerkInstance> entries) {
        LinkedHashMap<String, PerkInstance> unique = toUniqueMap(entries);
        if (unique.isEmpty()) {
            return ItemStack.EMPTY;
        }

        Perk.Type type = unique.values().iterator().next().perk().type;
        List<PerkInstance> filtered = new ArrayList<>();
        for (PerkInstance entry : unique.values()) {
            if (entry.perk().type == type) {
                filtered.add(new PerkInstance(entry.perk(), normalizeLevel(entry.level())));
            }
        }

        if (filtered.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (filtered.size() == 1) {
            return toPerkStack(filtered.get(0));
        }

        ItemStack output = iconStack.copy();
        if (!(output.getItem() instanceof PerkItem)) {
            output = toPerkStack(filtered.get(0));
        } else {
            output.setCount(1);
        }

        writeContainerTag(output, KIND_COMBINED, filtered, type);
        return output;
    }

    public static ItemStack writePerkFolderStack(List<PerkInstance> entries) {
        if (!ModItems.PERK_FOLDER.isPresent()) {
            return ItemStack.EMPTY;
        }

        LinkedHashMap<String, PerkInstance> unique = toUniqueMap(entries);
        if (unique.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack output = new ItemStack(ModItems.PERK_FOLDER.get());
        writeContainerTag(output, KIND_FOLDER, new ArrayList<>(unique.values()), null);
        return output;
    }

    public static void attachSplitExtras(ItemStack output, List<PerkInstance> extras, int refundLevels) {
        if (extras.isEmpty()) {
            output.removeTagKey(ITEM_SPLIT_TAG);
            return;
        }

        CompoundTag root = new CompoundTag();
        root.put(TAG_PERKS, writePerkList(extras, null));
        root.putInt(TAG_REFUND_LEVELS, Math.max(refundLevels, 0));
        output.getOrCreateTag().put(ITEM_SPLIT_TAG, root);
    }

    public static List<PerkInstance> consumeSplitExtras(ItemStack output) {
        CompoundTag root = output.getTagElement(ITEM_SPLIT_TAG);
        if (root == null || !root.contains(TAG_PERKS, Tag.TAG_LIST)) {
            return List.of();
        }
        List<PerkInstance> extras = readPerkList(root.getList(TAG_PERKS, Tag.TAG_COMPOUND), null);
        output.removeTagKey(ITEM_SPLIT_TAG);
        return extras;
    }

    public static boolean hasSplitExtras(ItemStack output) {
        CompoundTag root = output.getTagElement(ITEM_SPLIT_TAG);
        return root != null && root.contains(TAG_PERKS, Tag.TAG_LIST) && !root.getList(TAG_PERKS, Tag.TAG_COMPOUND).isEmpty();
    }

    public static int getSplitRefundLevels(ItemStack output) {
        CompoundTag root = output.getTagElement(ITEM_SPLIT_TAG);
        if (root == null) {
            return 0;
        }
        return Math.max(root.getInt(TAG_REFUND_LEVELS), 0);
    }

    public static ItemStack toPerkStack(PerkInstance instance) {
        ItemStack stack = instance.perk().getItem().get().getDefaultInstance();
        int count = Math.max(1, normalizeLevel(instance.level()));
        stack.setCount(Math.min(count, stack.getMaxStackSize()));
        stack.removeTagKey(ITEM_COMBINED_TAG);
        stack.removeTagKey(ITEM_SPLIT_TAG);
        stack.setRepairCost(0);
        return stack;
    }

    @Nullable
    public static Perk.Type getSingleType(Collection<PerkInstance> entries) {
        Perk.Type type = null;
        for (PerkInstance entry : entries) {
            if (entry == null || entry.perk() == null) {
                continue;
            }
            if (type == null) {
                type = entry.perk().type;
                continue;
            }
            if (entry.perk().type != type) {
                return null;
            }
        }
        return type;
    }

    @Nullable
    public static PerkInstance findByName(Collection<PerkInstance> entries, String perkName) {
        for (PerkInstance entry : entries) {
            if (entry == null || entry.perk() == null) {
                continue;
            }
            if (entry.perk().name.equals(perkName)) {
                return entry;
            }
        }
        return null;
    }

    public static List<PerkInstance> getMergedPerksForType(GunData data, Perk.Type type) {
        List<PerkInstance> result = new ArrayList<>();
        Perk basePerk = data.perk.get(type);
        if (basePerk == null) {
            return result;
        }
        result.add(new PerkInstance(basePerk, normalizeLevel(data.perk.getLevel(type))));
        result.addAll(getExtraPerks(data, type));
        return result;
    }

    public static List<PerkInstance> getAllExtraPerks(GunData data) {
        List<PerkInstance> result = new ArrayList<>();
        for (Perk.Type type : Perk.Type.values()) {
            if (data.perk.get(type) == null) {
                continue;
            }
            result.addAll(getExtraPerks(data, type));
        }
        return result;
    }

    public static List<PerkInstance> getExtraPerks(GunData data, Perk.Type type) {
        CompoundTag root = getGunRoot(data, false);
        if (root == null || !root.contains(type.getName(), Tag.TAG_LIST)) {
            return List.of();
        }
        return readPerkList(root.getList(type.getName(), Tag.TAG_COMPOUND), type);
    }

    public static void setExtraPerks(GunData data, Perk.Type type, List<PerkInstance> extras) {
        CompoundTag root = getGunRoot(data, true);
        ListTag list = writePerkList(extras, type);

        if (list.isEmpty()) {
            root.remove(type.getName());
        } else {
            root.put(type.getName(), list);
        }

        if (root.getAllKeys().isEmpty()) {
            data.perk().remove(GUN_COMBINED_TAG);
        }
    }

    public static List<ItemStack> toIconStacks(Collection<PerkInstance> perks) {
        List<ItemStack> icons = new ArrayList<>();
        for (PerkInstance instance : perks) {
            icons.add(toPerkStack(instance));
        }
        return icons;
    }

    public static int mergeUnique(Map<String, PerkInstance> target, Collection<PerkInstance> additions) {
        int added = 0;
        for (PerkInstance entry : additions) {
            if (entry == null || entry.perk() == null) {
                continue;
            }
            String key = entry.perk().name;
            if (target.containsKey(key)) {
                continue;
            }
            target.put(key, new PerkInstance(entry.perk(), normalizeLevel(entry.level())));
            added++;
        }
        return added;
    }

    public static LinkedHashMap<String, PerkInstance> toUniqueMap(Collection<PerkInstance> entries) {
        LinkedHashMap<String, PerkInstance> map = new LinkedHashMap<>();
        mergeUnique(map, entries);
        return map;
    }

    private static void writeContainerTag(ItemStack output, String kind, List<PerkInstance> entries, @Nullable Perk.Type forcedType) {
        CompoundTag root = new CompoundTag();
        root.putString(TAG_KIND, kind);
        if (forcedType != null) {
            root.putString(TAG_TYPE, forcedType.getName());
        }
        root.put(TAG_PERKS, writePerkList(entries, forcedType));
        output.getOrCreateTag().put(ITEM_COMBINED_TAG, root);
        output.removeTagKey(ITEM_SPLIT_TAG);
        output.setRepairCost(0);
    }

    private static ListTag writePerkList(Collection<PerkInstance> perks, @Nullable Perk.Type forcedType) {
        ListTag list = new ListTag();
        for (PerkInstance entry : perks) {
            if (entry == null || entry.perk() == null) {
                continue;
            }
            if (forcedType != null && entry.perk().type != forcedType) {
                continue;
            }
            CompoundTag perkTag = new CompoundTag();
            perkTag.putString(TAG_NAME, entry.perk().name);
            perkTag.putShort(TAG_LEVEL, normalizeLevel(entry.level()));
            list.add(perkTag);
        }
        return list;
    }

    @Nullable
    private static CompoundTag getGunRoot(GunData data, boolean create) {
        CompoundTag perkTag = data.perk();
        if (!perkTag.contains(GUN_COMBINED_TAG, Tag.TAG_COMPOUND)) {
            if (!create) {
                return null;
            }
            perkTag.put(GUN_COMBINED_TAG, new CompoundTag());
        }
        return perkTag.getCompound(GUN_COMBINED_TAG);
    }

    private static List<PerkInstance> readPerkList(ListTag listTag, @Nullable Perk.Type forcedType) {
        LinkedHashMap<String, PerkInstance> map = new LinkedHashMap<>();
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag perkTag = listTag.getCompound(i);
            String name = perkTag.getString(TAG_NAME);
            if (name.isBlank()) {
                continue;
            }
            Perk perk = findPerkByName(name, forcedType);
            if (perk == null) {
                continue;
            }
            map.putIfAbsent(perk.name, new PerkInstance(perk, normalizeLevel(perkTag.getShort(TAG_LEVEL))));
        }
        return new ArrayList<>(map.values());
    }

    @Nullable
    private static Perk findPerkByName(String name, @Nullable Perk.Type type) {
        for (RegistryObject<Perk> entry : allPerks()) {
            Perk perk = entry.get();
            if (!perk.name.equals(name)) {
                continue;
            }
            if (type != null && perk.type != type) {
                continue;
            }
            return perk;
        }
        return null;
    }

    private static List<RegistryObject<Perk>> allPerks() {
        List<RegistryObject<Perk>> perks = new ArrayList<>();
        perks.addAll(ModPerks.AMMO_PERKS.getEntries());
        perks.addAll(ModPerks.FUNC_PERKS.getEntries());
        perks.addAll(ModPerks.DAMAGE_PERKS.getEntries());
        return perks;
    }

    private static short normalizeLevel(short level) {
        return (short) Math.max(level, 1);
    }
}
