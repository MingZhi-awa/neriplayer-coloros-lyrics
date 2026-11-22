package io.github.mingzhiawa.neriplayerlyrics;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/** Standalone diagnostics entry: shows what the module depends on and can re-publish the binding. */
public final class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView status = findViewById(R.id.status_lines);
        status.setText(statusText());

        Button bindButton = findViewById(R.id.bind_button);
        bindButton.setOnClickListener(view -> {
            if (BridgeBindings.publish(this)) {
                toast(R.string.action_bind_done);
            } else {
                toast(R.string.action_bind_missing);
            }
        });

        Button openHostButton = findViewById(R.id.open_host_button);
        openHostButton.setOnClickListener(view -> {
            Intent launch = getPackageManager().getLaunchIntentForPackage(Host.PACKAGE);
            if (launch == null) {
                toast(R.string.action_host_missing);
                return;
            }
            try {
                startActivity(launch);
            } catch (ActivityNotFoundException ignored) {
                toast(R.string.action_host_missing);
            }
        });
    }

    private String statusText() {
        boolean bridge = Host.isBridgeInstalled(this);
        boolean host = isHostInstalled();
        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.status_module)).append('\n');
        builder.append(getString(host ? R.string.status_host_ok : R.string.status_host_missing))
                .append('\n');
        builder.append(getString(bridge
                ? R.string.status_bridge_ok
                : R.string.status_bridge_missing));
        return builder.toString();
    }

    private boolean isHostInstalled() {
        try {
            getPackageManager().getPackageInfo(Host.PACKAGE, 0);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private void toast(int stringRes) {
        Toast.makeText(this, stringRes, Toast.LENGTH_SHORT).show();
    }
}
