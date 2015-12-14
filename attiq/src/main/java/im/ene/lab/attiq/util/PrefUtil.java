package im.ene.lab.attiq.util;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.data.request.BaseRequest;

import java.io.IOException;

/**
 * Created by eneim on 12/13/15.
 */
public class PrefUtil {

  private static final String PREF_CURRENT_TOKEN = "attiq_preference_current_token";

  private static final AuthInterceptor sInterceptor = new AuthInterceptor();

  public static Interceptor authInterceptor() {
    return sInterceptor;
  }

  public static void setCurrentToken(String token) {
    Attiq.pref().edit().putString(PREF_CURRENT_TOKEN, token).apply();
  }

  public static String getCurrentToken() {
    return Attiq.pref().getString(PREF_CURRENT_TOKEN, null);
  }

  /**
   * ref: http://stackoverflow.com/a/27868976/1553254
   */
  static class AuthInterceptor implements Interceptor {

    @Override public Response intercept(Chain chain) throws IOException {
      Request.Builder requestBuilder = chain.request().newBuilder();
      if (!UIUtil.isEmpty(PrefUtil.getCurrentToken())) {
        requestBuilder.addHeader(BaseRequest.Headers.AUTHORIZATION,
            BaseRequest.Headers.authorization(PrefUtil.getCurrentToken()));
      }

      return chain.proceed(requestBuilder.build());
    }
  }
}