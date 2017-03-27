package io.merkur.bitcoinblockexplorer.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.merkur.bitcoinblockexplorer.Bitcoin;
import io.merkur.bitcoinblockexplorer.ConnectivityReceiver;
import io.merkur.bitcoinblockexplorer.MyApplication;
import io.merkur.bitcoinblockexplorer.R;
import io.merkur.bitcoinblockexplorer.fragments.FragmentBlocks;
import io.merkur.bitcoinblockexplorer.fragments.FragmentPeers;
import io.merkur.bitcoinblockexplorer.fragments.FragmentTxs;
import io.merkur.bitcoinblockexplorer.insight.Block;
import io.merkur.bitcoinblockexplorer.insight.Insight;
import io.merkur.bitcoinblockexplorer.insight.Tx;

import static io.merkur.bitcoinblockexplorer.MyApplication.REQUEST_EXTERNAL_STORAGE;
import static io.merkur.bitcoinblockexplorer.MyApplication.verifyStoragePermissions;

public class MainActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, SearchView.OnCloseListener,
         ConnectivityReceiver.ConnectivityReceiverListener{


    public static int RESULT_BLOCK = 101;
    public static int RESULT_TX = 102;
    public static int RESULT_PEER = 103;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private SearchView searchView;
    private MenuItem searchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //((MyApplication)getApplication()).startBlockChain();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        if (savedInstanceState==null) {
            checkConnection();
            if (verifyStoragePermissions(MainActivity.this)==true) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitcoin.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }

        // Get bundle object at appropriate place in your code
        /*try {
            String data = getIntent().getDataString();
            Log.d("data",data);
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack((isConnected)?"Good! Sync starting":"Sorry! Not connected to internet");
    }

    // Showing the status in Snackbar
    private void showSnack(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        snackbar.setDuration(10*1000);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitcoin.resume();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/

    }
    @Override
    protected void onPause() {
        super.onPause();
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitcoin.pause();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
    }

        /**
         * Callback will be triggered when there is change in
         * network connection
         */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack((isConnected)?"Good! Sync starting":"Sorry! Not connected to internet");
        if(ConnectivityReceiver.isConnected()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitcoin.resume();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitcoin.pause();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        //SearchManager searchManager = (SearchManager)         getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Return true to expand action view
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Write your code here
                onClose();
                // Return true to collapse action view
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            //searchable element
            return true;
        } else if (id == R.id.action_reset) {

            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Reset..");
            alertDialog.setMessage("Are you sure?");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getString(android.R.string.ok), new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Bitcoin.destroy();
                                Bitcoin.clear();
                                Bitcoin.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getString(android.R.string.cancel), new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();


            return true;
        } else if (id == R.id.action_github) {
            //searchable element
            String url="https://github.com/lvaccaro/BitcoinBlockExplorer";
            Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    static Block block;
    static Tx tx;
    static String blockHash;


    @Override
    public boolean onQueryTextSubmit(final String query) {
        // OnQueryTextSubmit was called twice, so clean
        searchView.setIconified(true);
        searchView.clearFocus();

        // clear
        block=null;
        tx=null;
        blockHash=null;

        // Pool Insight requests
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    block = Insight.getBlock(query);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    blockHash = Insight.getBlockHash(query);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    tx = Insight.getTx(query);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        executor.shutdown();
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // handle results
        if(blockHash!=null) {
            Intent intent = new Intent(MainActivity.this, BlockActivity.class);
            intent.putExtra("block", blockHash.toString());
            startActivity(intent);
        } else if(block!=null) {
            Intent intent = new Intent(MainActivity.this, BlockActivity.class);
            intent.putExtra("block", block.hash.toString());
            startActivity(intent);
        } else if(tx!=null) {
            Intent intent = new Intent(MainActivity.this, TxActivity.class);
            intent.putExtra("tx", tx.txid.toString());
            startActivity(intent);
        } else {
            showSnack("No height/block/tx found");
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onClose() {
        return false;
    }
    @Override
    public void onBackPressed() {
        if(searchMenuItem!=null && searchMenuItem.isActionViewExpanded()){
                searchMenuItem.collapseActionView();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0){
                return new FragmentPeers();
            }else if(position==1){
                return new FragmentBlocks();
            }else {
                return new FragmentTxs();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return FragmentPeers.TITLE;
                case 1:
                    return FragmentBlocks.TITLE;
                case 2:
                    return FragmentTxs.TITLE;
            }
            return null;
        }
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Bitcoin.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
