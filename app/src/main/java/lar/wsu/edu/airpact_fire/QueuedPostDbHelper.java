package lar.wsu.edu.airpact_fire;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Luke on 3/23/2016.
 */
public class QueuedPostDbHelper extends SQLiteOpenHelper {
    /* SQL Contract Stuff */
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + QueuedPostContract.PostEntry.TABLE_NAME + " (" +
                    QueuedPostContract.PostEntry._ID + " INTEGER PRIMARY KEY," +
                    QueuedPostContract.PostEntry.COLUMN_NAME_IMAGE + TEXT_TYPE + COMMA_SEP +
                    QueuedPostContract.PostEntry.COLUMN_NAME_DISTANCE + TEXT_TYPE + COMMA_SEP +
                    QueuedPostContract.PostEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + QueuedPostContract.PostEntry.TABLE_NAME;
    /* /// */

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "QueuedPost.db";

    public QueuedPostDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
