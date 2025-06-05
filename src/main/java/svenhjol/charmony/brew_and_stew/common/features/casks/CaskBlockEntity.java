package svenhjol.charmony.brew_and_stew.common.features.casks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import svenhjol.charmony.core.common.SyncedBlockEntity;

import javax.annotation.Nullable;
import java.util.*;

public class CaskBlockEntity extends SyncedBlockEntity implements Nameable {
    public static final String NAME_TAG = "name";
    public static final String BOTTLES_TAG = "bottles";
    public static final String EFFECTS_TAG = "effects";
    public static final String DURATIONS_TAG = "duration";
    public static final String AMPLIFIERS_TAG = "amplifier";
    public static final String DILUTIONS_TAG = "dilutions";
    public static final String FERMENTATION_TAG = "fermentation";

    // Mapped by data components.
    public Component name;
    public int bottles = 0;
    public double fermentation = CaskData.DEFAULT_FERMENTATION;
    public Map<ResourceLocation, Integer> durations = new HashMap<>();
    public Map<ResourceLocation, Integer> amplifiers = new HashMap<>();
    public Map<ResourceLocation, Integer> dilutions = new HashMap<>();
    public List<ResourceLocation> effects = new ArrayList<>();

    public CaskBlockEntity(BlockPos pos, BlockState state) {
        super(feature().registers.blockEntity.get(), pos, state);
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);

        valueInput.getString(NAME_TAG).ifPresent(s -> this.name = Component.literal(s));
        valueInput.getInt(BOTTLES_TAG).ifPresent(i -> this.bottles = i);
        this.fermentation = valueInput.getDoubleOr(FERMENTATION_TAG, CaskData.DEFAULT_FERMENTATION);

        this.effects.clear();
        this.durations.clear();
        this.amplifiers.clear();
        this.dilutions.clear();

        valueInput.read(EFFECTS_TAG, ResourceLocation.CODEC.listOf())
            .ifPresent(effects -> this.effects = new ArrayList<>(effects));

        var durations = valueInput.read(DURATIONS_TAG, CompoundTag.CODEC);
        var amplifiers = valueInput.read(AMPLIFIERS_TAG, CompoundTag.CODEC);
        var dilutions = valueInput.read(DILUTIONS_TAG, CompoundTag.CODEC);

