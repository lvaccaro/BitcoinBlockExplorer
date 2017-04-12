package com.vaccarostudio.bitcoinblockexplorer.sqlite;


import android.util.Base64;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.StoredBlock;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class DBBlock {

    private long id;
    public String hash;
    public int height;
    public byte header[];
    public BigInteger chainWork;

    public long getId(){
        return id;
    }
    public void setId(long id){
        this.id = id;
    }
    public String getHash(){
        return hash;
    }
    public void setHash(String hash){
        this.hash = hash;
    }
    public long getHeight(){
        return height;
    }
    public void setHeight(int height){
        this.height = height;
    }
    public BigInteger getChainWork(){
        return chainWork;
    }
    public void setChainWork(BigInteger chainWork){
        this.chainWork = chainWork;
    }

    public String getHeaderString(){
        return byteArrayToHex(this.header);
    }
    public byte[] getHeader(){
        return this.header;
    }
    public void setHeader(String string) {
        this.header = hexToByteArray(string);
    }
    public void setHeader(byte[] header) {
        this.header = header;
    }

    public StoredBlock getStoreBlock(NetworkParameters parameters){
        return new StoredBlock(this.getBlock(parameters),this.chainWork,this.height);
    }

    public Block getBlock(NetworkParameters parameters){
        return new Block(parameters,this.header);
    }

    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private static byte[] hexToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];
        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
