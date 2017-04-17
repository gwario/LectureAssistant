package at.ameise.lectureassistant.content.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * {@link SQLiteOpenHelper} for the lecture assistant database.
 *
 * Created by mariogastegger on 17.04.17.
 */
public class LectureAssistantDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "lectureassistant.db";

    public LectureAssistantDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TimelineSchema.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TimelineSchema.onDrop(db);
        TimelineSchema.onCreate(db);
    }
}
