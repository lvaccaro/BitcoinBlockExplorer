package com.vaccarostudio.bitcoinblockexplorer.insight;

import org.json.JSONException;
import org.json.JSONObject;


public class ScriptSig {
    /*
scriptSig: {
hex: "483045022100b5602be8297af4ed118102bfa8a410d1467afb27aec26c467a6a3fb2c4867f600220335229a8e0efbc616e99eacc64f83826fd75d520b776e8123be085226c95df51012102e5fb67442c0001c58a1a7278e8ab6c848e2d471f416796ea61f33a9f485073dd",
asm: "3045022100b5602be8297af4ed118102bfa8a410d1467afb27aec26c467a6a3fb2c4867f600220335229a8e0efbc616e99eacc64f83826fd75d520b776e8123be085226c95df51[ALL] 02e5fb67442c0001c58a1a7278e8ab6c848e2d471f416796ea61f33a9f485073dd"
},*/

    public String hex;
    public String asm;

    public static ScriptSig getJson(JSONObject json) throws JSONException {
        ScriptSig spk = new ScriptSig();
        try {
            spk.hex = json.getString("hex");
        } catch (Exception e) {
        }
        try {
            spk.asm = json.getString("asm");
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
        return txt;
    }
}
