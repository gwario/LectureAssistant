package at.ameise.lectureassistant.content.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import at.ameise.lectureassistant.content.model.TimelineEntry;

/**
 * Provides access to the timeline table.
 *
 * Created by mariogastegger on 17.04.17.
 */
public final class TimelineEntryDAO {

    /**
     * Inserts the entity in the database.
     * @param db        the database.
     * @param entity    the entity.
     * @return  the entity with {@link TimelineEntry#getId()} set.
     */
    public static TimelineEntry insert(SQLiteDatabase db, TimelineEntry entity) {

        long newRowId = db.insert(TimelineSchema.TimelineEntry.TABLE_NAME, null, TimelineSchema.fromEntity(entity));

        entity.setId(newRowId);

        return entity;
    }

    /**
     * Loads all timeline entries.
     * @param db    the database.
     * @return  All timeline entries.
     */
    public static List<TimelineEntry> load(SQLiteDatabase db) {

        List items = new ArrayList<>();

        Cursor cursor = db.query(TimelineSchema.TimelineEntry.TABLE_NAME, null, null, null, null, null, null);

        while(cursor.moveToNext()) {

            items.add(TimelineSchema.fromCursor(cursor));
        }

        cursor.close();

        return items;
    }
}
