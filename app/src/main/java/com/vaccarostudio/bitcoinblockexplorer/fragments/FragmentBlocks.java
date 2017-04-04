package com.vaccarostudio.bitcoinblockexplorer.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.FilteredBlock;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.core.listeners.BlocksDownloadedEventListener;
import org.bitcoinj.core.listeners.NewBestBlockListener;
import org.bitcoinj.store.BlockStoreException;

import java.util.LinkedHashMap;

import javax.annotation.Nullable;

import com.vaccarostudio.bitcoinblockexplorer.Bitcoin;
import com.vaccarostudio.bitcoinblockexplorer.R;
import com.vaccarostudio.bitcoinblockexplorer.activities.BlockActivity;
import com.vaccarostudio.bitcoinblockexplorer.activities.MainActivity;
import com.vaccarostudio.bitcoinblockexplorer.adapters.BlocksAdapter;

import static com.vaccarostudio.bitcoinblockexplorer.Bitcoin.blockChain;
import static com.vaccarostudio.bitcoinblockexplorer.Bitcoin.blockStore;
import static com.vaccarostudio.bitcoinblockexplorer.Bitcoin.peerGroup;


public class FragmentBlocks extends Fragment implements Bitcoin.MyListener, BlocksAdapter.OnItemClickListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String TITLE = "BLOCKS";


    private RecyclerView mRecyclerView;
    private BlocksAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView tvStatus;
    public final LinkedHashMap<Block, Integer> blocks = new LinkedHashMap<>();
    private long lastTimestamp=0;

    public FragmentBlocks() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blocks, container, false);
        tvStatus = (TextView) rootView.findViewById(R.id.tvStatus);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(getActivity(), R.drawable.divider_grey);
        horizontalDecoration.setDrawable(horizontalDivider);
        mRecyclerView.addItemDecoration(horizontalDecoration);

        // specify an adapter (see also next example)
        mAdapter = new BlocksAdapter(blocks);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        refreshUI();

        return rootView;
    }

    BlocksDownloadedEventListener blocksDownloadedEventListener=new BlocksDownloadedEventListener() {
        @Override
        public void onBlocksDownloaded(Peer peer, Block block, @Nullable FilteredBlock filteredBlock, int blocksLeft) {
            //Log.d("BlocksDownloaded",block.toString());
            Integer height=blockChain.getChainHead().getHeight();

            synchronized (blocks) {
                blocks.put(block, height);
                if (blocks.keySet().size() >= 10) {
                    blocks.remove(blocks.keySet().toArray()[blocks.keySet().size() - 1]);
                }
            }

            if(System.currentTimeMillis()-lastTimestamp>1000) {
                refreshUI();
                lastTimestamp = System.currentTimeMillis();
            }
        }
    };

    NewBestBlockListener newBestBlockListener = new NewBestBlockListener() {
        @Override
        public void notifyNewBestBlock(StoredBlock block) throws VerificationException {
            //Log.d("NewBestBlock",block.toString());
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bitcoin.mListeners.add(this);
        attach();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Bitcoin.mListeners.remove(this);
    }

    public void attach() {
        if(peerGroup!=null && blockChain!=null) {
            peerGroup.addBlocksDownloadedEventListener(blocksDownloadedEventListener);
            blockChain.addNewBestBlockListener(newBestBlockListener);
        }
    }

    public void detach() {
        if(peerGroup!=null && blockChain!=null) {
            peerGroup.removeBlocksDownloadedEventListener(blocksDownloadedEventListener);
            blockChain.removeNewBestBlockListener(newBestBlockListener);
        }
    }

    private void refreshUI(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(peerGroup==null || blockStore==null){
                        tvStatus.setText("0 "+ getResources().getString(R.string.current_height));
                    }else {
                        Integer maxHeight = peerGroup.getMostCommonChainHeight();
                        Integer curHeight = blockStore.getChainHead().getHeight();

                        tvStatus.setText(curHeight + "/" + maxHeight + " " + getResources().getString(R.string.current_height));
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (BlockStoreException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startCallback() {
        attach();
    }

    public void stopCallback() {
        detach();
    }

    @Override
    public void onItemClick(View view, int position, String key) {
        System.out.println("onItemClick" + key);
        if (key == null) {
            return;
        }
        Intent intent = new Intent(getContext(), BlockActivity.class);
        intent.putExtra("block",key);
        startActivityForResult(intent, MainActivity.RESULT_BLOCK);
    }
}