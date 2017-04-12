package com.vaccarostudio.bitcoinblockexplorer.sqlite;

import android.content.Context;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by luca on 11/04/2017.
 */

public class SQLiteBlockStore implements BlockStore {

    private static final Logger log = LoggerFactory.getLogger(SQLiteBlockStore.class);
    private BlocksDataSource blocksDataSource;
    private Sha256Hash chainHead;
    private NetworkParameters params;

    public SQLiteBlockStore(NetworkParameters params, Context context) throws BlockStoreException {
        this.params = params;
        blocksDataSource = new BlocksDataSource(context);

        blocksDataSource.open();
        if (blocksDataSource.count()==0){
            createNewBlockStore(params);
        } else{
            try {
                DBBlock block = blocksDataSource.getLast();
                Block b = new Block(params, block.getHeader());
                StoredBlock s = build(b);
                this.chainHead = s.getHeader().getHash();
            } catch (Exception e) {
                throw new BlockStoreException("Invalid BlockStore chainHead");
            }
        }
        blocksDataSource.close();
    }

    @Override
    public synchronized void put(StoredBlock block) throws BlockStoreException {

        blocksDataSource.open();
        Sha256Hash hash = block.getHeader().getHash();
        try {
            blocksDataSource.getHash(hash.toString());
        } catch (Exception e) {
            DBBlock dbBlock = new DBBlock();
            dbBlock.setHash(hash.toString());
            dbBlock.setHeight(block.getHeight());
            dbBlock.setHeader(block.getHeader().bitcoinSerialize());
            dbBlock.setChainWork(block.getChainWork());
            blocksDataSource.insert(dbBlock);
        }
        blocksDataSource.close();
    }

    @Override
    public synchronized StoredBlock get(Sha256Hash hash) throws BlockStoreException {

        if(hash==null)
            throw new BlockStoreException("Invalid hash");

        blocksDataSource.open();
        DBBlock block = null;
        try {
            block = blocksDataSource.getHash(hash.toString());
        } catch (Exception e) {
            blocksDataSource.close();
            return null;
        }

        Block b = new Block(params, block.getHeader());
        StoredBlock s = build(b);

        blocksDataSource.close();
        return s;
    }

    private StoredBlock build(Block b) throws BlockStoreException{
        // Look up the previous block it connects to.
        StoredBlock s;

        DBBlock dbBlockPrev = null;
        try {
            dbBlockPrev = blocksDataSource.getHash(b.getPrevBlockHash().toString());
        } catch (Exception e) {
            dbBlockPrev = null;
        }
        if (dbBlockPrev == null) {
            // First block in the stored chain has to be treated specially.
            if (b.equals(params.getGenesisBlock())) {
                s = new StoredBlock(params.getGenesisBlock().cloneAsHeader(), params.getGenesisBlock().getWork(), 0);
            } else {
                throw new BlockStoreException("Could not connect " + b.getHash().toString() + " to " + b.getPrevBlockHash().toString());
            }
        } else {
            // Don't try to verify the genesis block to avoid upsetting the unit tests.
            b.verifyHeader();
            // Calculate its height and total chain work.
            StoredBlock prev = dbBlockPrev.getStoreBlock(params);
            s = prev.build(b);
        }
        return s;
    }

    @Override
    public synchronized StoredBlock getChainHead() throws BlockStoreException {
        return this.get(chainHead);
    }

    @Override
    public synchronized void setChainHead(StoredBlock chainHead) throws BlockStoreException {
        this.chainHead = chainHead.getHeader().getHash();
        this.put(chainHead);
    }

    @Override
    public synchronized void close() throws BlockStoreException {
        blocksDataSource.close();
    }

    public synchronized void open(){
        blocksDataSource.open();
    }

    @Override
    public NetworkParameters getParams() {
        return params;
    }

    public synchronized void createNewBlockStore(NetworkParameters params) throws BlockStoreException {
        // Set up the genesis block. When we start out fresh, it is by definition the top of the chain.
        Block genesis = params.getGenesisBlock().cloneAsHeader();
        StoredBlock storedGenesis = new StoredBlock(genesis, genesis.getWork(), 0);
        this.chainHead = storedGenesis.getHeader().getHash();
        this.put(storedGenesis);
    }
}
