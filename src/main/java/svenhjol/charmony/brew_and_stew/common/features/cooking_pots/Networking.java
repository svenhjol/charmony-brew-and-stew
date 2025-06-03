package svenhjol.charmony.brew_and_stew.common.features.cooking_pots;

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

public final class Networking extends Setup<CookingPots> {
    public Networking(CookingPots feature) {
        super(feature);
    }

    // Server-to-client packet to send the position of the cooking pot to the client.
    public record S2CAddedToCookingPot(BlockPos pos) implements CustomPacketPayload {
        public static Type<S2CAddedToCookingPot> TYPE = new Type<>(Charmony.id("added_to_cooking_pot"));
        public static StreamCodec<FriendlyByteBuf, S2CAddedToCookingPot> CODEC =
            StreamCodec.of(S2CAddedToCookingPot::encode, S2CAddedToCookingPot::decode);

        public static void send(ServerLevel level, BlockPos pos) {
            PlayerHelper.getPlayersInRange(level, pos, 8.0d)
                .forEach(player -> ServerPlayNetworking.send((ServerPlayer)player, new S2CAddedToCookingPot(pos)));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static void encode(FriendlyByteBuf buf, S2CAddedToCookingPot self) {
            buf.writeBlockPos(self.pos);
        }

        private static S2CAddedToCookingPot decode(FriendlyByteBuf buf) {
            return new S2CAddedToCookingPot(buf.readBlockPos());
        }
    }
}
