package dev.fishy.hidesandhandicrafts.item;

import com.mojang.serialization.DataResult;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.math.Fraction;
import org.jspecify.annotations.Nullable;

public class SatchelItem extends Item {
    public static final int MAX_SHOWN_GRID_ITEMS_X = 4;
    public static final int MAX_SHOWN_GRID_ITEMS_Y = 3;
    public static final int MAX_SHOWN_GRID_ITEMS = 12;
    public static final int OVERFLOWING_MAX_SHOWN_GRID_ITEMS = 11;
    private static final int FULL_BAR_COLOR = ARGB.colorFromFloat(1.0F, 1.0F, 0.33F, 0.33F);
    private static final int BAR_COLOR = ARGB.colorFromFloat(1.0F, 0.44F, 0.53F, 1.0F);
    private static final Fraction CAPACITY = Fraction.getFraction(3, 1);

    public SatchelItem(final Item.Properties properties) {
        super(properties);
    }

    private static Fraction getWeightSafe(final SatchelContents contents) {
        DataResult<Fraction> result = contents.weight();
        Fraction rawWeight = result.isSuccess() ? result.getOrThrow() : CAPACITY;
        return rawWeight.divideBy(CAPACITY);
    }

    public static float getFullnessDisplay(final ItemStack itemStack) {
        SatchelContents contents = itemStack.getOrDefault(ModDataComponents.SATCHEL_CONTENTS, SatchelContents.EMPTY);
        return getWeightSafe(contents).floatValue();
    }

    @Override
    public boolean overrideStackedOnOther(final ItemStack self, final Slot slot, final ClickAction clickAction, final Player player) {
        SatchelContents initialContents = self.get(ModDataComponents.SATCHEL_CONTENTS);
        if (initialContents == null) {
            return false;
        }

        ItemStack other = slot.getItem();
        SatchelContents.Mutable contents = new SatchelContents.Mutable(initialContents);
        if (clickAction == ClickAction.PRIMARY && !other.isEmpty()) {
            if (contents.tryTransfer(slot, player) > 0) {
                playInsertSound(player);
            } else {
                playInsertFailSound(player);
            }

            self.set(ModDataComponents.SATCHEL_CONTENTS, contents.toImmutable());
            this.broadcastChangesOnContainerMenu(player);
            return true;
        } else if (clickAction == ClickAction.SECONDARY && other.isEmpty()) {
            ItemStack itemStack = contents.removeOne();
            if (itemStack != null) {
                ItemStack remainder = slot.safeInsert(itemStack);
                if (remainder.getCount() > 0) {
                    contents.tryInsert(remainder);
                } else {
                    playRemoveOneSound(player);
                }
            }

            self.set(ModDataComponents.SATCHEL_CONTENTS, contents.toImmutable());
            this.broadcastChangesOnContainerMenu(player);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            final ItemStack self, final ItemStack other, final Slot slot, final ClickAction clickAction, final Player player, final SlotAccess carriedItem
    ) {
        if (clickAction == ClickAction.PRIMARY && other.isEmpty()) {
            toggleSelectedItem(self, -1);
            return false;
        }

        SatchelContents initialContents = self.get(ModDataComponents.SATCHEL_CONTENTS);
        if (initialContents == null) {
            return false;
        }

        SatchelContents.Mutable contents = new SatchelContents.Mutable(initialContents);
        if (clickAction == ClickAction.PRIMARY && !other.isEmpty()) {
            if (slot.allowModification(player) && contents.tryInsert(other) > 0) {
                playInsertSound(player);
            } else {
                playInsertFailSound(player);
            }

            self.set(ModDataComponents.SATCHEL_CONTENTS, contents.toImmutable());
            this.broadcastChangesOnContainerMenu(player);
            return true;
        } else if (clickAction == ClickAction.SECONDARY && other.isEmpty()) {
            if (slot.allowModification(player)) {
                ItemStack removed = contents.removeOne();
                if (removed != null) {
                    playRemoveOneSound(player);
                    carriedItem.set(removed);
                }
            }

            self.set(ModDataComponents.SATCHEL_CONTENTS, contents.toImmutable());
            this.broadcastChangesOnContainerMenu(player);
            return true;
        } else {
            toggleSelectedItem(self, -1);
            return false;
        }
    }

    @Override
    public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }

    private void dropContent(final Level level, final Player player, final ItemStack itemStack) {
        if (this.dropContent(itemStack, player)) {
            playDropContentsSound(level, player);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }
    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }
    @Override
    public boolean isBarVisible(final ItemStack stack) {
        SatchelContents contents = stack.getOrDefault(ModDataComponents.SATCHEL_CONTENTS, SatchelContents.EMPTY);
        return getWeightSafe(contents).compareTo(Fraction.ZERO) > 0;
    }

