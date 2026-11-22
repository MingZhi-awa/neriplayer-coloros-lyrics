package io.github.mingzhiawa.neriplayerlyrics;

import android.content.Context;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.SystemClock;
import android.util.Log;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface;

/**
 * ColorOS live-lyrics companion for NeriPlayer (音理音理).
 *
 * <p>NeriPlayer already publishes a complete {@code lyricInfo} payload, so this module does not
 * invent a lyric transport. It only fills the two gaps the host cannot fill by itself:
 *
 * <ul>
 *   <li>announce the host as a Universal Player Provider so ColorOS Live Lyrics Bridge opens its
 *       vendor allow-lists (RUS whitelist, lyric entrance 52, AOD panel) for it;</li>
 *   <li>advertise the public {@code TOGGLE_TRANSLATION} action on the host {@link PlaybackState},
 *       which is what makes Bridge render its word-level renderer's translation button.</li>
 * </ul>
 *
 * <p>Nothing here touches metadata, artwork, playback commands or the lyric payload itself.
 */
public final class NeriPlayerLyricsModule extends XposedModule {
    private static final String TAG = "NeriPlayerLyrics";
    private static final long BRIDGE_PROBE_INTERVAL_MS = 60_000L;

    private volatile String processName = "";
    private volatile Boolean bridgeAvailable;
    private volatile long lastBridgeProbeMs;

    @Override
    public void onModuleLoaded(XposedModuleInterface.ModuleLoadedParam param) {
        processName = param.getProcessName();
    }

    @Override
    public void onPackageLoaded(XposedModuleInterface.PackageLoadedParam param) {
        if (!Host.PACKAGE.equals(param.getPackageName())) {
            return;
        }
        if (!Host.MAIN_PROCESS.equals(processName)) {
            info("skip: auxiliary process " + processName);
            return;
        }
        info("player process ready, process=" + processName);
        installPlaybackStateHook();
        publishBindingsAsync();
    }

    private void installPlaybackStateHook() {
        try {
            Method setPlaybackState = MediaSession.class.getMethod(
                    "setPlaybackState", PlaybackState.class);
            hook(setPlaybackState)
                    .setId("npl:playback-state")
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        Object argument = chain.getArg(0);
                        if (argument instanceof PlaybackState && isBridgeAvailable()) {
                            PlaybackState injected =
                                    PlaybackStateTranslationAction.withTranslationAction(
                                            (PlaybackState) argument);
                            if (injected != null) {
                                info("playback state: translation action advertised");
                                return chain.proceed(new Object[]{injected});
                            }
                        }
                        return chain.proceed();
                    });
            info("hook installed: MediaSession#setPlaybackState");
        } catch (Throwable error) {
            error("failed to hook MediaSession#setPlaybackState", error);
        }
    }

    private boolean isBridgeAvailable() {
        Boolean cached = bridgeAvailable;
        long now = SystemClock.elapsedRealtime();
        if (cached != null && now - lastBridgeProbeMs < BRIDGE_PROBE_INTERVAL_MS) {
            return cached;
        }
        Context context = Host.applicationContext();
        boolean available = Host.isBridgeInstalled(context);
        bridgeAvailable = available;
        lastBridgeProbeMs = now;
        if (cached == null || cached != available) {
            info(available
                    ? "bridge present: " + Host.BRIDGE_PACKAGE
                    : "bridge missing: " + Host.BRIDGE_PACKAGE);
        }
        return available;
    }

    private void publishBindingsAsync() {
        Thread worker = new Thread(() -> {
            long[] delays = {2_000L, 6_000L, 15_000L};
            for (long delay : delays) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    return;
                }
                Context context = Host.applicationContext();
                if (BridgeBindings.publish(context)) {
                    info("universal player binding published");
                    return;
                }
            }
            info("universal player binding skipped: bridge not installed");
        }, "NeriPlayerLyricsBindings");
        worker.setDaemon(true);
        worker.start();
    }

    private void info(String message) {
        log(Log.INFO, TAG, "[NPL] " + message);
    }

    private void error(String message, Throwable throwable) {
        log(Log.ERROR, TAG, "[NPL] " + message, throwable);
    }
}
