package neionew.mixins;

import neionew.Config;
import neionew.features.*;
import neionew.features.experiments.Chronomatron;
import neionew.features.experiments.Superpairs;
import neionew.features.experiments.Ultrasequencer;
import neionew.LocationChecker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.apache.commons.lang3.Strings.CI;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen {

    @Shadow
    private Slot lastClickSlot;
    @Shadow
    @Nullable
    protected Slot hoveredSlot;
    @Unique
    private static ContainerChat containerChat;
    @Unique
    private static Chronomatron chronomatron;
    @Unique
    private static Ultrasequencer ultrasequencer;
    @Unique
    private static Superpairs superpairs;

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void var8(CallbackInfo ci) {
        containerChat = new ContainerChat();
        ultrasequencer = new Ultrasequencer();
        chronomatron = new Chronomatron();
        superpairs = new Superpairs();
    }

    @Inject(method = "extractRenderState", at = @At(value = "HEAD"))
    private void var81(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        var cn = title.getString();
        var isAnviling = Config.bookCombine() && cn.contains("Anvil");
        var isRuning = Config.runeCombine() && cn.contains("Runic Pedestal");
        if (Config.containerChat() && !isAnviling && !isRuning) containerChat.onRender(graphics, width, height, mouseX, mouseY);
        final AbstractContainerScreen<?> container = (AbstractContainerScreen<?>) (Object) this;
        var handler = container.getMenu();
        if (handler instanceof ChestMenu cm) {
            if (isRuning) RuneCombine.onContainer(cm);
            else if (isAnviling) BookCombine.onContainer(cm);
            else if (CI.startsWithAny(cn, "Ultrasequencer (", "Chronomatron (") && Config.autoExperiments()) {
                if (cn.startsWith("Chronomatron (")) chronomatron.onDraw(cm);
                if (cn.startsWith("Ultrasequencer (")) ultrasequencer.onDraw(cm);
            }
        }
    }

    @Inject(method = "extractSlot", at = @At("TAIL"))
    private void var3(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        final AbstractContainerScreen<?> container = (AbstractContainerScreen<?>) (Object) this;
        if (container.getMenu() instanceof ChestMenu cm && title.getString().startsWith("Superpairs (") && Config.autoExperiments() && LocationChecker.isInPrivateIsland() && lastClickSlot != null) superpairs.onDraw(graphics, lastClickSlot, cm.slots);
    }


    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void var10(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        var cn = title.getString();
        var isAnviling = Config.bookCombine() && cn.contains("Anvil");
        var isRunning =  Config.runeCombine() && cn.contains("Runic Pedestal");
        if (isAnviling || isRunning) {
            if (hoveredSlot == null) return;
            cir.cancel();
            var stack = hoveredSlot.getItem();
            if (stack.getItem() == Items.PLAYER_HEAD && cn.contains("Runic Pedestal")) RuneCombine.rune = stack.getDisplayName().getString();
        }
        if (Config.autoExperiments() && CI.startsWithAny(cn, "Ultrasequencer (", "Chronomatron (")) cir.cancel();
        if (Config.containerChat() && containerChat.getHovered() && !isAnviling && !isRunning) {
            cir.cancel();
            containerChat.setTyping(true);
        }
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void n24(CallbackInfo ci) {
        RuneCombine.rune = null;
        BookCombine.reset();
        var cn = title.getString();
        if (cn.startsWith("Chronomatron (")) chronomatron.reset();
        if (cn.startsWith("Untrasequencer (")) ultrasequencer.ultraSequence.clear();
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void var11(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (Config.containerChat()) containerChat.onKeyPressed(event, cir);
    }
}