package io.merkur.bitcoinblockexplorer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.FilteredBlock;
import org.bitcoinj.core.GetDataMessage;
import org.bitcoinj.core.Message;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryBlockStore;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends MultiDexApplication {

    /* GLOBAL */

    public static NetworkParameters netParams;
    public static PeerGroup peerGroup;
    public static MemoryBlockStore blockStore;
    public static BlockChain blockChain;
    public static List<MyListener> mListeners = new ArrayList<>();
    public static MyDownload myDownload;

    public static String filePrefix = "explorer";
    public static String SPV_BLOCKCHAIN_SUFFIX = ".spvchain";
    public static String CHECKPOINTS_SUFFIX = ".checkpoint";
    //public static String blockchainFilename = filePrefix + SPV_BLOCKCHAIN_SUFFIX;
    //public static String checkpointsFilename = filePrefix + CHECKPOINTS_SUFFIX;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        mInstance = this;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    private static MyApplication mInstance;
    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void startBlockChain(){

        // work with testnet
        Log.d("BITCOINJ","Set Network\n");
        netParams = TestNet3Params.get();
        Log.d("BITCOINJ","Network = " + netParams.toString()+"\n");

        // data structure for block chain storage

        /*String blockchainFilename = Environment.getExternalStorageDirectory(  )+"/"+filePrefix + SPV_BLOCKCHAIN_SUFFIX;
        Log.d("BITCOINJ","Get or create SPV block store:"+blockchainFilename+"\n");
        File blockStoreFile = new File(blockchainFilename);
        if(!blockStoreFile.exists()){
            Log.d("BITCOINJ","BlockStoreFile created");
        }
        try {
            blockStore = new SPVBlockStore(netParams,blockStoreFile);
        } catch (BlockStoreException e) {
            e.printStackTrace();
            Log.d("BITCOINJ","Failed to get or create SPV block store: "+e.getMessage());
        }*/

        Log.d("BITCOINJ","Get or create Memory block store \n");
        blockStore = new MemoryBlockStore(netParams);

        // initialize BlockChain object
        Log.d("BITCOINJ","Create BlockChain");
        try {
            blockChain = new BlockChain(netParams, blockStore);
        } catch (BlockStoreException e) {
            e.printStackTrace();
            Log.d("BITCOINJ","Failed to create BlockChain: "+e.getMessage());
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

        for (MyListener listener : mListeners){
            listener.startCallback();
        }

    }

    public void stopBlockChain(){
        peerGroup.stop();
    }
    public interface MyListener {
        void startCallback();
        void stopCallback();
    }

    public class MyDownload extends DownloadProgressTracker {
        @Override
        public void onChainDownloadStarted(Peer peer, int blocksLeft) {
            super.onChainDownloadStarted(peer, blocksLeft);
            //Log.i("onChainDownloadStarted", "");

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
