package io.github.mingzhiawa.neriplayerlyrics;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Publishes the Universal Player Provider binding accepted by ColorOS Live Lyrics Bridge.
 *
 * <p>NeriPlayer already writes a standards-compliant {@code lyricInfo} payload into its own
 * {@code MediaMetadata}. What it cannot do is announce itself as a Bridge-managed player: without
 * that announcement Bridge keeps the vendor allow-lists closed for it. This binding broadcast is
 * the only documented handshake, and it makes Bridge treat the host like its built-in players
 * (RUS whitelist, media blacklist, AOD media panel, lyric entrance 52).
 */
final class BridgeBindings {
    static final String ACTION_PLAYER_BINDINGS_CHANGED =
            "io.github.andrealtb.universallyrics.action.PLAYER_BINDINGS_CHANGED";
    static final String EXTRA_BOUND_PACKAGES = "extra_bound_packages";
    static final String EXTRA_PROVIDER_IDENTITY = "extra_provider_identity";
    static final String PROVIDER_IDENTITY = "universal-player-provider";

    private BridgeBindings() {
    }

    static Intent bindingsIntent() {
        Intent intent = new Intent(ACTION_PLAYER_BINDINGS_CHANGED);
        intent.setPackage(Host.BRIDGE_PACKAGE);
        intent.putStringArrayListExtra(
                EXTRA_BOUND_PACKAGES,
                new ArrayList<>(Collections.singletonList(Host.PACKAGE)));
        intent.putExtra(EXTRA_PROVIDER_IDENTITY, PROVIDER_IDENTITY);
        return intent;
    }

    static boolean publish(Context context) {
        if (context == null || !Host.isBridgeInstalled(context)) {
            return false;
        }
        try {
            context.sendBroadcast(bindingsIntent());
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
