package svenhjol.charmony.brew_and_stew.common.mixins.casks;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charmony.brew_and_stew.common.features.casks.Casks;

@Mixin(PotionBrewing.class)
public class PotionBrewingMixin {
    @ModifyReturnValue(
        method = "mix",
        at = @At("RETURN")
    )
    private ItemStack hookMix(ItemStack original,
                              @Local(ordinal = 0, argsOnly = true) ItemStack itemStack,
                              @Local(ordinal = 1, argsOnly = true) ItemStack itemStack2
    ) {
        var opt = Casks.feature().handlers.restoreCustomPotionEffects(itemStack2, original);
        return opt.orElse(original);
    }
}
