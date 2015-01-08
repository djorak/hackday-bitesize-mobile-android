package uk.co.bbc.android.bitesize;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
    //public static final String BITESIZE_URL = "http://www.bbc.co.uk/education";
    public static final String BITESIZE_URL = "http://pal.sandbox.dev.bbc.co.uk/education";

    private WebView bitesizeWebView;
    private ProgressBar mProgress;
    private int mProgressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mProgress = (ProgressBar) findViewById(R.id.progress_bar);
        mProgress.setVisibility(View.GONE);

        bitesizeWebView = initBitesizeWebView();
        bitesizeWebView.loadUrl(BITESIZE_URL);
    }

    private WebView initBitesizeWebView() {
        WebView myWebView;
        myWebView = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");

        myWebView.setWebViewClient(new BitesizeWebViewClient() {
            @Override
            public void launchExternalBrowser(String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Boolean isHomepage = url.equals(BITESIZE_URL);
                getActionBar().setDisplayHomeAsUpEnabled(!isHomepage);

                view.setVisibility(View.INVISIBLE);

                mProgress.setVisibility(View.VISIBLE);
                mProgress.setProgress(0);

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgress.setProgress(100);
                mProgress.setVisibility(View.GONE);

                view.setVisibility(View.VISIBLE);

                super.onPageFinished(view, url);
            }
        });

        return myWebView;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && bitesizeWebView.canGoBack()) {
            bitesizeWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            case R.id.action_refresh:
                bitesizeWebView.reload();
        }

        return super.onOptionsItemSelected(item);
    }
}
