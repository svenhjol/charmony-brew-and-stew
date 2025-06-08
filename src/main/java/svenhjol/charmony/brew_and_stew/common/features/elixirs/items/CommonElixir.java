package svenhjol.charmony.brew_and_stew.common.features.elixirs.items;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import svenhjol.charmony.api.elixirs.ElixirDefinition;
import svenhjol.charmony.brew_and_stew.common.features.elixirs.Elixirs;

import java.util.ArrayList;
import java.util.List;

public class CommonElixir implements ElixirDefinition {
    @Override
    public String id() {
        return "common_elixir";
    }

    @Override
    public MutableComponent name(RandomSource random) {
        return feature().handlers.prefixedPotionName(random);
    }

    @Override
    public int numberOfEffects(RandomSource random) {
        return random.nextDouble() < 0.15d ? 2 : 1;
    }

    @Override
    public List<Holder<MobEffect>> validEffects(HolderGetter.Provider provider) {
        var log = feature().log();
        var ids = feature().commonElixirEffects();
        var registry = provider.lookupOrThrow(Registries.MOB_EFFECT);
        List<Holder<MobEffect>> effects = new ArrayList<>();

        for (var id : ids) {
            var res = ResourceLocation.tryParse(id);
            if (res == null) {
                log.debug("Could not parse " + id);
                continue;
            }

            var key = ResourceKey.create(Registries.MOB_EFFECT, res);
            var ref = registry.get(key).orElse(null);
            if (ref == null) {
                log.debug("Could not get holder for key " + key);
                continue;
            }

            effects.add(ref);
        }

        return effects;
    }

    @Override
    public int minAmplifier() {
        return feature().commonElixirMinAmplifier();
    }

    @Override
    public int maxAmplifier() {
        return feature().commonElixirMaxAmplifier();
    }

    private Elixirs feature() {
        return Elixirs.feature();
    }
}
