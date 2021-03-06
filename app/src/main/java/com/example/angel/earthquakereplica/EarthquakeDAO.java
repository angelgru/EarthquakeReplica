package com.example.angel.earthquakereplica;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface EarthquakeDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertEarthquakes(List<Earthquake> earthquakes);

    @Insert
    public void insertEarthquake(Earthquake earthquake);

    @Delete
    public  void deleteEarthquake(Earthquake earthquake);

    @Query("SELECT * FROM Earthquake ORDER BY mDate DESC")
    public LiveData<List<Earthquake>> loadAllEarthquakes();
}
