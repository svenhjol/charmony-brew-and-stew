package svenhjol.charmony.brew_and_stew.common.features.casks;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import svenhjol.charmony.core.Charmony;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.PlayerHelper;

public class Networking extends Setup<Casks> {
    public Networking(Casks feature) {
        super(feature);
    }

    public record S2CAddedToCask(BlockPos pos) implements CustomPacketPayload {
        public static Type<S2CAddedToCask> TYPE = new Type<>(Charmony.id("added_to_cask"));
        public static StreamCodec<FriendlyByteBuf, S2CAddedToCask> CODEC =
            StreamCodec.of(S2CAddedToCask::encode, S2CAddedToCask::decode);

        public static void send(ServerLevel level, BlockPos pos) {
            PlayerHelper.getPlayersInRange(level, pos, 8.0d)
                .forEach(player -> ServerPlayNetworking.send((ServerPlayer) player, new S2CAddedToCask(pos)));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static void encode(FriendlyByteBuf buf, S2CAddedToCask self) {
            buf.writeBlockPos(self.pos);
        }

        private static S2CAddedToCask decode(FriendlyByteBuf buf) {
            return new S2CAddedToCask(buf.readBlockPos());
        }
    }
}
