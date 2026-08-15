package neionew;

import ncore.NCore;
import net.minecraft.client.multiplayer.*;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerInput;
import oshi.util.tuples.Pair;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static ncore.NCore.mc;
import static neionew.Neionew.*;

public class LocationChecker {

    public static long lastClick = 0;
    public static Pair<Connection,UUID> connection;

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void delay(Runnable task) {
        scheduler.schedule(() -> mc.execute(task), ThreadLocalRandom.current().nextInt(250, 600), TimeUnit.MILLISECONDS);
    }

    public static void delayLow(Runnable task) {
        scheduler.schedule(() -> mc.execute(task), ThreadLocalRandom.current().nextInt(50, 150), TimeUnit.MILLISECONDS);
    }

    public static Config.Category currentTab;


    public static boolean isOnSkyblock() {
        return NCore.getGame() != null && NCore.getGame().contains("SKYBLOCK");
    }

    private static String currentLocation() {
        if (!isOnSkyblock()) return "";

        var entries = mc.getConnection().getOnlinePlayers();

        for (PlayerInfo entry : entries) {
            Component displayName = entry.getTabListDisplayName();
            if (displayName != null) {
                String line = displayName.getString();
                if (line.startsWith("Area: ")) return line;
            }
        }
        return "";
    }

    public static void useItem() {
        LocationChecker.delayLow(() -> mc.execute(() -> mc.gameMode.useItem(mc.player, mc.player.getUsedItemHand())));
    }

    private static void clickSlot(int delay, int syncId, int slot, ContainerInput input, Runnable action) {
        if (System.currentTimeMillis() - lastClick > (delay + random.nextLong(200))) {
            lastClick = System.currentTimeMillis();
            mc.gameMode.handleContainerInput(syncId, slot, 0, input, mc.player);
            action.run();
        }
    }

    public static void pickUpSlot(int delay, int syncId, int slot, Runnable action) {
        clickSlot(delay, syncId, slot, ContainerInput.PICKUP, action);
    }
    public static void quickMoveSlot(int delay, int syncId, int slot, Runnable action) {
        clickSlot(delay, syncId, slot, ContainerInput.QUICK_MOVE, action);
    }

    public static boolean isInMGM() {
        return currentLocation().contains("Moonglade Marsh");
    }
    public static boolean isInTC() { return currentLocation().contains("Torrhus Canyon"); }
    public static boolean isInGarden() {
        return currentLocation().contains("Garden");
    }
    public static boolean isInPrivateIsland() {
        return currentLocation().contains("Private Island");
    }
    public static void warpToIsland() {
        mc.getConnection().sendCommand("warp island");
    }
    public static void warpToLoch() {
        mc.getConnection().sendCommand("warp loch");
    }
}