package at.ameise.lectureassistant.content.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.ameise.lectureassistant.R;
import at.ameise.lectureassistant.content.model.TimelineEntry;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link android.support.v7.widget.RecyclerView.Adapter} for {@link at.ameise.lectureassistant.content.model.TimelineEntry}s.
 *
 * Created by mariogastegger on 17.04.17.
 */
public class TimelineEntryAdapter extends RecyclerView.Adapter<TimelineEntryAdapter.ResultViewHolder> {

    private List<TimelineEntry> data;

    public TimelineEntryAdapter() {
        this.data = new ArrayList<>();
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_result, parent, false);
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

    public void setData(List<TimelineEntry> timelineEntries) {
        data.clear();
        data.addAll(timelineEntries);
        notifyDataSetChanged();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_result_result)
        TextView textView;

        public ResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setResult(TimelineEntry result) {
            textView.setText(result.getText());
        }
    }
}