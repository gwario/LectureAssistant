package at.ameise.lectureassistant.content.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import at.ameise.lectureassistant.content.database.LectureAssistantDatabase;
import at.ameise.lectureassistant.content.database.TimelineEntryDao;
import at.ameise.lectureassistant.content.model.TimelineEntry;

/**
 * This is the view model for the {@link at.ameise.lectureassistant.activity.TimelineActivity}
 *
 * Created by mariogastegger on 20.05.17.
 */
public class TimelineViewModel extends ViewModel {

    private MutableLiveData<List<TimelineEntry>> timelineEntries;

    public LiveData<List<TimelineEntry>> getTimelineEntries(Context context) {

        if (timelineEntries == null) {
            timelineEntries = new MutableLiveData<List<TimelineEntry>>();
            loadTimelineEntries(context.getApplicationContext());
        }
        return timelineEntries;
    }

    private void loadTimelineEntries(Context context) {

        //TODO this method returns a new instance of the database on every call so we should use a singleton pattern here...
        Log.wtf("experiment", "db instance: " + Room.databaseBuilder(context, LectureAssistantDatabase.class, "lectureassistant")
                .build().hashCode());
        Log.wtf("experiment", "db instance: " + Room.databaseBuilder(context, LectureAssistantDatabase.class, "lectureassistant")
                .build().hashCode());

        new AsyncTask<Context, Void, List<TimelineEntry>>() {

            @Override
            protected List<TimelineEntry> doInBackground(Context... contexts) {

                return Room.databaseBuilder(context, LectureAssistantDatabase.class, "lectureassistant")
                        .build()
                        .timelineEntryDao()
                        .load();
            }

            @Override
            protected void onPostExecute(List<TimelineEntry> entries) {
                super.onPostExecute(entries);

                timelineEntries.setValue(entries);
            }
        }.execute(context.getApplicationContext());
    }

    public void storeTimelineEntries(Context context, TimelineEntry... entry) {

        if (timelineEntries == null) {
            timelineEntries = new MutableLiveData<List<TimelineEntry>>();
        }

        new AsyncTask<Context, Void, List<TimelineEntry>>() {

            @Override
            protected List<TimelineEntry> doInBackground(Context... contexts) {

                TimelineEntryDao dao = Room.databaseBuilder(context.getApplicationContext(), LectureAssistantDatabase.class, "lectureassistant")
                        .build()
                        .timelineEntryDao();

                dao.insert(entry);

                return dao.load();
            }

            @Override
            protected void onPostExecute(List<TimelineEntry> entries) {
                super.onPostExecute(entries);

                timelineEntries.setValue(entries);
            }
        }.execute(context.getApplicationContext());
    }

    private void insertTimelineEntries(Context context, TimelineEntry... entry) {

        Room.databaseBuilder(context.getApplicationContext(), LectureAssistantDatabase.class, "lectureassistant")
                .build()
                .timelineEntryDao().insert(entry);
    }
}
