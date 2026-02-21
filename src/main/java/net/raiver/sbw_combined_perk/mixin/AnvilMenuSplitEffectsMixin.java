package net.raiver.sbw_combined_perk.mixin;

import net.raiver.sbw_combined_perk.compat.sbw.CombinedPerkStorage;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuSplitEffectsMixin {
    @Shadow @Final private DataSlot cost;
    @Shadow public int repairItemCountCost;

    @Inject(
        method = "onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void sbwCombinedPerk$replaceSplitTakeEffects(Player player, ItemStack stack, CallbackInfo ci) {
        if (!CombinedPerkStorage.hasSplitExtras(stack)) {
            return;
        }
        ItemCombinerMenuAccessor menuAccessor = (ItemCombinerMenuAccessor) (Object) this;
        Container inputSlots = menuAccessor.sbwCombinedPerk$getInputSlots();
        ContainerLevelAccess access = menuAccessor.sbwCombinedPerk$getAccess();

        if (!player.getAbilities().instabuild) {
            player.giveExperienceLevels(-this.cost.get());
        }

        float breakChance = ForgeHooks.onAnvilRepair(player, stack, inputSlots.getItem(0), inputSlots.getItem(1));
        inputSlots.setItem(0, ItemStack.EMPTY);

        if (this.repairItemCountCost > 0) {
            ItemStack right = inputSlots.getItem(1);
            if (!right.isEmpty() && right.getCount() > this.repairItemCountCost) {
                right.shrink(this.repairItemCountCost);
                inputSlots.setItem(1, right);
            } else {
                inputSlots.setItem(1, ItemStack.EMPTY);
            }
        } else {
            inputSlots.setItem(1, ItemStack.EMPTY);
        }

        this.cost.set(0);
        access.execute((level, pos) -> {
            BlockState state = level.getBlockState(pos);
            if (!player.getAbilities().instabuild && state.is(BlockTags.ANVIL) && player.getRandom().nextFloat() < breakChance) {
                BlockState damaged = AnvilBlock.damage(state);
                if (damaged == null) {
                    level.removeBlock(pos, false);
                    level.levelEvent(LevelEvent.SOUND_ANVIL_BROKEN, pos, 0);
                } else {
                    level.setBlock(pos, damaged, 2);
                    level.levelEvent(LevelEvent.SOUND_GRINDSTONE_USED, pos, 0);
                }
            } else {
                level.levelEvent(LevelEvent.SOUND_GRINDSTONE_USED, pos, 0);
            }
        });

        ci.cancel();
    }
}
