package io.github.mingzhiawa.neriplayerlyrics;

import android.graphics.drawable.Icon;
import android.media.session.PlaybackState;

import java.util.List;

/**
 * Adds the public lyric-translation action to the host {@link PlaybackState}.
 *
 * <p>Bridge owns the button: it binds this action to the favorite slot and toggles the lyric
 * translation lane on the SystemUI side, so the player never has to answer the click. The player
 * only has to advertise the action, which is exactly what the documented
 * {@code PlaybackStateTranslationToggle} contract asks for.
 */
final class PlaybackStateTranslationAction {
    static final String ACTION_TRANSLATION =
            "io.github.andrealtb.lockscreenlyrics.action.TOGGLE_TRANSLATION";
    private static final String ACTION_LABEL = "\u7ffb\u8bd1";
    /** System icon used only as a placeholder; Bridge replaces the drawable it renders. */
    private static final String ICON_PACKAGE = "android";
    private static final int ICON_RESOURCE = android.R.drawable.ic_menu_sort_alphabetically;

    private PlaybackStateTranslationAction() {
    }

    static boolean hasTranslationAction(PlaybackState state) {
        if (state == null) {
            return false;
        }
        List<PlaybackState.CustomAction> actions = state.getCustomActions();
        if (actions == null) {
            return false;
        }
        for (PlaybackState.CustomAction action : actions) {
            if (action != null && ACTION_TRANSLATION.equals(action.getAction())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a copy of {@code state} carrying the translation action, or {@code null} when the
     * caller should publish {@code state} unchanged.
     */
    static PlaybackState withTranslationAction(PlaybackState state) {
        if (state == null || hasTranslationAction(state)) {
            return null;
        }
        try {
            PlaybackState.CustomAction action = new PlaybackState.CustomAction.Builder(
                    ACTION_TRANSLATION,
                    ACTION_LABEL,
                    Icon.createWithResource(ICON_PACKAGE, ICON_RESOURCE))
                    .build();
            PlaybackState.Builder builder = new PlaybackState.Builder(state);
            builder.addCustomAction(action);
            return builder.build();
        } catch (Throwable ignored) {
            // Contract is best-effort: a failed copy must never disturb host playback state.
            return null;
        }
    }
}
