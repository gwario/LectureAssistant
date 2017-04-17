package at.ameise.lectureassistant.content.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import at.ameise.lectureassistant.R;
import at.ameise.lectureassistant.content.model.TimelineEntry;

/**
 * {@link android.support.v7.widget.RecyclerView.Adapter} for {@link at.ameise.lectureassistant.content.model.TimelineEntry}s.
 *
 * Created by mariogastegger on 17.04.17.
 */
public class TimelineEntryAdapter extends RecyclerView.Adapter<TimelineEntryAdapter.ResultViewHolder> {

    private List<TimelineEntry> data;

    public TimelineEntryAdapter(List<TimelineEntry> data) {
        this.data = data;
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_result, parent, false);
        ResultViewHolder vh = new ResultViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        holder.setResult(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public ResultViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.card_result_result);
        }

        public void setResult(TimelineEntry result) {
            textView.setText(result.getText());
        }
    }
}