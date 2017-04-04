package com.vaccarostudio.bitcoinblockexplorer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vaccarostudio.bitcoinblockexplorer.R;

import org.bitcoinj.core.Transaction;

import java.util.ArrayList;
import java.util.List;



public class TxsAdapter extends RecyclerView.Adapter<TxsAdapter.ViewHolder> {

    private List<Transaction> mDataset = new ArrayList<>();
    TxsAdapter.OnItemClickListener mItemClickListener;

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder

public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
    // each data item is just a string in this case
    public TextView mTvTitle,mTvHash;
    public String key;
    public ViewHolder(View v) {
        super(v);
        mTvTitle = (TextView) v.findViewById(R.id.tvTitle);
        mTvHash = (TextView) v.findViewById(R.id.tvHash);
        mTvTitle.setOnClickListener(this);
        mTvHash.setOnClickListener(this);
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
    public TxsAdapter(List<Transaction> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TxsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tx, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTvTitle.setText("");
        synchronized (mDataset) {
            try {
                final Transaction tx = mDataset.get(position);
                holder.mTvTitle.setText(String.valueOf(tx.getOutputSum().getValue()/100000000) + " BTC"+((tx.isPending())?" (U)":""));
                holder.mTvHash.setText(tx.getHashAsString());
                holder.key = tx.getHashAsString().toString();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position, String id);
    }

    public void setOnItemClickListener(TxsAdapter.OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}