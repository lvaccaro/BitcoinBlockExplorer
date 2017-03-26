package io.merkur.bitcoinblockexplorer.ipapi;

import org.json.JSONObject;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import io.merkur.bitcoinblockexplorer.insight.Block;
import io.merkur.bitcoinblockexplorer.insight.Insight;

/**
 * Created by luca on 26/03/2017.
 */

public class Geocode {
    public static String url = "http://ip-api.com/json";

    public static Hostplace get(String ip) throws Exception {
        HttpGet httppost = new HttpGet(Geocode.url + "/" + ip);
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(httppost);

        // StatusLine stat = response.getStatusLine();
        int status = response.getStatusLine().getStatusCode();

        if (status == 200) {
            HttpEntity entity = response.getEntity();
            String data = EntityUtils.toString(entity);

            JSONObject json = new JSONObject(data);
            if (json.getString("status").equals("fail"))
                throw new Exception();

            Hostplace hostplace = Hostplace.getJson(json);
            return hostplace;
        }
        throw new Exception();
    }
}
