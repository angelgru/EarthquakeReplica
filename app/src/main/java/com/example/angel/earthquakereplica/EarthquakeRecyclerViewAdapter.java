package com.example.angel.earthquakereplica;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.angel.earthquakereplica.databinding.ListItemEarthquakeBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EarthquakeRecyclerViewAdapter
        extends RecyclerView.Adapter<EarthquakeRecyclerViewAdapter.ViewHolder>{

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);
    private static final NumberFormat MAGNITUDE_FORMAT =
            new DecimalFormat("0.0");
    private List<Earthquake> mEarthquakes;

    public EarthquakeRecyclerViewAdapter(List<Earthquake> mEarthquakes) {
        this.mEarthquakes = mEarthquakes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListItemEarthquakeBinding binding = ListItemEarthquakeBinding.inflate(
                LayoutInflater.from(viewGroup.getContext()),
                viewGroup,
                false
        );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Earthquake earthquake = mEarthquakes.get(i);
        viewHolder.binding.setEarthquake(earthquake);
        viewHolder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mEarthquakes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ListItemEarthquakeBinding binding;

        public ViewHolder(ListItemEarthquakeBinding binding) {
            super(binding.getRoot());
            this.binding  = binding;
            binding.setMagnitudeformat(MAGNITUDE_FORMAT);
            binding.setTimeformat(TIME_FORMAT);
        }
    }
}
