package ru.vsu.cs.weatherforecast;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.vsu.cs.weatherforecast.listener.OnItemListener;

public class ForecastListAdapter extends RecyclerView.Adapter<ForecastListAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<ForecastListItem> list;
    private final OnItemListener onItemListener;

    public ForecastListAdapter(@NonNull Activity context, List<ForecastListItem> list, OnItemListener onItemListener) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.forecast_list_item, parent, false);
        return new ViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForecastListItem item = list.get(position);
        holder.tvTemperature.setText(item.getTemperature());
        holder.tvDescription.setText(item.getDescription());
        holder.tvDate.setText(item.getDate());
        holder.ivWeatherImage.setImageDrawable(item.getWeatherImage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        final TextView tvDescription;
        final TextView tvTemperature;
        final TextView tvDate;
        final ImageView ivWeatherImage;
        OnItemListener onItemListener;

        public ViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            this.tvDescription = itemView.findViewById(R.id.weatherDescriptionItem);
            this.tvTemperature = itemView.findViewById(R.id.temperatureItem);
            this.tvDate = itemView.findViewById(R.id.weekdayItem);
            this.ivWeatherImage = itemView.findViewById(R.id.weatherImageItem);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }
}
