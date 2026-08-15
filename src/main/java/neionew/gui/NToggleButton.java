package neionew.gui;

import ncore.NButton;
import neionew.Config;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static ncore.NCore.mc;
import static ncore.RenderUtils.roundedFill;

public class NToggleButton extends NButton {
    public final List<NButton> subButtons = new ArrayList<>();
    public boolean opened = false;

    public NToggleButton(int x, int y, int width, int height, String label) {
        super(x, y, width, height, label);
    }

    @Override
    public void draw(GuiGraphicsExtractor context, double mouseX, double mouseY) {
        boolean hovered = isHovered(mouseX, mouseY);
        boolean enabled = Config.get(label);

        int baseColor;
        var green = new Color(0, 150, 0);
        var red = new Color(200, 0, 0);
        if (enabled) {
            baseColor = hovered ? green.darker().getRGB() : green.getRGB();
        } else {
            baseColor = hovered ? red.darker().getRGB() : red.getRGB();
        }

        roundedFill(context, x, y, width, height, baseColor);
        int textX = x + width / 2;
        int textY = y + (height - mc.font.lineHeight) / 2;
        context.centeredText(mc.font, label, textX, textY, Color.white.getRGB());

        if (!subButtons.isEmpty()) {
            int size = 8;
            int yellow = 0xFFFFFF44;

            if (!opened) {
                for (int i = 0; i < size; i++) {
                    int left = x + width - i;
                    int top = y + 6 + i / 2;
                    int bottom = y + height - 6 - i / 2;
                    context.fill(left, top, left + 2, bottom, yellow);
                }
            } else {
                for (int i = 0; i < size; i++) {
                    int left = x + i;
                    int top = y + 6 + i / 2;
                    int bottom = y + height - 6 - i / 2;
                    context.fill(left, top, left + 2, bottom, yellow);
                }
            }
        }

        if (opened) for (NButton b : subButtons) { b.draw(context, mouseX, mouseY); }
    }

    @Override
    protected void onClick(final MouseButtonEvent event) {
        Config.toggle(label);
    }

    @Override
    public boolean mouseClicked(final MouseButtonEvent click) {
        if (click.button() == 0 && isHovered(click.x(), click.y())) {
            onClick(click);
            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        if (click.button() == 1 && isHovered(click.x(), click.y()) && !subButtons.isEmpty()) {
            for (NButton b : NButton.getButtons()) {
                if (b instanceof NToggleButton kb && kb != this) kb.opened = false;
            }
            opened = !opened;
        }
        if (opened) for (NButton b : subButtons) b.mouseClicked(click);
        return false;
    }

    @Override
    public void mouseDragged(final MouseButtonEvent event) {
        if (opened) for (NButton b : subButtons) b.mouseDragged(event);
    }

    @Override
    public void mouseReleased(final MouseButtonEvent event) {
        if (opened) for (NButton b : subButtons) b.mouseReleased(event);
    }
}