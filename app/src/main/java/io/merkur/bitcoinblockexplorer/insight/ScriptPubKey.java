package io.merkur.bitcoinblockexplorer.insight;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ScriptPubKey {
    /*
scriptPubKey: {
hex: "a91453aa80d7bb1f9befc5627a680afcf4088b9a65c687",
asm: "OP_HASH160 53aa80d7bb1f9befc5627a680afcf4088b9a65c6 OP_EQUAL",
addresses: [
"39KQGEzcSZUGvWXz3HFVatNcPaWSr3ty4w"
],
type: "scripthash"
},*/

    public String hex;
    public String asm;
    public List<String> addresses;
    public String type;

    public static ScriptPubKey getJson(JSONObject json) throws JSONException {
        ScriptPubKey spk = new ScriptPubKey();
        try {
            spk.hex = json.getString("hex");
        } catch (Exception e) {
        }
        try {
            spk.asm = json.getString("asm");
        } catch (Exception e) {
        }
        try {
            spk.type = json.getString("type");
        } catch (Exception e) {
        }
        try {
            spk.addresses = new ArrayList<>();
            for (int i = 0; i < json.getJSONArray("addresses").length(); i++) {
                spk.addresses.add(json.getJSONArray("addresses").get(i).toString());
            }
        } catch (Exception e) {
        }
        return spk;
    }


    @Override
    public String toString() {
        String txt = "";
        if (this.hex != null) {
            txt += "hex: " + hex + "\n";
        }
        if (this.asm != null) {
            txt += "asm: " + asm + "\n";
        }
        if (this.type != null) {
            txt += "type: " + type + "\n";
        }
        if (this.addresses != null) {
            txt += "addresses: \n";
            for (String address : this.addresses){
                txt += address+"\n";
            }
        }
        return txt;
    }
}
