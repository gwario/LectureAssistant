package at.ameise.lectureassistant.content.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import at.ameise.lectureassistant.content.model.TimelineEntry;

/**
 * Provides access to the timeline table.
 *
 * Created by mariogastegger on 17.04.17.
 */
@Dao
public interface TimelineEntryDao {

    /**
     * Inserts entites in the database.
     * @param entity    the entity.
     */
    @Insert
    void insert(TimelineEntry... entity);

    /**
     * Loads all timeline entries.
     * @return  All timeline entries.
     */
    @Query("SELECT * FROM timeline_entries")
    List<TimelineEntry> load();
}
