package com.vaccarostudio.bitcoinblockexplorer.insight;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class Block {

    public String hash="";
    public Long size;
    public Long height;
    public Long version;
    public String merkleroot;
    public List<String> transactions;
    public Long time;
    public Long nonce;
    public String bits;
    public Double difficulty;
    public String chainwork;
    public Long confirmations;
    public String previousblockhash;
    public String nextblockhash;
    public Double reward;
    public Boolean isMainChain;


    /* EXAMPLE:
hash: "000000000000000001627406838bb8d70530f5b391dd28a4cac90e833439c47d",
size: 999150,
height: 455662,
version: 536870912,
merkleroot: "7964431827b1a7aaf3fe86e06c69a9eb163ef421941256bd8c36cbdb2137e848",
tx: [
    ],
time: 1488592120,
nonce: 2645399509,
bits: "180262df",
difficulty: 460769358090.71423,
chainwork: "000000000000000000000000000000000000000000432ec9c169b09ae7839587",
confirmations: 558,
previousblockhash: "000000000000000001511ac791639cd660efa37afc685e546739b54a00698204",
nextblockhash: "00000000000000000248c678dd8170bd85d8e66e59fe7199f6a1512dea43c47a",
reward: 12.5,
isMainChain: true,
poolInfo: { }
    */

    public static Block getJson(JSONObject json) {
        Block block = new Block();
        try {
            block.hash = json.getString("hash");
        }catch (Exception e){}
        try {
        block.version=json.getLong("version");
        }catch (Exception e){}
        try {
        block.size=json.getLong("size");
        }catch (Exception e){}
        try {
        block.height=json.getLong("height");
        }catch (Exception e){}
        try {
        block.merkleroot=json.getString("merkleroot");
        }catch (Exception e){}
        try {
        block.time=json.getLong("time");
        }catch (Exception e){}
        try {
        block.nonce=json.getLong("nonce");
        }catch (Exception e){}
        try {
        block.bits=json.getString("bits");
        }catch (Exception e){}
        try {
        block.difficulty=json.getDouble("difficulty");
        }catch (Exception e){}
        try {
        block.size=json.getLong("size");
        }catch (Exception e){}
        try {
        block.chainwork=json.getString("chainwork");
        }catch (Exception e){}
        try {
        block.confirmations=json.getLong("confirmations");
        }catch (Exception e){}
        try {
        block.previousblockhash=json.getString("previousblockhash");
        }catch (Exception e){}
        try {
        block.nextblockhash=json.getString("nextblockhash");
        }catch (Exception e){}
        try {
        block.reward=json.getDouble("reward");
        }catch (Exception e){}
        try {
        block.isMainChain=json.getBoolean("isMainChain");
        }catch (Exception e){}
        try {
        block.transactions=new ArrayList<>();
        for (int i=0; i<json.getJSONArray("tx").length();i++){
            block.transactions.add(json.getJSONArray("tx").get(i).toString());
        }
        }catch (Exception e){}
        return block;
    }

    public LinkedHashMap<String, String> toDataset(){
        LinkedHashMap<String, String> mDataset = new LinkedHashMap<>();
        Date date = new Date(this.time);
        mDataset.put("Hash",this.hash);
        mDataset.put("Height",String.valueOf(this.height));
        mDataset.put("Prev Block",this.previousblockhash);
        mDataset.put("Next Block",this.nextblockhash);
        mDataset.put("MerkleRoot",this.merkleroot);
        mDataset.put("DifficultyTarget",String.valueOf(this.difficulty));
        mDataset.put("Nonce",String.valueOf(this.nonce));
        mDataset.put("Date",date.toLocaleString());
        mDataset.put("Version",String.valueOf(this.version));
        mDataset.put("Chainwork",this.chainwork);
        mDataset.put("Size",String.valueOf(this.size));
        mDataset.put("Reward",String.valueOf(this.reward));
        mDataset.put("Confirmations",String.valueOf(this.confirmations));
        mDataset.put("isMainChain",String.valueOf(this.isMainChain));
        mDataset.put("Transactions",String.valueOf(this.transactions.size()));
        int i=0;
        for (String tx : this.transactions){
            mDataset.put(String.valueOf(i)+" Transaction",tx);
            i++;
        }
        return mDataset;
    }

    public static String merkle(List<String> hashList){
        if(hashList.size()==1){
            return hashList.get(0);
        }
        List<String> newHashList = new ArrayList<>();
        // Process pairs. For odd length, the last is skipped
        for (int i=0;i<hashList.size()-1;i+=2){
            newHashList.add( hash2(hashList.get(i), hashList.get(i+1)) );
        }
        if (hashList.size() % 2 == 1) {
            // odd, hash last item twice
            newHashList.add(hash2(hashList.get(hashList.size() - 1), hashList.get(hashList.size() - 1)));
        }
        return merkle(newHashList);
    }


    public static String hash2(String a, String b){
        // Reverse inputs before and after hashing due to big-endian / little-endian nonsense

        byte[] a1 = Utils.hexStringToByteArray(a);
        byte[] b1 = Utils.hexStringToByteArray(b);
        Utils.reverse(a1);
        Utils.reverse(b1);

        byte[] ab = new byte[a1.length + b1.length];
        System.arraycopy(a1, 0, ab, 0, a1.length);
        System.arraycopy(b1, 0, ab, a1.length, b1.length);

        byte[] hash = Sha256Hash.hash(Sha256Hash.hash(ab));
        Utils.reverse(hash);
        return Utils.byteArrayToHex(hash);
    }

    public boolean equals (StoredBlock storedBlock){
        if (storedBlock == null )
            return false;

        return storedBlock.getHeight() == this.height &&
                storedBlock.getHeader().getHashAsString().equals(this.hash) &&
                storedBlock.getHeader().getMerkleRoot().toString().equals(this.merkleroot) &&
                storedBlock.getHeader().getNonce() == this.nonce &&
                storedBlock.getHeader().getTimeSeconds() == this.time &&
                //storedBlock.getHeader().getTransactions().size() == this.tx.size() &&
                storedBlock.getHeader().getVersion() == this.version;

    }
}
