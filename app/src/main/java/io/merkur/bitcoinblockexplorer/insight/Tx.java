package io.merkur.bitcoinblockexplorer.insight;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Tx {

    public String txid="";
    public Long version;
    public Long locktime;
    public String blockhash;
    public Long blockheight;
    public Long confirmations;
    public Long time;
    public Long blocktime;
    public Double valueOut;
    public Long size;
    public Double fees;
    public long valueIn;

    public List<ValueIn> vin;
    public List<ValueOut> vout;


    /* EXAMPLE:
    txid: "e5e630a4d2e195474cc600f1a604833169b729e80c1bbef22a26a6d02bbb715b",
    version: 1,
    locktime: 0,
    vin: [],
    vout: [],
    blockhash: "000000000000000001627406838bb8d70530f5b391dd28a4cac90e833439c47d",
    blockheight: 455662,
    confirmations: 553,
    time: 1488592120,
    blocktime: 1488592120,
    valueOut: 0.07521384,
    size: 4189,
    valueIn: 0.08204904,
    fees: 0.0068352
    */

    public static Tx getJson(JSONObject json) throws JSONException {
        Tx tx = new Tx();
        try {
            tx.txid = json.getString("txid");
        } catch (Exception e) {
        }
        try {
            tx.version = json.getLong("version");
        } catch (Exception e) {
        }
        try {
            tx.locktime = json.getLong("locktime");
        } catch (Exception e) {
        }
        try {
            tx.blockhash = json.getString("blockhash");
        } catch (Exception e) {
        }
        try {
            tx.blockheight = json.getLong("blockheight");
        } catch (Exception e) {
        }
        try {
            tx.confirmations = json.getLong("confirmations");
        } catch (Exception e) {
        }
        try {
            tx.time = json.getLong("time");
        } catch (Exception e) {
        }
        try {
            tx.blocktime = json.getLong("blocktime");
        } catch (Exception e) {
        }
        try {
            tx.valueOut = json.getDouble("valueOut");
        } catch (Exception e) {
        }
        try {
            tx.size = json.getLong("size");
        } catch (Exception e) {
        }
        try {
            tx.valueIn = json.getLong("valueIn");
        } catch (Exception e) {
        }
        try {
            tx.fees = json.getDouble("fees");
        } catch (Exception e) {
        }
        try {
            tx.vin = new ArrayList<>();
            for (int i = 0; i < json.getJSONArray("vin").length(); i++) {
                tx.vin.add(ValueIn.getJson(json.getJSONArray("vin").getJSONObject(i)));
            }
        } catch (Exception e) {
        }
        try {
            tx.vout = new ArrayList<>();
            for (int i = 0; i < json.getJSONArray("vout").length(); i++) {
                tx.vout.add(ValueOut.getJson(json.getJSONArray("vout").getJSONObject(i)));
            }
        } catch (Exception e) {
        }
        return tx;
    }
}
