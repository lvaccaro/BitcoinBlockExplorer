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

import org.bitcoinj.core.AbstractBlockChain;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.core.listeners.TransactionReceivedInBlockListener;

import java.util.ArrayList;
import java.util.List;

import com.vaccarostudio.bitcoinblockexplorer.Bitcoin;
import com.vaccarostudio.bitcoinblockexplorer.R;
import com.vaccarostudio.bitcoinblockexplorer.activities.MainActivity;
import com.vaccarostudio.bitcoinblockexplorer.activities.TxActivity;
import com.vaccarostudio.bitcoinblockexplorer.adapters.TxsAdapter;


public class FragmentTxs extends Fragment implements Bitcoin.MyListener, TxsAdapter.OnItemClickListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String TITLE = "TXS";


    private RecyclerView mRecyclerView;
    private TxsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView tvStatus;
    private List<Transaction> transactions = new ArrayList<>();
    private long lastTimestamp=0;
    private long countTransactions=0;

    public FragmentTxs() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_txs, container, false);
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
        mAdapter = new TxsAdapter(transactions);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        refreshUI();

        return rootView;
    }


    TransactionReceivedInBlockListener transactionReceivedInBlockListener=new TransactionReceivedInBlockListener() {
        @Override
        public void receiveFromBlock(Transaction tx, StoredBlock block, AbstractBlockChain.NewBlockType blockType, int relativityOffset) throws VerificationException {
            //Log.d("TransactionIsInBlock",block.toString());

            synchronized (transactions) {
                countTransactions++;
                transactions.add(tx);
                if (transactions.size() >= 10) {
                    transactions.remove(10);
                }
            }

            if(System.currentTimeMillis()-lastTimestamp>1000) {
                refreshUI();
                lastTimestamp = System.currentTimeMillis();
            }
        }

        @Override
        public boolean notifyTransactionIsInBlock(Sha256Hash txHash, StoredBlock block, AbstractBlockChain.NewBlockType blockType, int relativityOffset) throws VerificationException {
            //Log.d("TransactionIsInBlock",block.toString());
            return false;
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
        if(Bitcoin.blockChain!=null) {
            Bitcoin.blockChain.addTransactionReceivedListener(transactionReceivedInBlockListener);
        }
    }

    public void detach() {
        super.onDetach();
        if(Bitcoin.blockChain!=null) {
            Bitcoin.blockChain.removeTransactionReceivedListener(transactionReceivedInBlockListener);
        }
    }

    private void refreshUI(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    tvStatus.setText(String.valueOf(countTransactions)+" " +getResources().getString(R.string.transactions));
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //mAdapter.notifyDataSetChanged();
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
        Intent intent = new Intent(getContext(), TxActivity.class);
        intent.putExtra("tx",key);
        startActivityForResult(intent, MainActivity.RESULT_TX);
    }


}