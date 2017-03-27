package io.merkur.bitcoinblockexplorer;

import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.FilteredBlock;
import org.bitcoinj.core.GetDataMessage;
import org.bitcoinj.core.Message;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.LevelDBBlockStore;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.store.SPVBlockStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by luca on 24/03/2017.
 */

public class Bitcoin {

    /* GLOBAL */
    public static NetworkParameters netParams;
    public static PeerGroup peerGroup;
    //public static MemoryBlockStore blockStore;
    public static BlockStore blockStore;
    public static BlockChain blockChain;
    public static Bitcoin.MyDownload myDownload;
    public static List<Bitcoin.MyListener> mListeners = new ArrayList<>();

    /* CONSTANT */
    public static String filePrefix = "explorer";
    public static String SPV_BLOCKCHAIN_SUFFIX = ".spvchain";
    public static String CHECKPOINTS_SUFFIX = ".checkpoint";
    public static String DISK_SUFFIX = ".disk";
    public static String blockchainFilename = filePrefix + SPV_BLOCKCHAIN_SUFFIX;
    public static String checkpointsFilename = filePrefix + CHECKPOINTS_SUFFIX;
    public static String diskFilename = filePrefix + DISK_SUFFIX;

    public static File blockStoreFile;
    public static Boolean isStarted=false;

    public static void clear(){
        Log.d("BITCOINJ","Remove SPV block store:"+blockchainFilename+"\n");
        File blockStoreFile = new File(blockchainFilename);
        if(blockStoreFile.exists()){
            blockStoreFile.delete();
            Log.d("BITCOINJ","BlockStoreFile deleted");
        }
        isStarted=false;
    }

    public static void start() throws Exception {

        if (isStarted==true){
            Log.d("BITCOINJ","Just started\n");
            throw new Exception();
        }

        // work with net
        Log.d("BITCOINJ","Set Network\n");
        //netParams = TestNet3Params.get();
        netParams = NetworkParameters.prodNet();
        Log.d("BITCOINJ","Network = " + netParams.toString()+"\n");

        // Load the block chain data file or generate a new one
        String blockchainFilename = Environment.getExternalStorageDirectory(  )+"/"+diskFilename;
        Log.d("BITCOINJ","Get or create SPV block store:"+blockchainFilename+"\n");
        blockStoreFile = new File(blockchainFilename);
        if(!blockStoreFile.exists()){
            blockStoreFile.createNewFile();
            Log.d("BITCOINJ","BlockStoreFile created");
        }

        try {
            blockStore = new DiskBlockStore(netParams,blockStoreFile);
        } catch (BlockStoreException e) {
            e.printStackTrace();
            Log.d("BITCOINJ","Failed to get or create block store: "+e.getMessage());
            throw new Exception(e);
        }

        // initialize BlockChain object
        Log.d("BITCOINJ","Create BlockChain");
        try {
            blockChain = new BlockChain(netParams, blockStore);
        } catch (BlockStoreException e) {
            e.printStackTrace();
            Log.d("BITCOINJ","Failed to create BlockChain: "+e.getMessage());
            throw new Exception(e);
        }

        Log.d("BITCOINJ","Create PeerGroup");
        peerGroup = new PeerGroup(netParams, blockChain);
        peerGroup.setUserAgent("PeerMonitor", "1.0");
        peerGroup.setMaxConnections(4);
        peerGroup.addPeerDiscovery(new DnsDiscovery(netParams));

        Log.d("BITCOINJ","Start Asynchronous PeerGroup");
        peerGroup.start();

        Log.d("BITCOINJ","Starting Download");
        myDownload = new MyDownload();
        peerGroup.startBlockChainDownload(myDownload);
        Log.d("BITCOINJ","Download Started");
        isStarted=true;
    }

    public static void pause(){
        if(peerGroup!=null) {
            peerGroup.stop();
        }
    }

    public static void resume(){
        if(peerGroup!=null && !peerGroup.isRunning()) {
            peerGroup.start();
        }
    }

    public static void destroy(){
        isStarted=false;
        if(peerGroup!=null && peerGroup.isRunning()) {
            peerGroup.stop();
        }
        try {
            blockStore.close();
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }
    }

    public interface MyListener {
        void startCallback();
        void stopCallback();
    }

    public static class MyDownload extends DownloadProgressTracker {
        @Override
        public void onChainDownloadStarted(Peer peer, int blocksLeft) {
            super.onChainDownloadStarted(peer, blocksLeft);
            Log.i("onChainDownloadStarted", peer.getAddr().toString());
            for (MyListener listener : mListeners){
                listener.stopCallback();
                listener.startCallback();
            }
        }

        @Override
        protected void startDownload(int blocks) {
            super.startDownload(blocks);
            //Log.i("startDownload", "");
        }

        @Override
        protected void doneDownload() {
            Log.i("doneDownload", "");
        }

        @Override
        public void onBlocksDownloaded(Peer peer, Block block, FilteredBlock filteredBlock, int blocksLeft) {
            super.onBlocksDownloaded(peer, block, filteredBlock, blocksLeft);
            //Log.i("onBlocksDownloaded", peer.toString());
        }

        @Override
        public Message onPreMessageReceived(Peer peer, Message m) {
            //Log.i("onPreMessageReceived", peer.toString());
            return super.onPreMessageReceived(peer, m);
        }

        @Nullable
        @Override
        public List<Message> getData(Peer peer, GetDataMessage m) {
            //Log.i("getData", m.toString());
            //Log.i("onChainDownloadStarted", peer.toString());
            return null;
        }

    }

}
