package neionew.mixins;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import ncore.NActionButton;
import neionew.Config;
import neionew.LocationChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static ncore.TextUtils.drawWrappedText;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl$PackConfirmScreen")
public abstract class PackConfirmScreenMixin extends ConfirmScreen {

    @Unique
    private NActionButton yesButton;
    @Unique
    private NActionButton noButton;

    @Unique
    private int lastWidth;
    @Unique
    private int lastHeight;
    @Unique
    private long lClicked;

    public PackConfirmScreenMixin(BooleanConsumer callback, Component title, Component message) {
        super(callback, title, message);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(ClientCommonPacketListenerImpl pl, Minecraft mc, Screen parentScreen, List requests, boolean required, Component prompt, CallbackInfo ci) {
        if (Objects.equals(Config.packMode(), "Auto-Accept") && String.valueOf(LocationChecker.connection.getA().getRemoteAddress()).contains("hypixel.net")) callback.accept(true);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        var conn = LocationChecker.connection;
        if (!Objects.equals(Config.packMode(), "Disabled") || !String.valueOf(conn.getA().getRemoteAddress()).contains("hypixel.net")) return;
        int baseY = height / 2;
        if (width != lastWidth || height != lastHeight) {
            yesButton = new NActionButton(width / 2 - 100, baseY, 200, 25, "Auto-Accept", Color.green, () -> {
                Config.set("Server Pack Mode", "Auto-Accept");
                callback.accept(true);
            });
            noButton = new NActionButton(width / 2 - 100, baseY + 30, 200, 25, "Auto Reject", Color.red, () -> {
                Config.set("Server Pack Mode", "Auto-Reject");
                conn.getA().send(new ServerboundResourcePackPacket(conn.getB(), ServerboundResourcePackPacket.Action.ACCEPTED));
                conn.getA().send(new ServerboundResourcePackPacket(conn.getB(), ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED));
            });
            lastWidth = width;
            lastHeight = height;
        }
        Component message = Component.literal("Select Override Server Resource Pack Mode\n\n").withColor(Color.WHITE.getRGB()).append(Component.literal(" If you use own pack or like null items more, select Auto-Reject, otherwise use Auto-Accept").withColor(Color.CYAN.getRGB()));
        drawWrappedText(context, font, message, width / 2, baseY - 30, width - 50);
        yesButton.draw(context, mouseX, mouseY);
        noButton.draw(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(final @NotNull MouseButtonEvent event, final boolean doubleClick) {
        if (System.currentTimeMillis() - lClicked > 1000) {
            yesButton.mouseClicked(event);
            noButton.mouseClicked(event);
            lClicked = System.currentTimeMillis();
        }
        return false;
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        return false;
    }
}
