package neionew.features;

import neionew.Config;
import neionew.LocationChecker;

import static neionew.Neionew.random;

public class AutoLobby {

    static long lastTime = 0L;

    public static void onTick() {
        if (!LocationChecker.isOnSkyblock() || !Config.scannerMain() || GalateaTimer.running || System.currentTimeMillis() - lastTime < (1000 + random.nextInt(100))) return;
        if (LocationChecker.isInMGM()) {
            if (ESP.getLeaving() && Config.warpIsland()) LocationChecker.warpToIsland();
        } else if (Config.warpGalatea()) LocationChecker.warpToLoch();
        lastTime = System.currentTimeMillis();
    }
}
