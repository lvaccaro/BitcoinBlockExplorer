package com.vaccarostudio.bitcoinblockexplorer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vaccarostudio.bitcoinblockexplorer.R;

import org.bitcoinj.core.Block;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;


public class BlocksAdapter extends RecyclerView.Adapter<BlocksAdapter.ViewHolder> {

    LinkedHashMap<Block, Integer> mDataset = new LinkedHashMap<>();
    BlocksAdapter.OnItemClickListener mItemClickListener;

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    // each data item is just a string in this case
    public TextView mTvTitle,mTvBlockHash,mTvHeight,mTvTransactions;
    public String key;
    public ViewHolder(View v) {
        super(v);
        mTvTitle = (TextView) v.findViewById(R.id.tvTitle);
        mTvBlockHash = (TextView) v.findViewById(R.id.tvBlockHash);
        mTvHeight = (TextView) v.findViewById(R.id.tvHeight);
        mTvTransactions = (TextView) v.findViewById(R.id.tvTransactions);
        mTvBlockHash.setOnClickListener(this);
        mTvHeight.setOnClickListener(this);
        mTvTransactions.setOnClickListener(this);
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
    public BlocksAdapter(LinkedHashMap<Block, Integer> myDataset) {
        this.mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BlocksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.block2, parent, false);
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
            final Block block = (Block) (mDataset.keySet().toArray()[position]);
            Integer height = (Integer) (mDataset.values().toArray())[position];

            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm");
            String formatted = dateFormat.format(block.getTime());
            holder.mTvTitle.setText(formatted);
            holder.mTvBlockHash.setText(block.getHashAsString());
            holder.mTvHeight.setText(String.valueOf(height));
            holder.mTvTransactions.setText(String.valueOf(block.getTransactions().size()));
            holder.key = block.getHashAsString().toString();
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position, String id);
    }

    public void setOnItemClickListener(BlocksAdapter.OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}