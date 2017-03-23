package io.merkur.bitcoinblockexplorer.fragments;

import android.content.Context;
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

import org.bitcoinj.core.Peer;
import org.bitcoinj.core.listeners.PeerConnectedEventListener;
import org.bitcoinj.core.listeners.PeerDisconnectedEventListener;

import java.util.HashMap;
import java.util.LinkedHashMap;

import io.merkur.bitcoinblockexplorer.MyApplication;
import io.merkur.bitcoinblockexplorer.R;
import io.merkur.bitcoinblockexplorer.adapters.PeersAdapter;

import static io.merkur.bitcoinblockexplorer.MyApplication.peerGroup;


public class FragmentPeers extends Fragment implements MyApplication.MyListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String TITLE = "PEERS";


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView tvStatus;
    public final LinkedHashMap<Peer, String> reverseDnsLookups = new LinkedHashMap<>();
    private long lastTimestamp=0;

    public FragmentPeers() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_peers, container, false);
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
        mAdapter = new PeersAdapter(reverseDnsLookups);
        mRecyclerView.setAdapter(mAdapter);

        refreshUI();

        return rootView;
    }

    PeerConnectedEventListener peerConnectedEventListener=new PeerConnectedEventListener() {
        @Override
        public void onPeerConnected(final Peer peer, int peerCount) {

            if(System.currentTimeMillis()-lastTimestamp>1000) {
                refreshUI();
                lastTimestamp = System.currentTimeMillis();
            }
            lookupReverseDNS(peer);
        }
    };

    PeerDisconnectedEventListener peerDisconnectedEventListener = new PeerDisconnectedEventListener() {
        @Override
        public void onPeerDisconnected(final Peer peer, int peerCount) {

            if(System.currentTimeMillis()-lastTimestamp>1000) {
                refreshUI();
                lastTimestamp = System.currentTimeMillis();
            }

            synchronized (reverseDnsLookups) {
                reverseDnsLookups.remove(peer);
            }
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MyApplication.mListeners.add(this);
        attach();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MyApplication.mListeners.remove(this);
    }


    public void attach(){
        reverseDnsLookups.clear();
        if(peerGroup!=null) {
            peerGroup.addConnectedEventListener(peerConnectedEventListener);
            peerGroup.addDisconnectedEventListener(peerDisconnectedEventListener);
            for (Peer peer : peerGroup.getConnectedPeers()) {
                lookupReverseDNS(peer);
            }
        }
    }
    public void detach(){
        if(peerGroup!=null) {
            peerGroup.removeConnectedEventListener(peerConnectedEventListener);
            peerGroup.removeDisconnectedEventListener(peerDisconnectedEventListener);
        }
    }

    private void refreshUI(){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    tvStatus.setText(reverseDnsLookups.size() + " "+getResources().getString(R.string.peers_connected));
                    mAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void lookupReverseDNS(final Peer peer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String reverseDns = peer.getAddress().getAddr().getCanonicalHostName();
                synchronized (reverseDnsLookups) {
                    reverseDnsLookups.put(peer, reverseDns);
                }
                refreshUI();
            }
        }).start();
    }

    public void startCallback() {
        attach();
    }

    public void stopCallback() {
        detach();
    }


}