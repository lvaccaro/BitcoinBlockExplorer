package com.vaccarostudio.bitcoinblockexplorer.sqlite;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;

import android.database.sqlite.SQLiteDatabase;

public class BlocksDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_HASH , MySQLiteHelper.COLUMN_HEIGHT, MySQLiteHelper.COLUMN_HEADER, MySQLiteHelper.COLUMN_CHAINWORK };

    public BlocksDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public DBBlock insert(DBBlock block) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_HASH, block.getHash());
        values.put(MySQLiteHelper.COLUMN_HEIGHT, block.getHeight());
        values.put(MySQLiteHelper.COLUMN_HEADER, block.getHeaderString());
        values.put(MySQLiteHelper.COLUMN_CHAINWORK, block.getChainWork().toString());
        long insertId = database.insert(MySQLiteHelper.TABLE_BLOCKSTORE, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BLOCKSTORE,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();

        DBBlock newBlock = cursorToBlock(cursor);
        cursor.close();
        return newBlock;
    }

    public void delete(DBBlock dbblock) {
        long id = dbblock.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_BLOCKSTORE, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public DBBlock get(int id) throws Exception {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BLOCKSTORE, allColumns, MySQLiteHelper.COLUMN_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor == null)
            throw new Exception();
        cursor.moveToFirst();
        return cursorToBlock(cursor);
    }
    public DBBlock getHash(String hash) throws Exception {
        Cursor cursor=null;
        try {
            cursor = database.query(MySQLiteHelper.TABLE_BLOCKSTORE, allColumns, MySQLiteHelper.COLUMN_HASH + "=?", new String[]{hash}, null, null, null, null);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (cursor == null)
            throw new Exception();
        cursor.moveToFirst();
        return cursorToBlock(cursor);
    }
    public DBBlock getHeight(long height) throws Exception {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BLOCKSTORE, allColumns, MySQLiteHelper.COLUMN_HEIGHT + "=?", new String[] { String.valueOf(height) }, null, null, null, null);
        if (cursor == null)
            throw new Exception();
        cursor.moveToFirst();
        return cursorToBlock(cursor);
    }

    public long count(){
        return DatabaseUtils.queryNumEntries(database, MySQLiteHelper.TABLE_BLOCKSTORE);
    }

    public List<DBBlock> getAll() {
        List<DBBlock> dbblocks = new ArrayList<DBBlock>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BLOCKSTORE, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            dbblocks.add(cursorToBlock(cursor));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return dbblocks;
    }

    public DBBlock getFirst() throws Exception {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BLOCKSTORE, allColumns, null, null, null, null, null);
        if (cursor == null)
            throw new Exception();
        cursor.moveToFirst();
        return cursorToBlock(cursor);
    }

    public DBBlock getLast() throws Exception {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BLOCKSTORE, allColumns, null, null, null, null, null);
        if (cursor == null)
            throw new Exception();
        cursor.moveToLast();
        return cursorToBlock(cursor);
    }

    private DBBlock cursorToBlock(Cursor cursor) {
        DBBlock block = new DBBlock();
        block.setId(cursor.getLong(0));
        block.setHash(cursor.getString(1));
        block.setHeight(cursor.getInt(2));
        block.setHeader(cursor.getString(3));
        block.setChainWork(new BigInteger(cursor.getString(4)));
        return block;
    }
}