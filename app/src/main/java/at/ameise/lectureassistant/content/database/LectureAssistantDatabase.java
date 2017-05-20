package at.ameise.lectureassistant.content.database;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import at.ameise.lectureassistant.content.model.TimelineEntry;

/**
 * {@link SQLiteOpenHelper} for the lecture assistant database.
 *
 * Created by mariogastegger on 17.04.17.
 */
@Database(entities = {TimelineEntry.class}, version = 2)
public abstract class LectureAssistantDatabase extends RoomDatabase {

    //public static final int DATABASE_VERSION = 1;

    //public static final String DATABASE_NAME = "lectureassistant.db";

    public abstract TimelineEntryDao timelineEntryDao();

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration databaseConfiguration) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}
