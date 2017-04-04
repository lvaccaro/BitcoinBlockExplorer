package com.vaccarostudio.bitcoinblockexplorer.insight;

import org.json.JSONObject;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;


public class Insight {

    //public static String url = "https://test-insight.bitpay.com/api";
    public static String url = "https://insight.bitpay.com/api";

    public static Block getBlock(String blockhash) throws Exception {
        HttpGet httppost = new HttpGet(Insight.url + "/block/" + blockhash);
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(httppost);

        // StatusLine stat = response.getStatusLine();
        int status = response.getStatusLine().getStatusCode();

        if (status == 200) {
            HttpEntity entity = response.getEntity();
            String data = EntityUtils.toString(entity);

            JSONObject json = new JSONObject(data);
            Block block = Block.getJson(json);
            return block;
        }
        throw new Exception();
    }

    public static String getBlockHash(String height) throws Exception {
        HttpGet httppost = new HttpGet(Insight.url + "/block-index/" + height);
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(httppost);

        // StatusLine stat = response.getStatusLine();
        int status = response.getStatusLine().getStatusCode();

        if (status == 200) {
            HttpEntity entity = response.getEntity();
            String data = EntityUtils.toString(entity);

            JSONObject json = new JSONObject(data);
            String hash = json.getString("blockHash");
            return hash;
        }
        throw new Exception();
    }


    public static Tx getTx(String hash) throws Exception {
        HttpGet httppost = new HttpGet(Insight.url + "/tx/" + hash);
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(httppost);

        // StatusLine stat = response.getStatusLine();
        int status = response.getStatusLine().getStatusCode();

        if (status == 200) {
            HttpEntity entity = response.getEntity();
            String data = EntityUtils.toString(entity);

            JSONObject json = new JSONObject(data);
            Tx tx = Tx.getJson(json);
            return tx;
        }
        throw new Exception();
    }


}
