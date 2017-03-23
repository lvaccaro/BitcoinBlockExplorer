package io.merkur.bitcoinblockexplorer.insight;

import org.json.JSONException;
import org.json.JSONObject;

public class ValueOut {



    /*
value: "0.03475493",
n: 0,
scriptPubKey: {
hex: "a91453aa80d7bb1f9befc5627a680afcf4088b9a65c687",
asm: "OP_HASH160 53aa80d7bb1f9befc5627a680afcf4088b9a65c6 OP_EQUAL",
addresses: [
"39KQGEzcSZUGvWXz3HFVatNcPaWSr3ty4w"
],
type: "scripthash"
},
spentTxId: "f4ab85573b29a85868dba28ddf74230adf20ef7e67da0b0043f02cdd03cda503",
spentIndex: 19,
spentHeight: 455709
*/

    public String value;
    public Long n;
    public String spentTxId;
    public Long spentIndex;
    public Long spentHeight;
    public ScriptPubKey scriptPubKey;


    public static ValueOut getJson(JSONObject json) throws JSONException {
        ValueOut v = new ValueOut();
        v.value=json.getString("value");
        v.n=json.getLong("n");
        v.spentTxId=json.getString("spentTxId");
        v.spentIndex=json.getLong("spentIndex");
        v.spentHeight=json.getLong("spentHeight");
        v.scriptPubKey = ScriptPubKey.getJson(json.getJSONObject("scriptPubKey"));
        return v;
    }

    @Override
    public String toString(){
        String txt="";
        if (this.value!=null){
            txt+="value: "+value+"\n";
        }
        if (this.n!=null){
            txt+="n: "+n+"\n";
        }
        if (this.spentTxId!=null){
            txt+="spentTxId: "+spentTxId+"\n";
        }
        if (this.spentIndex!=null){
            txt+="valueSat: "+spentIndex+"\n";
        }
        if (this.spentHeight!=null){
            txt+="spentHeight: "+spentHeight+"\n";
        }
        if (this.scriptPubKey!=null){
            txt+="scriptPubKey: \n";
            txt+=scriptPubKey.toString();
        }
        return txt;
    }
}
