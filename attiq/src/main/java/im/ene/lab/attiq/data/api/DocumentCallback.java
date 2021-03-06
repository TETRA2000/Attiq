/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.lab.attiq.data.api;

import java.io.IOException;
import java.io.InputStream;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by eneim on 1/14/16.
 *
 * Used after loading an website by OkHttp, then parsing it by Jsoup
 */
public abstract class DocumentCallback implements okhttp3.Callback {

  private final String baseUrl;

  public DocumentCallback(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @Override public void onFailure(Call call, IOException e) {
    onDocument(null);
  }

  @Override public void onResponse(Call call, Response response) throws IOException {
    ResponseBody body = response.body();
    InputStream stream = body == null ? null : body.byteStream();
    if (stream != null) {
      Document document = Jsoup.parse(stream, "utf-8", baseUrl);
      onDocument(document);
    }
  }

  public abstract void onDocument(Document response);
}
