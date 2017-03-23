package io.merkur.bitcoinblockexplorer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bitcoinj.core.Block;

import java.util.HashMap;

import io.merkur.bitcoinblockexplorer.activities.BlockActivity;
import io.merkur.bitcoinblockexplorer.R;

public class BlocksAdapter extends RecyclerView.Adapter<BlocksAdapter.ViewHolder> {

    HashMap<Block, Integer> mDataset = new HashMap<>();

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder

public static class ViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public TextView mTvTitle,mTvBlockHash,mTvHeight,mTvTransactions;
    public ViewHolder(View v) {
        super(v);
        mTvTitle = (TextView) v.findViewById(R.id.tvTitle);
        mTvBlockHash = (TextView) v.findViewById(R.id.tvBlockHash);
        mTvHeight = (TextView) v.findViewById(R.id.tvHeight);
        mTvTransactions = (TextView) v.findViewById(R.id.tvTransactions);

    }
}
    // Provide a suitable constructor (depends on the kind of dataset)
    public BlocksAdapter(HashMap<Block, Integer> myDataset) {
        this.mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BlocksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.block, parent, false);
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

            holder.mTvTitle.setText(block.getTime().toLocaleString());
            holder.mTvBlockHash.setText(block.getHashAsString());
            holder.mTvHeight.setText("Height: " + String.valueOf(height));
            holder.mTvTransactions.setText("Transactions: " + block.getTransactions().size());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, BlockActivity.class);
                    intent.putExtra("block",block.getHashAsString().toString());
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}