package ru.vsu.cs.weatherforecast;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ForecastListAdapter extends RecyclerView.Adapter<ForecastListAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<ForecastListItem> list;

    public ForecastListAdapter(@NonNull Activity context, List<ForecastListItem> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.forecast_list_item, parent, false);
        return new ViewHolder(view);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvDescription;
        final TextView tvTemperature;
        final TextView tvDate;
        final ImageView ivWeatherImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvDescription = (TextView) itemView.findViewById(R.id.weatherDescriptionItem);
            this.tvTemperature = (TextView) itemView.findViewById(R.id.temperatureItem);
            this.tvDate = (TextView) itemView.findViewById(R.id.weekdayItem);
            this.ivWeatherImage = (ImageView) itemView.findViewById(R.id.weatherImageItem);
        }
    }
}
