package com.j4.trader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class MainActivity extends Activity {
    WebView w;
    SharedPreferences sp;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        // FLAG_SECURE: no screenshots / screen recording (token is on screen), KEEP_SCREEN_ON: app stays awake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sp = getSharedPreferences("j4", MODE_PRIVATE);
        w = new WebView(this);
        setContentView(w);
        WebSettings s = w.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(false);
        w.setWebViewClient(new WebViewClient() {
            @Override public void onReceivedError(WebView v, WebResourceRequest r, WebResourceError e) {
                if (r.isForMainFrame()) ask("Server not reachable. Check address:");
            }
        });
        String u = sp.getString("url", "");
        if (u.isEmpty()) ask("Server address (e.g. http://100.x.x.x:8080):"); else w.loadUrl(u);
    }

    void ask(String msg) {
        final EditText e = new EditText(this);
        e.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        e.setText(sp.getString("url", "http://"));
        new AlertDialog.Builder(this).setTitle("J4 Trader").setMessage(msg).setView(e).setCancelable(false)
            .setPositiveButton("Open", (d, i) -> {
                String u = e.getText().toString().trim();
                sp.edit().putString("url", u).apply();
                w.loadUrl(u);
            }).show();
    }

    @Override public void onBackPressed() { if (w.canGoBack()) w.goBack(); else super.onBackPressed(); }
}
