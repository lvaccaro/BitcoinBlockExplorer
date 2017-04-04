package com.vaccarostudio.bitcoinblockexplorer.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;

import java.util.LinkedHashMap;

import com.vaccarostudio.bitcoinblockexplorer.MySnackbar;
import com.vaccarostudio.bitcoinblockexplorer.R;
import com.vaccarostudio.bitcoinblockexplorer.adapters.ItemAdapter;
import com.vaccarostudio.bitcoinblockexplorer.insight.Insight;
import com.vaccarostudio.bitcoinblockexplorer.Bitcoin;
import com.vaccarostudio.bitcoinblockexplorer.insight.Block;


public class BlockActivity extends AppCompatActivity implements ItemAdapter.OnItemClickListener {



    private RecyclerView mRecyclerView;
    private ItemAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LinkedHashMap<String, String> mDataset = new LinkedHashMap<>();
    private StoredBlock storedBlock;
    private Block block;
    private String block_address;

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
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        setStatusPending();


        try {
            getBlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBlock() throws Exception {
        try {
            block_address = getIntent().getStringExtra("block");
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception();
        }

        block=null;
        storedBlock=null;

        new AsyncTask<Void,Void,Boolean>(){
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    block = null;
                    block = Insight.getBlock(block_address);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean == false){
                    MySnackbar.showNegative(BlockActivity.this, "Sorry! Not connected to internet");
                    setStatusFailed();
                    return;
                }

                checkLocalBlockStore();

                mDataset.clear();
                if (block != null) {
                    mDataset = block.toDataset();
                    mAdapter = new ItemAdapter(mDataset);
                    mAdapter.setOnItemClickListener(BlockActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }.execute();
    }


    public void checkLocalBlockStore(){
        new AsyncTask<Void,Void,Boolean>(){
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    storedBlock = null;
                    Sha256Hash blockHash = new Sha256Hash(block_address);
                    storedBlock = Bitcoin.blockStore.get(blockHash);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean == false){
                    MySnackbar.showNegative(BlockActivity.this, "No block found");
                    setStatusFailed();
                    return;
                }
                if (storedBlock == null){
                    MySnackbar.showWarning(BlockActivity.this, "Blockchain not sync");
                    return;
                }
                if( block.equals(storedBlock) ){
                    MySnackbar.showPositive(BlockActivity.this, "Verification Success");
                    setStatusSuccess();
                } else {
                    MySnackbar.showNegative(BlockActivity.this, "Verification Failed");
                    setStatusFailed();
                }

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                MySnackbar.showWarning(BlockActivity.this, "Waiting sync");
                setStatusPending();
            }
        }.execute();
    }


    private void refresh(final StoredBlock storedBlock){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update info
                mDataset.clear();
                if(storedBlock!=null){
                    mDataset.put("Hash",storedBlock.getHeader().getHashAsString());
                    mDataset.put("Height",String.valueOf(storedBlock.getHeight()));
                    //mDataset.put("Prev",storedBlock.getPrev(blockStore).getHeader().getHashAsString());
                    mDataset.put("MerkleRoot",storedBlock.getHeader().getMerkleRoot().toString());
                    mDataset.put("DifficultyTarget",String.valueOf(storedBlock.getHeader().getDifficultyTarget()));
                    mDataset.put("Nonce",String.valueOf(storedBlock.getHeader().getNonce()));
                    mDataset.put("Date",storedBlock.getHeader().getTime().toLocaleString());
                    mDataset.put("Version",String.valueOf(storedBlock.getHeader().getVersion()));
                    mDataset.put("Work",String.valueOf(storedBlock.getHeader().getWork()));
                    //mDataset.put("Transactions",String.valueOf(storedBlock.getHeader().getTransactions().size()));
                    mAdapter.notifyDataSetChanged();
            }
        }
    });
    }



    public void setStatusPending(){
        setStatus("Verification Pending");
    }
    public void setStatusFailed(){
        setStatus("Verification Failed");
    }
    public void setStatusSuccess(){
        setStatus("Verification Success");
    }
    public void setStatus(String message){
        ((TextView)findViewById(R.id.tvStatus)).setText(message);
    }

    @Override
    public void onItemClick(View view, int position, String key) {
        System.out.println("onItemClick" + key);
        if (key == null) {
            return;
        }
        String value = mDataset.get(key);
        if (value == null) {
            return;
        }
        if (key.contains("Block")) {
            Intent intent = new Intent(BlockActivity.this, BlockActivity.class);
            intent.putExtra("block", value);
            startActivity(intent);
        } else if (key.contains(" Transaction")) {
            Intent intent = new Intent(BlockActivity.this, TxActivity.class);
            intent.putExtra("tx", value);
            startActivity(intent);
        } else {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(value);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", value);
                clipboard.setPrimaryClip(clip);
            }
            MySnackbar.showPositive(BlockActivity.this, getResources().getString(R.string.copied_text_to_clipboard));
        }
    }
}
