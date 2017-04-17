package at.ameise.lectureassistant.content.model;

/**
 * Pojo for timeline entries.
 *
 * Created by mariogastegger on 17.04.17.
 */
public class TimelineEntry {

    private long id;
    private String text;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