    @Override
    public int getBarWidth(final ItemStack stack) {
        SatchelContents contents = stack.getOrDefault(ModDataComponents.SATCHEL_CONTENTS, SatchelContents.EMPTY);
        return Math.min(1 + Mth.mulAndTruncate(getWeightSafe(contents), 12), 13);
    }

    @Override
    public int getBarColor(final ItemStack stack) {
        SatchelContents contents = stack.getOrDefault(ModDataComponents.SATCHEL_CONTENTS, SatchelContents.EMPTY);
        return getWeightSafe(contents).compareTo(Fraction.ONE) >= 0 ? FULL_BAR_COLOR : BAR_COLOR;
    }

    public static void toggleSelectedItem(final ItemStack stack, final int selectedItem) {
        SatchelContents initialContents = stack.get(ModDataComponents.SATCHEL_CONTENTS);
        if (initialContents != null) {
            SatchelContents.Mutable contents = new SatchelContents.Mutable(initialContents);
            contents.toggleSelectedItem(selectedItem);
            stack.set(ModDataComponents.SATCHEL_CONTENTS, contents.toImmutable());
        }
    }

    public static int getSelectedItemIndex(final ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.SATCHEL_CONTENTS, SatchelContents.EMPTY).getSelectedItemIndex();
    }

    public static int getNumberOfItemsToShow(final ItemStack stack) {
        SatchelContents contents = stack.getOrDefault(ModDataComponents.SATCHEL_CONTENTS, SatchelContents.EMPTY);
        return contents.getNumberOfItemsToShow();
    }

    private boolean dropContent(final ItemStack satchel, final Player player) {
        SatchelContents contents = satchel.get(ModDataComponents.SATCHEL_CONTENTS);
        if (contents != null && !contents.isEmpty()) {
            Optional<ItemStack> itemStack = removeOneItemFromBundle(satchel, player, contents);
            if (itemStack.isPresent()) {
                player.drop(itemStack.get(), true);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static Optional<ItemStack> removeOneItemFromBundle(final ItemStack self, final Player player, final SatchelContents initialContents) {
        SatchelContents.Mutable contents = new SatchelContents.Mutable(initialContents);
        ItemStack removed = contents.removeOne();
        if (removed != null) {
            playRemoveOneSound(player);
            self.set(ModDataComponents.SATCHEL_CONTENTS, contents.toImmutable());
            return Optional.of(removed);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void onUseTick(final Level level, final LivingEntity livingEntity, final ItemStack itemStack, final int ticksRemaining) {
        if (livingEntity instanceof Player player) {
            int useDuration = this.getUseDuration(itemStack, livingEntity);
            boolean isFirstTick = ticksRemaining == useDuration;
            if (isFirstTick || ticksRemaining < useDuration - 10 && ticksRemaining % 2 == 0) {
                this.dropContent(level, player, itemStack);
            }
        }
    }

    @Override
    public int getUseDuration(final ItemStack itemStack, final LivingEntity entity) {
        return 200;
    }

    @Override
    public ItemUseAnimation getUseAnimation(final ItemStack itemStack) {
        return ItemUseAnimation.BUNDLE;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(final ItemStack satchel) {
        // TODO (later step): swap this for a real satchel tooltip renderer.
        // Vanilla's BundleTooltip only accepts a real BundleContents, which we no longer use,
        // so for now the hover-preview grid is just disabled.
        return Optional.empty();
    }

    @Override
    public void onDestroyed(final ItemEntity entity) {
        SatchelContents contents = entity.getItem().get(ModDataComponents.SATCHEL_CONTENTS);
        if (contents != null) {
            entity.getItem().set(ModDataComponents.SATCHEL_CONTENTS, SatchelContents.EMPTY);
            ItemUtils.onContainerDestroyed(entity, contents.itemCopyStream());
        }
    }

    private static void playRemoveOneSound(final Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertSound(final Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertFailSound(final Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
    }

    private static void playDropContentsSound(final Level level, final Entity entity) {
        level.playSound(
                null, entity.blockPosition(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.PLAYERS, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F
        );
    }

    private void broadcastChangesOnContainerMenu(final Player player) {
        AbstractContainerMenu containerMenu = player.containerMenu;
        if (containerMenu != null) {
            containerMenu.slotsChanged(player.getInventory());
        }
    }
}