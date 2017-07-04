package edu.wsu.lar.airpact_fire.data.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import edu.wsu.lar.airpact_fire.data.Post;

/**
 * Handles local posts, from queued posts to successfully submitted posts
 **/
/* Uses SQL to store persistent data */
/* SOURCE: http://developer.android.com/training/basics/data-storage/databases.html */
public class PostDataManager {

    // Columns:
    //      postFlag, user, image, secretKey, description,
    //      highColor, highX, highY,
    //      lowColor, lowX, lowY,
    //      estimatedVisualRange, geoX, geoY, tags, date

    // NOTE: Get database in an AsyncTask because getting it could be long-running

    // Creates new post entry in SQL table
    public static long addPost(Context context, Post post) {
        // If post exists, update the IsPosted field in SQL to that of the parameter
        long previousPostId = doesPostExist(context, post);
        if (previousPostId >= 0) {
            //Toast.makeText(Post.Activity, "Post is being flagged as " + post.IsPosted + ".", Toast.LENGTH_SHORT).show();
            flagPost(context, post.SqlId, post.IsPosted);

            return previousPostId;
        }

        // If this is reached, we have a new post create
        int postNum = getNumPosts(context, post.User) + 1;

        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PostContract.PostEntry.COLUMN_NAME_POST_FLAG, post.IsPosted);
        values.put(PostContract.PostEntry.COLUMN_NAME_USER, post.User);
        values.put(PostContract.PostEntry.COLUMN_NAME_IMAGE, post.Image);
        values.put(PostContract.PostEntry.COLUMN_NAME_SECRET_KEY, post.getSecretKey());
        values.put(PostContract.PostEntry.COLUMN_NAME_DESCRIPTION, post.Description);
        values.put(PostContract.PostEntry.COLUMN_NAME_HIGH_COLOR, post.HighColor);
        values.put(PostContract.PostEntry.COLUMN_NAME_HIGH_X, post.HighX);
        values.put(PostContract.PostEntry.COLUMN_NAME_HIGH_Y, post.HighY);
        values.put(PostContract.PostEntry.COLUMN_NAME_LOW_COLOR, post.LowColor);
        values.put(PostContract.PostEntry.COLUMN_NAME_LOW_X, post.LowX);
        values.put(PostContract.PostEntry.COLUMN_NAME_LOW_Y, post.LowY);
        values.put(PostContract.PostEntry.COLUMN_NAME_VISUAL_RANGE, post.VisualRangeOne);
        //values.put(PostContract.PostEntry.COLUMN_NAME_VISUAL_RANGE, post.VisualRangeTwo);
        values.put(PostContract.PostEntry.COLUMN_NAME_GEO_X, post.GeoX);
        values.put(PostContract.PostEntry.COLUMN_NAME_GEO_Y, post.GeoY);
        values.put(PostContract.PostEntry.COLUMN_NAME_TAGS, post.Tags);
        values.put(PostContract.PostEntry.COLUMN_NAME_DATE, post.Date.getTime().toString());
        values.put(PostContract.PostEntry.COLUMN_NAME_POST_NUM, postNum);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(
                PostContract.PostEntry.TABLE_NAME,
                null,
                values);

        // Not that it matters, but let's set the fields of the post
        post.PostNum = postNum;
        post.SqlId = newRowId;

