package io.github.mingzhiawa.neriplayerlyrics;

import android.content.Context;
import android.content.pm.PackageManager;

/** Stable constants for the host player and the ColorOS live-lyrics Bridge. */
final class Host {
    /** Host player package: NeriPlayer (音理音理). */
    static final String PACKAGE = "moe.ouom.neriplayer";
    /** Only the main process owns the playback MediaSession. */
    static final String MAIN_PROCESS = PACKAGE;

    /** Optional companion module: ColorOS Live Lyrics Bridge (SystemUI side). */
    static final String BRIDGE_PACKAGE = "io.github.andrealtb.lockscreenlyrics";

    private Host() {
    }

    static Context applicationContext() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object application = activityThread.getDeclaredMethod("currentApplication").invoke(null);
            if (application instanceof Context) {
                return (Context) application;
            }
        } catch (Throwable ignored) {
            // Early process bootstrap may not expose the application yet.
        }
        return null;
    }

    static boolean isBridgeInstalled(Context context) {
        if (context == null) {
            return false;
        }
        try {
            context.getPackageManager().getPackageInfo(BRIDGE_PACKAGE, 0);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
            return false;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
