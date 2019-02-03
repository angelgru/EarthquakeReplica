package com.example.angel.earthquakereplica;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EarthquakeListFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private List<Earthquake> mEarthquakes = new ArrayList<>();
    private RecyclerView recyclerView;
    private EarthquakeRecyclerViewAdapter recyclerViewAdapter;
    EarthquakeViewModel earthquakeViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int mMagnitude = 0;

    public EarthquakeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        earthquakeViewModel = ViewModelProviders.of(this).get(EarthquakeViewModel.class);
        earthquakeViewModel.getEarthquakes().observe(this, new Observer<List<Earthquake>>() {
            @Override
            public void onChanged(@Nullable List<Earthquake> earthquakes) {
                setEarthquakes(earthquakes);
            }
        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public void setEarthquakes(List<Earthquake> earthquakes) {
        updateFromPreferences();

        mEarthquakes.clear();
        recyclerViewAdapter.notifyDataSetChanged();
        for (Earthquake earthquake: earthquakes) {
            if (!mEarthquakes.contains(earthquake) && earthquake.getMagnitude() >= mMagnitude) {
                mEarthquakes.add(earthquake);
                recyclerViewAdapter.notifyItemInserted(mEarthquakes.indexOf(earthquake));
            }
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_earthquake, container, false);
        recyclerView = view.findViewById(R.id.list);
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewAdapter = new EarthquakeRecyclerViewAdapter(mEarthquakes);
        recyclerView.setAdapter(recyclerViewAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateEarthquakes();
            }
        });
    }

    private void updateEarthquakes() {
        earthquakeViewModel.loadEarthquakes();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("minMagnitude")) {
            List<Earthquake> earthquakes = earthquakeViewModel.getEarthquakes().getValue();
            setEarthquakes(earthquakes);
        }
    }

    private void updateFromPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mMagnitude = Integer.valueOf(sharedPreferences.getString("minMagnitude", "3"));
    }
}
