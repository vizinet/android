package lar.wsu.edu.airpact_fire;

import android.provider.BaseColumns;

/* SQL Database Contract
 *  - Determines contents of database
 *  - Holds queued posts
 */
public final class QueuedPostContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public QueuedPostContract() {}

    /* Inner class that defines the table contents */
    public static abstract class PostEntry implements BaseColumns {
        public static final String TABLE_NAME = "post";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_TIME = "time";
    }
}
