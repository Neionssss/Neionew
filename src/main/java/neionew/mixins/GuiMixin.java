package neionew.mixins;

import neionew.ClickGUI;
import neionew.Config;
import neionew.SelectorScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    private @Nullable Screen screen;

    @Redirect(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;releaseAll()V"))
    private void var5() {
        if (!Config.lInventoryMove() || !(screen instanceof ClickGUI || screen instanceof SelectorScreen || screen instanceof PauseScreen)) KeyMapping.releaseAll();
    }
}
