package ru.vsu.cs.weatherforecast;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ForecastListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] datesData;
    private final String[] temperatureData;
    private final String[] cloudsData;
    private final String[] windSpeedData;
    private final String[] descriptionData;

    public ForecastListAdapter(Activity context, String[] datesData, String[] temperatureData, String[] cloudsData, String[] windSpeedData, String[] descriptionData) {
        super(context, R.layout.forecast_list_item, datesData);
        this.context = context;
        this.datesData = datesData;
        this.temperatureData = temperatureData;
        this.cloudsData = cloudsData;
        this.windSpeedData = windSpeedData;
        this.descriptionData = descriptionData;
    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.forecast_list_item, null,true);

        TextView tvDatesData = (TextView) rowView.findViewById(R.id.tvDateItem);
        TextView tvTemperatureData = (TextView) rowView.findViewById(R.id.tvTemperatureItem);
        TextView tvCloudsData = (TextView) rowView.findViewById(R.id.tvCloudsItem);
        TextView tvWindSpeedData = (TextView) rowView.findViewById(R.id.tvWindSpeedItem);
        TextView tvDescriptionData = (TextView) rowView.findViewById(R.id.tvDescriptionItem);

        tvDatesData.setText(datesData[position]);
        tvTemperatureData.setText(temperatureData[position]);
        tvCloudsData.setText(cloudsData[position]);
        tvWindSpeedData.setText(windSpeedData[position]);
        tvDescriptionData.setText(descriptionData[position]);

        return rowView;
    }
}