        for (var effect : effects) {
            var str = effect.toString();
            this.durations.put(effect, durations.flatMap(tag -> tag.getInt(str)).orElse(0));
            this.amplifiers.put(effect, amplifiers.flatMap(tag -> tag.getInt(str)).orElse(0));
            this.dilutions.put(effect, dilutions.flatMap(tag -> tag.getInt(str)).orElse(0));
        }
    }

    @Override
    public void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);

        if (this.name != null) {
            valueOutput.putString(NAME_TAG, this.name.toString());
        }

        valueOutput.putInt(BOTTLES_TAG, this.bottles);
        valueOutput.putDouble(FERMENTATION_TAG, this.fermentation);

        CompoundTag durations = new CompoundTag();
        CompoundTag amplifiers = new CompoundTag();
        CompoundTag dilutions = new CompoundTag();

        for (var effect : this.effects) {
            var str = effect.toString();
            durations.putInt(str, this.durations.get(effect));
            amplifiers.putInt(str, this.amplifiers.get(effect));
            dilutions.putInt(str, this.dilutions.get(effect));
        }

        valueOutput.store(EFFECTS_TAG, ResourceLocation.CODEC.listOf(), this.effects);
        valueOutput.store(DURATIONS_TAG, CompoundTag.CODEC, durations);
        valueOutput.store(AMPLIFIERS_TAG, CompoundTag.CODEC, amplifiers);
        valueOutput.store(DILUTIONS_TAG, CompoundTag.CODEC, dilutions);
    }

    @SuppressWarnings("Java8MapApi")
    public boolean add(ItemStack input) {
        if (!feature().handlers.isValidPotion(input)) {
            return false;
        }

        var potion = feature().handlers.getPotion(input);
        var potionEffects = Optional.ofNullable(potion).map(p -> p.value().getEffects()).orElse(List.of());
        var potionCustomEffects = Optional.ofNullable(input.get(DataComponents.POTION_CONTENTS))
            .map(PotionContents::customEffects)
            .orElse(List.of());


        if (bottles < feature().maxBottles()) {

            // Reset effects if fresh cask
            if (bottles == 0) {
                this.effects.clear();
            }

            // Potions without effects just dilute the mix
            if (potion != Potions.WATER || !potionCustomEffects.isEmpty()) {
                var currentEffects = potionCustomEffects.isEmpty() && !potionEffects.isEmpty() ? potionEffects : potionCustomEffects;

                // Strip out immediate effects and other weird things
                currentEffects = currentEffects.stream()
                    .filter(e -> e.getDuration() > 1)
                    .toList();

                currentEffects.forEach(effect -> {
                    var changedAmplifier = false;
                    var duration = effect.getDuration();
                    var amplifier = effect.getAmplifier();
                    var type = effect.getEffect();
                    var effectId = BuiltInRegistries.MOB_EFFECT.getKey(type.value());

                    if (effectId == null) {
                        return;
                    }

                    if (!effects.contains(effectId)) {
                        effects.add(effectId);
                    }

                    if (amplifiers.containsKey(effectId)) {
                        int existingAmplifier = amplifiers.get(effectId);
                        changedAmplifier = amplifier != existingAmplifier;
                    }
                    amplifiers.put(effectId, amplifier);

                    if (!durations.containsKey(effectId)) {
                        durations.put(effectId, duration);
                    } else {
                        var existingDuration = durations.get(effectId);
                        if (changedAmplifier) {
                            durations.put(effectId, duration);
                        } else {
                            durations.put(effectId, existingDuration + duration);
                        }
                    }
                });
            }

            bottles++;

            effects.forEach(effectId -> {
                if (!dilutions.containsKey(effectId)) {
                    dilutions.put(effectId, bottles);
                } else {
                    int existingDilution = dilutions.get(effectId);
                    dilutions.put(effectId, existingDilution + 1);
                }
            });

            if (level != null) {
                level.playSound(null, getBlockPos(), feature().registers.addSound.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
            }

            setChanged();
            return true;
        }

        return false;
    }

    @Nullable
    public ItemStack take() {
        if (this.bottles > 0) {
            var bottle = getBottle();
            removeBottle();

            // Play sound
            if (level != null) {
                var pos = getBlockPos();
                if (bottles > 0) {
                    level.playSound(null, pos, feature().registers.takeSound.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                } else {
                    level.playSound(null, pos, feature().registers.emptySound.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }

            return bottle;
        }

        return null;
    }

    private ItemStack getBottle() {
        // create a potion from the cask's contents
        List<MobEffectInstance> effects = new ArrayList<>();

        for (var effectId : this.effects) {
            var effect = BuiltInRegistries.MOB_EFFECT.get(effectId).orElse(null);
            if (effect == null) continue;

            int duration = this.durations.get(effectId);
            int amplifier = this.amplifiers.get(effectId);
            int dilution = this.dilutions.get(effectId);

            effects.add(new MobEffectInstance(effect, duration / dilution, amplifier));
        }

        if (!effects.isEmpty()) {
            var bottle = feature().handlers.getBasePotionBottle();
            var data = new PotionContents(Optional.empty(), Optional.empty(), effects, Optional.empty());
            bottle.set(DataComponents.POTION_CONTENTS, data);
            
            var customName = this.name != null ? this.name : Component.translatable("item.charmony.home_brew");
            bottle.set(DataComponents.CUSTOM_NAME, customName);
            return feature().handlers.makeCustomPotion(customName, effects);
        } else {
            return feature().handlers.getFilledWaterBottle();
        }
    }

    private void removeBottle() {
        // if no more bottles in the cask, flush out the cask data
        if (--bottles <= 0) {
            this.flush();
        }

        setChanged();
    }

    private void flush() {
        this.effects.clear();
        this.durations.clear();
        this.dilutions.clear();
        this.amplifiers.clear();
        this.bottles = 0;
        this.fermentation = 1.0d;

        setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public Component getName() {
        if (name != null) {
            return name;
        }
        return this.getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }

    Component getDefaultName() {
        return Component.translatable("container.charmony.cask");
    }

    protected static Casks feature() {
        return Casks.feature();
    }
}
