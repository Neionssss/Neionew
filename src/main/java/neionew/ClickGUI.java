package neionew;

import com.mojang.blaze3d.platform.InputConstants;
import ncore.NActionButton;
import ncore.NButton;
import ncore.NSliderButton;
import neionew.gui.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.awt.Color;
import java.util.List;

import static ncore.NCore.mc;
import static neionew.Config.*;
import static neionew.LocationChecker.currentTab;

public class ClickGUI extends Screen {
    public List<Tab> tabs;

    public ClickGUI(List<Tab> tabs) {
        super(Component.literal("Neionew settings"));
        this.tabs = tabs;
    }

    @Override
    protected void init() {
        if (currentTab == null) currentTab = tabs.getFirst().category();
        loadTab();
    }

    @Override
    public void extractBackground(final @NotNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {}

    private void loadTab() {
        NButton.clearAll();
        int y = 60;
        int x = width / 2 - 100;
        int btnWidth = 200;

        for (Setting s : getByCategory(currentTab)) {
            NButton button = switch (s.type) {
                case BOOLEAN -> new NToggleButton(x, y, btnWidth, 20, s.name);
                case SELECTOR -> new NActionButton(x, y, btnWidth, 20, s.name, new Color(0, 177, 200), () -> mc.gui.setScreen(new SelectorScreen(s, s.btnList, this)));
                case NUMBER -> new NSliderButton(x, y, btnWidth, 20, s.name, s.min, s.max, ((Number) s.value).intValue(), val -> set(s.name, val));
                case KEYBIND -> new NKeyBind(x, y, btnWidth, 20, s.name);
            };

            if (!s.subSettings.isEmpty() && button instanceof NToggleButton toggle) {
                int offsetY = y;
                for (Setting sub : s.subSettings) {
                    NButton subButton = switch (sub.type) {
                        case BOOLEAN -> new NToggleButton(x + btnWidth + 12, offsetY, 150, 20, sub.name);
                        case SELECTOR -> new NActionButton(x + btnWidth + 12, offsetY, 150, 20, sub.name, new Color(0, 177, 200), () -> mc.gui.setScreen(new SelectorScreen(s, s.btnList, this)));
                        case NUMBER -> new NSliderButton(x + btnWidth + 12, offsetY, 150, 20, sub.name, sub.min, sub.max, ((Number) sub.value).intValue(), val -> set(sub.name, val));
                        case KEYBIND -> new NKeyBind(x + btnWidth + 12, offsetY, 150, 20, sub.name);
                    };
                    toggle.subButtons.add(subButton);
                    offsetY += 25;
                }
            }

            NButton.addButton(button);
            y += 25;
        }

        int tabY = 30;
        for (Tab tab : tabs) {
            NButton.addButton(new NActionButton(10, tabY, 80, 20, tab.category().name(),
                    currentTab == tab.category() ? Color.orange : Color.cyan.darker(),
                    () -> {
                        currentTab = tab.category();
                        loadTab();
                    }
            ));
            tabY += 30;
        }
    }


    @Override
    public void extractRenderState(final GuiGraphicsExtractor context, final int mouseX, final int mouseY, final float a) {
        int colX = width / 2 - 100;
        int colWidth = 200;
        int topY = 60;
        int bottomY = topY + getByCategory(currentTab).size() * 25;
        context.fill(colX - 10, topY, colX - 8, bottomY, 0xFF555555); // левая полоса
        context.fill(colX + colWidth + 10, topY, colX + colWidth + 8, bottomY, 0xFF555555); // правая полоса
        NButton.getButtons().forEach(btn -> btn.draw(context, mouseX, mouseY));
        context.centeredText(mc.font, title, width / 2, 25, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(final @NonNull MouseButtonEvent event, final boolean doubleClick) {
        for (NButton b : NButton.getButtons()) { b.mouseClicked(event); }
        saveConfig();
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(final @NonNull MouseButtonEvent event, final double dx, final double dy) {
        for (NButton b : NButton.getButtons()) { b.mouseDragged(event); }
        saveConfig();
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(final @NotNull MouseButtonEvent event) {
        for (NButton b: NButton.getButtons()) { b.mouseReleased(event); }
        saveConfig();
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(final @NotNull KeyEvent event) {
        for (NButton b : NButton.getButtons()) {
            if (b instanceof NKeyBind kb && kb.keyPressed(event.key()) && event.key() == InputConstants.KEY_ESCAPE) return false;
        }
        saveConfig();
        return super.keyPressed(event);
    }
}