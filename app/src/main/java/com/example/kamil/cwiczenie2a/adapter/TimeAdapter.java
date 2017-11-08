package com.example.kamil.cwiczenie2a.adapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kamil.cwiczenie2a.R;
import com.example.kamil.cwiczenie2a.interfaces.ISharedPreferences;
import com.example.kamil.cwiczenie2a.models.LapTime;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by Kamil on 30.10.2017.
 */

public class TimeAdapter extends ArrayAdapter<LapTime> implements ISharedPreferences {
    private static class ViewHolder{
        private TextView PositionTextView;
        private TextView LapTimeTextView;
        private TextView LapTimeDifference;
        private TextView TotalTimeTextView;
        public ViewHolder(){
            // nothing to do here
        }
    }
    private final LayoutInflater inflater;
    private int sizeLimit;
    private boolean showTimeDifference;
    private final LinkedList<LapTime> items;
    public TimeAdapter(Context context, LinkedList<LapTime> times, int sizeLimit, boolean showTimeDifference) {
        super(context, 0, times);
        this.inflater = LayoutInflater.from(context);
        this.items = times;
        this.sizeLimit = sizeLimit;
        this.showTimeDifference = showTimeDifference;
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
    public void setShowTimeDifference(boolean val){
        showTimeDifference = val;
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
            holder.LapTimeDifference = (TextView)itemView.findViewById(R.id.LapTimeDifference);
            itemView.setTag(holder);
        } else {
            holder = (ViewHolder)itemView.getTag();
        }
        holder.PositionTextView.setText(String.format(Locale.UK,"%02d.", position+1));
        holder.LapTimeTextView.setText(item.getLapTime());
        holder.TotalTimeTextView.setText(item.getTotalTime());
        holder.LapTimeDifference.setText(item.getLapTimeDifference());
        if(!showTimeDifference){
            holder.LapTimeDifference.setVisibility(View.GONE);
        } else{
            holder.LapTimeDifference.setVisibility(View.VISIBLE);
        }

        return itemView;
    }
    @Override
    public void onSaveSharedPreferences(SharedPreferences.Editor editor) {
        editor.putInt("TimeAdapterCount",getCount());
        for(int i = 0; i < getCount(); i++){
            editor.putString("TimesTotalTime"+i, getItem(i).getTotalTime());
            editor.putString("TimesLapTime"+i, getItem(i).getLapTime());
            editor.putString("LapTimeDifference"+i,getItem(i).getLapTimeDifference());
        }
    }

    @Override
    public void onRestoreSharedPreferences(SharedPreferences prefs) {
        LapTime tmp;
        String totalTime;
        String lapTime;
        String lapTimeDifference;
        for (int i =0; i < prefs.getInt("TimeAdapterCount",0);i++){
            totalTime = prefs.getString("TimesTotalTime"+i,"");
            lapTime = prefs.getString("TimesLapTime"+i,"");
            lapTimeDifference = prefs.getString("LapTimeDifference"+i,"");
            tmp = new LapTime(totalTime,lapTime,lapTimeDifference);
            add(tmp);
        }
        notifyDataSetChanged();
    }
}

