package io.merkur.bitcoinblockexplorer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bitcoinj.core.Transaction;

import java.util.ArrayList;
import java.util.List;

import io.merkur.bitcoinblockexplorer.R;
import io.merkur.bitcoinblockexplorer.activities.TxActivity;


public class TxsAdapter extends RecyclerView.Adapter<TxsAdapter.ViewHolder> {

    private List<Transaction> mDataset = new ArrayList<>();

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder

public static class ViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public TextView mTvTitle,mTvHash;
    public ViewHolder(View v) {
        super(v);
        mTvTitle = (TextView) v.findViewById(R.id.tvTitle);
        mTvHash = (TextView) v.findViewById(R.id.tvHash);
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
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Context context = view.getContext();
                        Intent intent = new Intent(context, TxActivity.class);
                        intent.putExtra("tx",tx.getHashAsString().toString());
                        view.getContext().startActivity(intent);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}