        return newRowId;
    }

    // Find post with same sqlId and remove it from table
    public static boolean deletePost(Context context, Post post) {
        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = PostContract.PostEntry._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(post.SqlId)};

        db.delete(PostContract.PostEntry.TABLE_NAME, selection, selectionArgs);

        return false;
    }

    // Call next overloaded function, just with *current* user
    public static List<Post> getPosts(Context context) {
        String user = AppDataManager.getRecentUser();
        return getPosts(context, user);
    }

    // Get all posts from user
    public static List<Post> getPosts(Context context, String user) {
        // TODO: See if we shouldn't be creating a new helper each time
        DatabaseHelper mDbHelper = new DatabaseHelper(context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                PostContract.PostEntry._ID,
                PostContract.PostEntry.COLUMN_NAME_POST_FLAG,
                PostContract.PostEntry.COLUMN_NAME_USER,
                PostContract.PostEntry.COLUMN_NAME_IMAGE,
                PostContract.PostEntry.COLUMN_NAME_SECRET_KEY,
                PostContract.PostEntry.COLUMN_NAME_DESCRIPTION,
                PostContract.PostEntry.COLUMN_NAME_HIGH_COLOR,
                PostContract.PostEntry.COLUMN_NAME_HIGH_X,
                PostContract.PostEntry.COLUMN_NAME_HIGH_Y,
                PostContract.PostEntry.COLUMN_NAME_LOW_COLOR,
                PostContract.PostEntry.COLUMN_NAME_LOW_X,
                PostContract.PostEntry.COLUMN_NAME_LOW_Y,
                PostContract.PostEntry.COLUMN_NAME_VISUAL_RANGE,
                PostContract.PostEntry.COLUMN_NAME_GEO_X,
                PostContract.PostEntry.COLUMN_NAME_GEO_Y,
                PostContract.PostEntry.COLUMN_NAME_TAGS,
                PostContract.PostEntry.COLUMN_NAME_DATE,
                PostContract.PostEntry.COLUMN_NAME_POST_NUM
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                PostContract.PostEntry.COLUMN_NAME_UPDATED + " DESC";

        // Which row to read, based on the ID
        String selection = PostContract.PostEntry.COLUMN_NAME_USER + " LIKE ?";
        String[] selectionArgs = {user};

        // Get rows
        Cursor c = db.query(
                PostContract.PostEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        // Return on no results
        if (c.getCount() < 1) return new ArrayList<>();

        // Add all posts from this user to array
        List<Post> postList = new ArrayList<>();

        c.moveToFirst();
        do {
            // Get SQL post SubmitFieldVars
            long itemId = c.getLong(c.getColumnIndexOrThrow(PostContract.PostEntry._ID));
            long postNum = Long.parseLong(c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_POST_NUM)));
            String postFlag = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_POST_FLAG));
            String image = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_IMAGE));
            String secretKey = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_SECRET_KEY));
            String description = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_DESCRIPTION));
            String highColor = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_HIGH_COLOR));
            String highX = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_HIGH_X));
            String highY = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_HIGH_Y));
            String lowColor = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_LOW_COLOR));
            String lowX = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_LOW_X));
            String lowY = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_LOW_Y));
            String visualRange = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_VISUAL_RANGE));
            String geoX = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_GEO_X));
            String geoY = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_GEO_Y));
            String tags = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_TAGS));
            // Date is a little tricky
            // Get date string
            String dateString = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_DATE));
            Calendar date = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(Post.DATE_FORMAT, Locale.ENGLISH);
            // Attempt to parse date
            try {
                date.setTime(sdf.parse(dateString));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Create post and add to list
            Post post = new Post(user, description, image, secretKey, highColor, highX, highY, lowColor, lowX, lowY,
                    visualRange, geoX, geoY, tags, postFlag, itemId, date, postNum);
            postList.add(post);

        } while (c.moveToNext());

        // Lend list
        return postList;
    }

    // Get a post given the id (no need for user)
    public static Post getPost(Context context, long postId) {
        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                PostContract.PostEntry._ID,
                PostContract.PostEntry.COLUMN_NAME_POST_FLAG,
                PostContract.PostEntry.COLUMN_NAME_USER,
                PostContract.PostEntry.COLUMN_NAME_IMAGE,
                PostContract.PostEntry.COLUMN_NAME_SECRET_KEY,
                PostContract.PostEntry.COLUMN_NAME_DESCRIPTION,
                PostContract.PostEntry.COLUMN_NAME_HIGH_COLOR,
                PostContract.PostEntry.COLUMN_NAME_HIGH_X,
                PostContract.PostEntry.COLUMN_NAME_HIGH_Y,
                PostContract.PostEntry.COLUMN_NAME_LOW_COLOR,
                PostContract.PostEntry.COLUMN_NAME_LOW_X,
                PostContract.PostEntry.COLUMN_NAME_LOW_Y,
                PostContract.PostEntry.COLUMN_NAME_VISUAL_RANGE,
                PostContract.PostEntry.COLUMN_NAME_GEO_X,
                PostContract.PostEntry.COLUMN_NAME_GEO_Y,
                PostContract.PostEntry.COLUMN_NAME_TAGS,
                PostContract.PostEntry.COLUMN_NAME_DATE,
                PostContract.PostEntry.COLUMN_NAME_POST_NUM
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                PostContract.PostEntry.COLUMN_NAME_UPDATED + " DESC";

        // Which row to read, based on the ID
        String selection = PostContract.PostEntry._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(postId)};

        // Get rows
        Cursor c = db.query(
                PostContract.PostEntry.TABLE_NAME,        // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        // Return on no results
        if (c.getCount() < 1) return null;

        // Get post with cursor
        c.moveToFirst();

        // Read post SubmitFieldVars
        long itemId = c.getLong(c.getColumnIndexOrThrow(PostContract.PostEntry._ID));
        long postNum = Long.parseLong(c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_POST_NUM)));
        String user = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_USER));
        String postFlag = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_POST_FLAG));
        String image = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_IMAGE));
        String secretKey = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_SECRET_KEY));
        String description = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_DESCRIPTION));
        String highColor = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_HIGH_COLOR));
        String highX = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_HIGH_X));
        String highY = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_HIGH_Y));
        String lowColor = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_LOW_COLOR));
        String lowX = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_LOW_X));
        String lowY = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_LOW_Y));
        String visualRange = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_VISUAL_RANGE));
        String geoX = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_GEO_X));
        String geoY = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_GEO_Y));
        String tags = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_TAGS));
        // Date
        String dateString = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_DATE));
        Calendar date = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(Post.DATE_FORMAT, Locale.ENGLISH);
        try {
            date.setTime(sdf.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Create post from SubmitFieldVars
        Post post = new Post(user, description, image, secretKey, highColor, highX, highY, lowColor, lowX, lowY,
                visualRange, geoX, geoY, tags, postFlag, itemId, date, postNum);

        // Give post
        return post;
    }

    public static void populatePost(Context context, long postId, Post post) {
        DatabaseHelper mDbHelper = new DatabaseHelper(context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        // TODO: Make projection a static array in this class
        String[] projection = {
                PostContract.PostEntry._ID,
                PostContract.PostEntry.COLUMN_NAME_POST_FLAG,
                PostContract.PostEntry.COLUMN_NAME_USER,
                PostContract.PostEntry.COLUMN_NAME_IMAGE,
                PostContract.PostEntry.COLUMN_NAME_SECRET_KEY,
                PostContract.PostEntry.COLUMN_NAME_DESCRIPTION,
                PostContract.PostEntry.COLUMN_NAME_HIGH_COLOR,
                PostContract.PostEntry.COLUMN_NAME_HIGH_X,
                PostContract.PostEntry.COLUMN_NAME_HIGH_Y,
                PostContract.PostEntry.COLUMN_NAME_LOW_COLOR,
                PostContract.PostEntry.COLUMN_NAME_LOW_X,
                PostContract.PostEntry.COLUMN_NAME_LOW_Y,
                PostContract.PostEntry.COLUMN_NAME_VISUAL_RANGE,
                PostContract.PostEntry.COLUMN_NAME_GEO_X,
                PostContract.PostEntry.COLUMN_NAME_GEO_Y,
                PostContract.PostEntry.COLUMN_NAME_TAGS,
                PostContract.PostEntry.COLUMN_NAME_DATE,
                PostContract.PostEntry.COLUMN_NAME_POST_NUM
        };

        // TODO: ** Don't repeat yourself **

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                PostContract.PostEntry.COLUMN_NAME_UPDATED + " DESC";

        // Which row to read, based on the ID
        String selection = PostContract.PostEntry._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(postId)};

        // Get rows
        Cursor c = db.query(
                PostContract.PostEntry.TABLE_NAME,        // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        // Return on no results
        // TODO: Probably should throw exception
        if (c.getCount() < 1) return;

        // Get post with cursor
        c.moveToFirst();

        // Set post fields
        post.SqlId = c.getLong(c.getColumnIndexOrThrow(PostContract.PostEntry._ID));
        post.PostNum = Long.parseLong(
                c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_POST_NUM)));
        post.User = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_USER));
        post.IsPosted = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_POST_FLAG));
        post.Image = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_IMAGE));
        post.setSecretKey(c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_SECRET_KEY)));
        post.Description = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_DESCRIPTION));
        post.HighColor = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_HIGH_COLOR));
        post.HighX = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_HIGH_X));
        post.HighY = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_HIGH_Y));
        post.LowColor = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_LOW_COLOR));
        post.LowX = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_LOW_X));
        post.LowY = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_LOW_Y));
        post.VisualRangeOne = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_VISUAL_RANGE));
        // TODO
        post.VisualRangeTwo = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_VISUAL_RANGE));
        post.GeoX = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_GEO_X));
        post.GeoY = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_GEO_Y));
        post.Tags = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_TAGS));
        // Date
        String dateString = c.getString(c.getColumnIndexOrThrow(PostContract.PostEntry.COLUMN_NAME_DATE));
        Calendar date = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(Post.DATE_FORMAT, Locale.ENGLISH);
        try {
            date.setTime(sdf.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        post.Date = date;

        // Fin!
    }

    // Set given post as posted or queued
    public static void flagPost(Context context, long sqlId, String flag) {
        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(PostContract.PostEntry.COLUMN_NAME_POST_FLAG, flag);

        // Which row to update, based on the ID
        String selection = PostContract.PostEntry._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(sqlId)};

        int count = db.update(
                PostContract.PostEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    // Checks for exactly equal post dates; sees if that post already exists
    public static long doesPostExist(Context context, Post post) {
        DatabaseHelper mDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                PostContract.PostEntry._ID,
                PostContract.PostEntry.COLUMN_NAME_DATE
        };

        // Have Cursor sort by ID (which doesn't really matter)
        String sortOrder =
                PostContract.PostEntry.COLUMN_NAME_UPDATED + " DESC";

        // Read rows with exactly the same date
        String selection = PostContract.PostEntry.COLUMN_NAME_DATE + " LIKE ?";
        String[] selectionArgs = {post.Date.getTime().toString()}; // Store string version of date

        // Get rows
        Cursor c = db.query(
                PostContract.PostEntry.TABLE_NAME,        // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        // If no results, post doesn't exist
        if (c.getCount() < 1) return -1;
        // Else, post does exist; return post id
        c.moveToFirst();
        return c.getLong(c.getColumnIndexOrThrow(PostContract.PostEntry._ID));
    }

    public static String getTableAsString(SQLiteDatabase db, String tableName) {
        Log.d("PostDataManager", "getTableAsString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }

    // NOTE: This is not efficient with respect to calling getNumQueued after
    public static int getNumPosts(Context context, String user) {
        List<Post> posts = getPosts(context, user);
        return posts.size();
    }

    public static int getNumSubmitted(Context context, String user) {
        List<Post> posts = getPosts(context, user);
        int numPosted = 0;
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).IsPosted.equals("true")) numPosted++;
        }
        return numPosted;
    }

    public static int getNumQueued(Context context, String user) {
        List<Post> posts = getPosts(context, user);
        int numQueued = 0;
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).IsPosted.equals("false")) numQueued++;
        }
        return numQueued;
    }

    public static Calendar getLastPostTime(Context context, String user) {
        List<Post> posts = getPosts(context, user);
        // Return date of last post
        return posts.get(posts.size() - 1).Date;
    }

    // Defines SQL scheme
    public static final class PostContract {

        // SQL statements
        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + PostEntry.TABLE_NAME + " (" +
                        PostEntry._ID + " INTEGER PRIMARY KEY," +
                        PostEntry.COLUMN_NAME_POST_FLAG + TEXT_TYPE + COMMA_SEP +
                        PostEntry.COLUMN_NAME_USER + TEXT_TYPE + COMMA_SEP +
                        PostEntry.COLUMN_NAME_IMAGE + TEXT_TYPE + COMMA_SEP +
                        PostEntry.COLUMN_NAME_SECRET_KEY + TEXT_TYPE + COMMA_SEP +
                        PostEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                        PostEntry.COLUMN_NAME_HIGH_COLOR + TEXT_TYPE + COMMA_SEP +
                        PostEntry.COLUMN_NAME_HIGH_X + TEXT_TYPE + COMMA_SEP +
                        PostEntry.COLUMN_NAME_HIGH_Y + TEXT_TYPE + COMMA_SEP +
                        PostEntry.COLUMN_NAME_LOW_COLOR + TEXT_TYPE + COMMA_SEP +
                        PostEntry.COLUMN_NAME_LOW_X + TEXT_TYPE + COMMA_SEP +
                        PostEntry.COLUMN_NAME_LOW_Y + TEXT_TYPE + COMMA_SEP +
                        PostEntry.COLUMN_NAME_VISUAL_RANGE + TEXT_TYPE + COMMA_SEP +
                        PostEntry.COLUMN_NAME_GEO_X + TEXT_TYPE + COMMA_SEP +
                        PostEntry.COLUMN_NAME_GEO_Y + TEXT_TYPE + COMMA_SEP +
                        PostEntry.COLUMN_NAME_TAGS + TEXT_TYPE + COMMA_SEP +
                        PostEntry.COLUMN_NAME_POST_NUM + TEXT_TYPE + COMMA_SEP +
                        PostEntry.COLUMN_NAME_DATE + TEXT_TYPE +
                        " )";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + PostEntry.TABLE_NAME;

        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.
        public PostContract() {
        }

        /* Inner class that defines the table contents */
        public abstract class PostEntry implements BaseColumns {
            public static final String TABLE_NAME = "posts";
            // Data columns
            public static final String COLUMN_NAME_POST_FLAG = "postFlag";
            public static final String COLUMN_NAME_USER = "user";
            public static final String COLUMN_NAME_IMAGE = "image";
            public static final String COLUMN_NAME_SECRET_KEY = "secretKey";
            public static final String COLUMN_NAME_DESCRIPTION = "description";
            public static final String COLUMN_NAME_HIGH_COLOR = "highColor";
            public static final String COLUMN_NAME_HIGH_X = "highX";
            public static final String COLUMN_NAME_HIGH_Y = "highY";
            public static final String COLUMN_NAME_LOW_COLOR = "lowColor";
            public static final String COLUMN_NAME_LOW_X = "lowX";
            public static final String COLUMN_NAME_LOW_Y = "lowY";
            public static final String COLUMN_NAME_VISUAL_RANGE = "estimatedVisualRange";
            public static final String COLUMN_NAME_GEO_X = "geoX";
            public static final String COLUMN_NAME_GEO_Y = "geoY";
            public static final String COLUMN_NAME_TAGS = "tags";
            public static final String COLUMN_NAME_DATE = "date";
            public static final String COLUMN_NAME_POST_NUM = "postNum";

            // Column to sort by
            public static final String COLUMN_NAME_UPDATED = COLUMN_NAME_USER;
        }
    }

    // Helps manage SQL database
    public static class DatabaseHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(PostContract.SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(PostContract.SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
