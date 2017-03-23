package io.merkur.bitcoinblockexplorer.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStoreException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import io.merkur.bitcoinblockexplorer.insight.Block;
import io.merkur.bitcoinblockexplorer.insight.Insight;
import io.merkur.bitcoinblockexplorer.insight.Tx;
import io.merkur.bitcoinblockexplorer.insight.ValueIn;
import io.merkur.bitcoinblockexplorer.insight.ValueOut;
import io.merkur.bitcoinblockexplorer.R;
import io.merkur.bitcoinblockexplorer.adapters.ItemAdapter;

import static io.merkur.bitcoinblockexplorer.MyApplication.blockStore;

public class TxActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private final HashMap<String, String> mDataset = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(this, R.drawable.divider_grey);
        horizontalDecoration.setDrawable(horizontalDivider);
        mRecyclerView.addItemDecoration(horizontalDecoration);

        // specify an adapter (see also next example)
        mAdapter = new ItemAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);

        try {
            getTx();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTx() throws Exception {
        String tx_address;
        try {
            tx_address = getIntent().getStringExtra("tx");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
        insigthTx(tx_address);
    }

    private void refresh() {

        mDataset.clear();

        if (tx == null || block == null) {
            return;
        }

        if (tx.txid != null) {
            mDataset.put("txid", String.valueOf(tx.txid));
        }
        if (tx.blockhash != null) {
            mDataset.put("blockhash", String.valueOf(tx.blockhash));
        }
        if (tx.blockheight != null) {
            mDataset.put("blockheight", String.valueOf(tx.blockheight));
        }
        if (tx.blocktime != null) {
            mDataset.put("blocktime", String.valueOf(tx.blocktime));
        }
        if (tx.confirmations != null) {
            mDataset.put("confirmations", String.valueOf(tx.confirmations));
        }
        if (tx.fees != null) {
            mDataset.put("fees", String.valueOf(tx.fees));
        }
        if (tx.size != null) {
            mDataset.put("size", String.valueOf(tx.size));
        }
        if (tx.time != null) {
            mDataset.put("time", String.valueOf(tx.time));
        }
        if (tx.version != null) {
            mDataset.put("version", String.valueOf(tx.version));
        }

        if (tx.vin != null) {
            for (ValueIn vin : tx.vin) {
                mDataset.put("Vin", vin.toString());
            }
        }
        if (tx.vout != null) {
            for (ValueOut vout : tx.vout) {
                mDataset.put("Vout", vout.toString());
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    Tx tx;
    Block block;
    StoredBlock storedBlock;


    public void insigthTx(String txid) {



        String url = Insight.url + "/tx/" + txid;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            tx = Tx.getJson(response);
                            insigthBlock(tx.blockhash);
                            refresh();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showSnack("Request Insight failed");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        showSnack("Request Insight failed");
                    }
                }
        );
    }

    public void insigthBlock(String blockhash) {
        String url = Insight.url + "/block/" + blockhash;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        block = Block.getJson(response);
                            verify();
                            refresh();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        showSnack("Request Insight failed");
                    }
                }
        );
    }




    public void verify(){
        // Generate Merkle Root from TXs
        String merkleRoot = Block.merkle(block.tx);

        // Get block from blockstore
        Sha256Hash hash = new Sha256Hash(block.hash);
        try {
            storedBlock = blockStore.get(hash);
        } catch (BlockStoreException e) {
            e.printStackTrace();
            showSnack("Error to retrieve store block");
            return;
        }

        // Compare Markle Root
        if(merkleRoot.equals(block.merkleroot)){
            showSnack("Merkle Root Verified");
        } else {
            showSnack("Merkle Root Invalid");
        }
    }


    // Showing the status in Snackbar
    private void showSnack(String message) {
        Snackbar snackbar = Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        snackbar.show();
    }
}
