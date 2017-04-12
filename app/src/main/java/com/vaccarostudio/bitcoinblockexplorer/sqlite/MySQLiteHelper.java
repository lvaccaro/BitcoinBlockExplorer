package com.vaccarostudio.bitcoinblockexplorer.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database name
    private static final String DATABASE_NAME = "blockstore.db";
    // Database version
    private static final int DATABASE_VERSION = 3;
    // Table name
    public static final String TABLE_BLOCKSTORE = "blockstore";
    // Columns name
    public static final String COLUMN_ID        = "_id";
    public static final String COLUMN_HEIGHT    = "height";
    public static final String COLUMN_HASH      = "hash";
    public static final String COLUMN_HEADER    = "header";
    public static final String COLUMN_CHAINWORK = "chainwork";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_BLOCKSTORE + "( " + COLUMN_ID
            + " integer primary key autoincrement, " +
            COLUMN_HEIGHT + " integer not null, " +
            COLUMN_HASH + " text not null, " +
            COLUMN_HEADER + " text not null, " +
            COLUMN_CHAINWORK + " text not null );";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOCKSTORE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}