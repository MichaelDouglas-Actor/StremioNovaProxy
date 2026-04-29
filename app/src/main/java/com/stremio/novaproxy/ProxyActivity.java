package com.stremio.novaproxy;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ProxyActivity extends Activity {

    private static final String TAG = "StremioNovaProxy";

    // Nova Video Player package and activity
    private static final String NOVA_PACKAGE = "org.courville.nova";
    private static final String NOVA_ACTIVITY = "org.courville.nova.player.PlayerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent incoming = getIntent();
        if (incoming == null) {
            Log.e(TAG, "No intent received");
            finish();
            return;
        }

        // Extract the URL - try data URI first, then common extras
        String url = null;

        Uri data = incoming.getData();
        if (data != null) {
            url = data.toString();
            Log.d(TAG, "Got URL from intent data URI (" + url.length() + " chars): " + url);
        }

        if (url == null) {
            url = incoming.getStringExtra("url");
            if (url == null) url = incoming.getStringExtra("videoUrl");
            if (url == null) url = incoming.getStringExtra("path");
        }

        if (url != null) {
            try {
                String decoded = url;

            // Handle double-encoding safely (max 2 passes)
                for (int i = 0; i < 2; i++) {
                    String temp = java.net.URLDecoder.decode(decoded, "UTF-8");
                    if (temp.equals(decoded)) break;
                    decoded = temp;
            }

        // Rebuild safely to normalize all special characters
                Uri uri = Uri.parse(decoded);
                String normalized = uri.buildUpon().build().toString();
        
                Log.d(TAG, "Normalized URL (" + normalized.length() + " chars): " + normalized);
        
                url = normalized;

        } catch (Exception e) {
            Log.w(TAG, "URL normalization failed, using original: " + e.getMessage());
        }
    }

        if (url == null || url.isEmpty()) {
            Log.e(TAG, "Could not extract URL from intent");
            Toast.makeText(this, "StremioNovaProxy: No URL found in intent", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Log.d(TAG, "Forwarding URL to Nova (" + url.length() + " chars)");

        // Build a new intent targeting Nova directly
        Intent novaIntent = new Intent(Intent.ACTION_VIEW);
        novaIntent.setComponent(new ComponentName(NOVA_PACKAGE, NOVA_ACTIVITY));

        // Pass as extra string - avoids Android URI length/parsing issues
        novaIntent.putExtra("url", url);
        novaIntent.putExtra("videoUrl", url);

        // Also attempt setData with the URI in case Nova needs it
        try {
            Uri uri = Uri.parse(url);
        
            novaIntent.setData(uri);
            novaIntent.setType(getMimeType(incoming));
        
            // Stronger compatibility for Nova
            novaIntent.putExtra(Intent.EXTRA_TEXT, url);
            novaIntent.putExtra("android.intent.extra.STREAM", uri);

        } catch (Exception e) {
            Log.w(TAG, "setData failed, relying on extras only: " + e.getMessage());
            novaIntent.setType(getMimeType(incoming));
        }
        // Forward any extras from the original intent
        Bundle extras = incoming.getExtras();
        if (extras != null) {
            novaIntent.putExtras(extras);
        }

        novaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(novaIntent);
            Log.d(TAG, "Successfully launched Nova");
        } catch (Exception e) {
            Log.e(TAG, "Failed to launch Nova: " + e.getMessage());
            Toast.makeText(this,
                "StremioNovaProxy: Could not launch Nova Video Player. Is it installed?",
                Toast.LENGTH_LONG).show();
        }

        finish();
    }

    private String getMimeType(Intent intent) {
        String type = intent.getType();
        return (type != null) ? type : "video/*";
    }
}
