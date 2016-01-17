package im.ene.lab.attiq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;

/**
 * Created by eneim on 12/13/15.
 */
public class AuthActivity extends AppCompatActivity {

  private WebView mWebView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mWebView = new WebView(this);
    setContentView(mWebView);
    mWebView.setWebViewClient(mWebClient);
    mWebView.loadUrl(ApiClient.authCallback());
  }

  private static final String TAG = "WebViewActivity";

  private WebViewClient mWebClient = new WebViewClient() {

    @Override public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      if (url.startsWith(getString(R.string.api_callback))) {
        String cookies = CookieManager.getInstance().getCookie(url);
        Log.e(TAG, "onPageFinished: " + cookies);
      }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      Log.d(TAG, "auth:callback:" + url);
      // catch the application callback url here and return to LoginActivity
      // attiq://lab.ene.im/qiita/oauth?code=c73fe3c7d4e570086221b17f1e64c95b2dec8616&state
      // =299792459
      if (url.startsWith(getString(R.string.api_callback))) {
        Intent result = new Intent(AuthActivity.this, MainActivity.class);
        // setup return result
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.EXTRA_AUTH_CALLBACK, url);
        result.putExtras(bundle);

        setResult(RESULT_OK, result);
        finish();
        return true;
      } else {
        return super.shouldOverrideUrlLoading(view, url);
      }
    }
  };

}