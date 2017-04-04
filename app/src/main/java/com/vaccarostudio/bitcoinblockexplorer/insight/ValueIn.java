package com.vaccarostudio.bitcoinblockexplorer.insight;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;


public class ValueIn {



    /*
{
txid: "9f766a1a8474429a94d82980377760e9254cb365d6e96e75c1df44562c5333b6",
vout: 0,
sequence: 4294967294,
n: 0,
scriptSig: {
hex: "47304402206923bc01f8964fbb349c987b30fd10b238975414a0af0da9b3ed7b97e003a64002201fe6091ea5795bfce34422803315393aba965e202eb0d3ece4d8665b707034dd012102a055a8c59281d5025b996e0a8e322a08eb9f9e3032a34b52d4c3d12c5055d4f6",
asm: "304402206923bc01f8964fbb349c987b30fd10b238975414a0af0da9b3ed7b97e003a64002201fe6091ea5795bfce34422803315393aba965e202eb0d3ece4d8665b707034dd[ALL] 02a055a8c59281d5025b996e0a8e322a08eb9f9e3032a34b52d4c3d12c5055d4f6"
},
addr: "17uQTsAuoP2TxCVwig2gs5ofB68U7DnNcg",
valueSat: 3501910,
value: 0.0350191,
doubleSpentTxID: null
},
{
coinbase: "03eef306132f5669614254432f42696720426c6f636b732f36fabe6d6db1324bd07e7c81f76e33dffecff257785be976d2ff78ed8bcf0799056be0a4be0100000000000000092f4542312f4144362f12b94a531ad29391797a359df0144833500000",
sequence: 4294967295,
n: 0
}

*/

    public String txid;
    public Long vout;
    public ScriptSig scriptSig;
    public String addr;
    public Long valueSat;
    public Double value;
    public Boolean doubleSpentTxID;

    public String coinbase;
    public Long sequence;
    public Long n;


    public static ValueIn getJson(JSONObject json) {
        ValueIn v = new ValueIn();
        try {
            v.coinbase = json.getString("coinbase");
        } catch (Exception e) {
        }
        try {
            v.n = json.getLong("n");
        } catch (Exception e) {
        }
        try {
            v.sequence = json.getLong("sequence");
        } catch (Exception e) {
        }
        try {
            v.txid = json.getString("txid");
        } catch (Exception e) {
        }
        try {
            v.vout = json.getLong("vout");
        } catch (Exception e) {
        }
        try {
            v.addr = json.getString("addr");
        } catch (Exception e) {
        }
        try {
            v.valueSat = json.getLong("valueSat");
        } catch (Exception e) {
        }
        try {
            v.value = json.getDouble("value");
        } catch (Exception e) {
        }
        try {
            v.doubleSpentTxID = json.getBoolean("doubleSpentTxID");
        } catch (Exception e) {
        }
        try {
            v.scriptSig = ScriptSig.getJson(json.getJSONObject("scriptSig"));
        } catch (Exception e) {
        }
        return v;
    }

    public static String jsontoString(JSONObject json) throws JSONException {
        String txt="";
        for(Iterator<String> iter = json.keys();iter.hasNext();) {
            String key = iter.next();
            Object obj = json.get(key);
            txt+=key+": ";
            if(obj instanceof Boolean){
                txt+= String.valueOf( obj );
            } else if(obj instanceof Integer){
                txt+= String.valueOf( obj );
            } else if(obj instanceof Long){
                txt+= String.valueOf( obj );
            } else if(obj instanceof String){
                txt+= obj;
            } else if(obj instanceof List){
                txt+= obj.toString();
            }
            txt+="\n";
        }
        return txt;
    }

    @Override
    public String toString(){
        String txt="";
        if (this.txid!=null){
            txt+="Txid: "+txid+"\n";
        }
        if (this.vout!=null){
            txt+="vout: "+vout+"\n";
        }
        if (this.addr!=null){
            txt+="addr: "+addr+"\n";
        }
        if (this.valueSat!=null){
            txt+="valueSat: "+valueSat+"\n";
        }
        if (this.value!=null){
            txt+="value: "+value+"\n";
        }
        if (this.doubleSpentTxID!=null){
            txt+="doubleSpentTxID: "+doubleSpentTxID+"\n";
        }
        if (this.scriptSig!=null){
            txt+="scriptSig: \n";
            txt+=scriptSig.toString();
        }
        return txt;
    }
}
