package com.example.kamil.timer.adapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kamil.timer.R;
import com.example.kamil.timer.interfaces.ISharedPreferences;
import com.example.kamil.timer.models.LapTime;

import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by Kamil on 30.10.2017.
 */

public class TimeAdapter extends ArrayAdapter<LapTime> implements ISharedPreferences {
    public static final String TimeAdapterCountName = "TimeAdapterCount";
    private static class ViewHolder{
        private TextView PositionTextView;
        private TextView LapTimeTextView;
        private TextView IntermediateTime;
        private TextView TotalTimeTextView;
        public ViewHolder(){
            // nothing to do here
        }
    }
    private final LayoutInflater inflater;
    private int sizeLimit;
    private boolean showIntermediateTimes;
    private final LinkedList<LapTime> items;
    public TimeAdapter(Context context, LinkedList<LapTime> times, int sizeLimit, boolean showIntermediateTimes) {
        super(context, 0, times);
        this.inflater = LayoutInflater.from(context);
        this.items = times;
        this.sizeLimit = sizeLimit;
        this.showIntermediateTimes = showIntermediateTimes;
    }

    @Override
    public void add(@Nullable LapTime object) {
        if(items.size() >= sizeLimit) {
            items.removeFirst();
        }
        super.add(object);
    }
    public void setSizeLimit(int size){
        sizeLimit = size;
        while(items.size() > sizeLimit){
            items.removeFirst();
        }
    }
    public void setShowIntermediateTimes(boolean val){
        showIntermediateTimes = val;
    }
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View itemView = convertView;
        ViewHolder holder = null;
        final LapTime item = getItem(position);

        if(null == itemView) {
            itemView = this.inflater.inflate(R.layout.laptimeelement, parent, false);

            holder = new ViewHolder();

            holder.PositionTextView = (TextView)itemView.findViewById(R.id.PositionTextView);
            holder.LapTimeTextView = (TextView)itemView.findViewById(R.id.LapTimeTextView);
            holder.TotalTimeTextView = (TextView)itemView.findViewById(R.id.TotalTimeTextView);
            holder.IntermediateTime = (TextView)itemView.findViewById(R.id.IntermediateTimeTextView);
            itemView.setTag(holder);
        } else {
            holder = (ViewHolder)itemView.getTag();
        }
        holder.PositionTextView.setText(String.format(Locale.UK,"%02d.", position+1));
        holder.LapTimeTextView.setText(item.getLapTime());
        holder.TotalTimeTextView.setText(item.getTotalTime());
        holder.IntermediateTime.setText(item.getIntermediateTime());

        SetVisibilityOfIntermediateTime(holder);
        return itemView;
    }

    private void SetVisibilityOfIntermediateTime(ViewHolder holder) {
        if(!showIntermediateTimes){
            holder.IntermediateTime.setVisibility(View.GONE);
        } else{
            holder.IntermediateTime.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveSharedPreferences(SharedPreferences.Editor editor) {
        editor.putInt(TimeAdapterCountName,getCount());
        for(int i = 0, j = getCount(); i < j; i++){
            editor.putString(LapTime.TotalTimeName+i, getItem(i).getTotalTime());
            editor.putString(LapTime.LapTimeName+i, getItem(i).getLapTime());
            editor.putString(LapTime.IntermediateTimeName+i,getItem(i).getIntermediateTime());
        }
    }

    @Override
    public void onRestoreSharedPreferences(SharedPreferences prefs) {
        String totalTime;
        String lapTime;
        String intermediateTime;
        for (int i = 0, j = prefs.getInt(TimeAdapterCountName,0); i < j ; i++){
            totalTime = prefs.getString(LapTime.TotalTimeName+i,"");
            lapTime = prefs.getString(LapTime.LapTimeName+i,"");
            intermediateTime = prefs.getString(LapTime.IntermediateTimeName+i,"");
            add(new LapTime(totalTime,lapTime,intermediateTime));
        }
        notifyDataSetChanged();
    }
}

