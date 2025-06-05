package svenhjol.charmony.brew_and_stew.common.features.casks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import svenhjol.charmony.core.base.Setup;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class Handlers extends Setup<Casks> {
    public Handlers(Casks feature) {
        super(feature);
    }

    public ItemStack getBasePotionBottle() {
        var out = new ItemStack(Items.POTION);
        out.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.AWKWARD));
        return out;
    }

    public ItemStack getFilledWaterBottle() {
        var out = new ItemStack(Items.POTION);
        out.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
        return out;
    }

    public boolean isValidPotion(ItemStack potion) {
        boolean valid = potion.is(Items.POTION);

        if (!valid && feature().allowSplashAndLingering()) {
            valid = potion.is(Items.LINGERING_POTION) || potion.is(Items.SPLASH_POTION);
        }

        return valid;
    }

    @Nullable
    public Holder<Potion> getPotion(ItemStack stack) {
        return Optional.ofNullable(stack.get(DataComponents.POTION_CONTENTS))
            .flatMap(PotionContents::potion)
            .orElse(null);
    }

    public ItemStack makeCustomPotion(Component customName, List<MobEffectInstance> effects) {
        var stack = feature().handlers.getBasePotionBottle();
        var base = getPotion(stack);
        if (base == null) {
            return stack;
        }

        var newPotionContents = new PotionContents(Optional.of(base), Optional.empty(), effects, Optional.empty());
        stack.set(DataComponents.POTION_CONTENTS, newPotionContents);
        stack.set(DataComponents.CUSTOM_NAME, customName);

        return stack;
    }

    public InteractionResult playerAddToCask(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                             InteractionHand hand, BlockHitResult hitResult) {
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CaskBlockEntity cask) {
            if (stack.getItem() == Items.NAME_TAG && stack.has(DataComponents.CUSTOM_NAME)) {

                if (!level.isClientSide()) {
                    // Name the cask using a name tag.
                    cask.name = stack.getHoverName();
                    cask.setChanged();

                    level.playSound(null, pos, feature().registers.nameSound.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                    stack.shrink(1);
                }
                
                return InteractionResult.SUCCESS;

            } else if (stack.is(Items.GLASS_BOTTLE)) {

                // Take a bottle of liquid from the cask using a glass bottle.
                if (!level.isClientSide()) {
                    var out = cask.take();
                    if (out != null) {
                        player.getInventory().add(out);

                        stack.shrink(1);

                        if (cask.effects.size() > 1) {
                            feature().advancements.tookLiquidFromCask(player);
                        }
                    }
                }
                return InteractionResult.SUCCESS;

            } else if (isValidPotion(stack)) {

                // Add a bottle of liquid to the cask using a filled glass bottle.
                if (!level.isClientSide()) {
                    var result = cask.add(stack);
                    if (result) {
                        stack.shrink(1);

                        // give the glass bottle back to the player
                        player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));

                        if (!level.isClientSide()) {
                            // Let nearby players know an item was added to the cask
                            Networking.S2CAddedToCask.send((ServerLevel) level, pos);

                            // do advancement for filling with potions
                            if (cask.bottles > 1 && cask.effects.size() > 1) {
                                feature().advancements.addedLiquidToCask(player);
                            }
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    public Optional<ItemStack> restoreCustomPotionEffects(ItemStack original, ItemStack result) {
        var homeBrewData = feature().registers.homeBrewData.get();
        
        if (!original.has(homeBrewData)) {
            // Do vanilla behavior if the original potion is not a home brew (from the cask).
            return Optional.empty();
        }

        var originalPotionContents = original.get(DataComponents.POTION_CONTENTS);
        if (originalPotionContents == null) {
            return Optional.empty();
        }

        var originalCustomName = original.get(DataComponents.CUSTOM_NAME);
        var originalHomeBrewData = original.get(homeBrewData);
        var originalCustomEffects = originalPotionContents.customEffects();

        var resultPotionContents = result.get(DataComponents.POTION_CONTENTS);
        if ((resultPotionContents == null || resultPotionContents.customEffects().isEmpty())
            && !originalCustomEffects.isEmpty()) {
            var copy = result.copy();

            copy.set(DataComponents.POTION_CONTENTS, originalPotionContents);
            if (originalCustomName != null) {
                copy.set(DataComponents.CUSTOM_NAME, originalCustomName);
            }

            copy.set(homeBrewData, originalHomeBrewData);
            return Optional.of(copy);
        }
        
        return Optional.empty();
    }
}
