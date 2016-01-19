package im.ene.lab.attiq.util;

import android.support.annotation.NonNull;

import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.data.api.Header;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by eneim on 12/13/15.
 */
public class PrefUtil {

  private static final String PREF_CURRENT_TOKEN = "attiq_preference_current_token";

  private static final String PREF_FIRST_START_FLAG = "attiq_preference_flag_first_start";

  private static final Ok3AuthInterceptor sOk3Auth = new Ok3AuthInterceptor();

  public static Interceptor ok3Auth() {
    return sOk3Auth;
  }

  public static void setCurrentToken(String token) {
    if (token != null) {
      Attiq.pref().edit().putString(PREF_CURRENT_TOKEN, token).apply();
    } else {
      Attiq.pref().edit().remove(PREF_CURRENT_TOKEN).apply();
    }
  }

  @NonNull
  public static String getCurrentToken() {
    return Attiq.pref().getString(PREF_CURRENT_TOKEN, "");
  }

  public static boolean isFirstStart() {
    return Attiq.pref().getBoolean(PREF_FIRST_START_FLAG, true);
  }

  public static void setFirstStart(boolean isFirstStart) {
    Attiq.pref().edit().putBoolean(PREF_FIRST_START_FLAG, isFirstStart).apply();
  }

  /**
   * ref: http://stackoverflow.com/a/27868976/1553254
   */
  static class Ok3AuthInterceptor implements Interceptor {

    @Override public Response intercept(Chain chain) throws IOException {
      Request.Builder requestBuilder = chain.request().newBuilder();
      if (!UIUtil.isEmpty(PrefUtil.getCurrentToken())) {
        requestBuilder.addHeader(Header.Request.AUTHORIZATION,
            Header.Request.authorization(PrefUtil.getCurrentToken()));
      }

      return chain.proceed(requestBuilder.build());
    }
  }

}
