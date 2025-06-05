package svenhjol.charmony.brew_and_stew.common.features.casks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record HomeBrewData(double fermentation) {
    public static final Codec<HomeBrewData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.DOUBLE.fieldOf("fermentation")
            .forGetter(HomeBrewData::fermentation)
    ).apply(instance, HomeBrewData::new));

    public static final StreamCodec<FriendlyByteBuf, HomeBrewData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.DOUBLE, HomeBrewData::fermentation,
        HomeBrewData::new
    );
    
    public static final HomeBrewData EMPTY = new HomeBrewData(CaskData.DEFAULT_FERMENTATION);
}
