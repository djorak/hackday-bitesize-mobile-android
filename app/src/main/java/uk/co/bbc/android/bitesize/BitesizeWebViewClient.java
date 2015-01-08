package uk.co.bbc.android.bitesize;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by mazej01 on 07/01/2015.
 */
public abstract class BitesizeWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (
            Uri.parse(url).getHost().endsWith("bbc.co.uk")
            && Uri.parse(url).getPathSegments().get(0).equals("education")
        ) {
            view.loadUrl(url);
            return false;
        }

        launchExternalBrowser(url);
        return true;
    }

    public abstract void launchExternalBrowser(String url);
}
