package io.merkur.bitcoinblockexplorer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bitcoinj.core.Peer;

import java.util.HashMap;
import java.util.LinkedHashMap;

import io.merkur.bitcoinblockexplorer.activities.PeerActivity;
import io.merkur.bitcoinblockexplorer.R;


public class PeersAdapter extends RecyclerView.Adapter<PeersAdapter.ViewHolder> {

    LinkedHashMap<Peer, String> mDataset = new LinkedHashMap<>();
    PeersAdapter.OnItemClickListener mItemClickListener;

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder

public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
    // each data item is just a string in this case
    public TextView mTvTitle,mTvIp,mTvBestHeight,mTvLastPingTime;
    public String key;
    public ViewHolder(View v) {
        super(v);
        mTvTitle = (TextView) v.findViewById(R.id.tvTitle);
        mTvIp = (TextView) v.findViewById(R.id.tvIp);
        mTvBestHeight = (TextView) v.findViewById(R.id.tvBestHeight);
        mTvLastPingTime = (TextView) v.findViewById(R.id.tvLastPingTime);
        mTvTitle.setOnClickListener(this);
        mTvIp.setOnClickListener(this);
        mTvBestHeight.setOnClickListener(this);
        mTvLastPingTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        System.out.println("onClick");
        if(mItemClickListener != null) {
            mItemClickListener.onItemClick(v, getAdapterPosition(), key); //OnItemClickListener mItemClickListener;
        }
    }
}
    // Provide a suitable constructor (depends on the kind of dataset)
    public PeersAdapter(LinkedHashMap<Peer, String>  myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PeersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.peer, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        synchronized (mDataset) {
            final Peer peer = (Peer) (mDataset.keySet().toArray()[position]);
            String name = (String) (mDataset.values().toArray())[position];

            holder.mTvTitle.setText(name.toString());
            holder.mTvIp.setText("Host: " + peer.getAddress().toString());
            holder.mTvBestHeight.setText("BestHeight: " + String.valueOf(peer.getBestHeight()));
            holder.mTvLastPingTime.setText("LastPingTime: " + String.valueOf(peer.getLastPingTime()));
            holder.key = peer.getAddress().toString();
        }
    }


    public interface OnItemClickListener {
        public void onItemClick(View view, int position, String id);
    }

    public void setOnItemClickListener(PeersAdapter.OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}