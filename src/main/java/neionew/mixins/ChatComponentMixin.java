package neionew.mixins;

import neionew.Config;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ncore.TextUtils.unformattedText;
import static org.apache.commons.lang3.Strings.CI;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    @Inject(method = "addMessage", at = @At("HEAD"), cancellable = true)
    private void var5(Component contents, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
        String mString = unformattedText(contents.getString());
        if (CI.startsWithAny(mString, "Profile ID: ", "Latest update: ", "Welcome to Hypixel SkyBlock!", "You are playing on profile:", "Your radio is weak. Find another enjoyer to boost it.", "Healing effects are not effective when out of breath!") || (Config.scannerMain() && CI.startsWithAny(mString, "Sending to server mini", "Warping"))) ci.cancel();
    }
}