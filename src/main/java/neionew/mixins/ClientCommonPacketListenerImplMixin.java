package neionew.mixins;

import neionew.Config;
import neionew.LocationChecker;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import oshi.util.tuples.Pair;

import java.util.Objects;

@Mixin(ClientCommonPacketListenerImpl.class)
public abstract class ClientCommonPacketListenerImplMixin {

    @Shadow
    @Final
    protected Connection connection;

    @Inject(method = "handleResourcePackPush", at = @At("HEAD"), cancellable = true)
    private void s(ClientboundResourcePackPushPacket packet, CallbackInfo ci) {
        var pid = packet.id();
        if (Objects.equals(Config.packMode(), "Auto-Reject")) {
            ci.cancel();
            connection.send(new ServerboundResourcePackPacket(pid, ServerboundResourcePackPacket.Action.ACCEPTED));
            connection.send(new ServerboundResourcePackPacket(pid, ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED));
        } else LocationChecker.connection = new Pair<>(connection, pid);
    }
}
