package at.ameise.lectureassistant.content.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Contains the schema of the timeline table.
 *
 * Created by mariogastegger on 17.04.17.
 */
final class TimelineSchema {

    private TimelineSchema() {}

    public static class TimelineEntry implements BaseColumns {
        public static final String TABLE_NAME = "timeline";
        public static final String COLUMN_NAME_TEXT = "text";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TimelineEntry.TABLE_NAME + " (" +
                    TimelineEntry._ID + " INTEGER PRIMARY KEY," +
                    TimelineEntry.COLUMN_NAME_TEXT + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TimelineEntry.TABLE_NAME;


    static void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    static void onDrop(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
    }

    /**
     * @param entity    the entity.
     * @return the content values.
     */
    static ContentValues fromEntity(at.ameise.lectureassistant.content.model.TimelineEntry entity) {

        ContentValues values = new ContentValues();

        values.put(TimelineEntry.COLUMN_NAME_TEXT, entity.getText());

        return values;
    }

    static at.ameise.lectureassistant.content.model.TimelineEntry fromCursor(Cursor cursor) {

        final at.ameise.lectureassistant.content.model.TimelineEntry entry = new at.ameise.lectureassistant.content.model.TimelineEntry();

        entry.setId(cursor.getLong(cursor.getColumnIndexOrThrow(TimelineEntry._ID)));
        entry.setText(cursor.getString(cursor.getColumnIndexOrThrow(TimelineEntry.COLUMN_NAME_TEXT)));

        return entry;
    }
}
