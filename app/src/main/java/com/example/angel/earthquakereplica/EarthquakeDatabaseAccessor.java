package com.example.angel.earthquakereplica;

import android.arch.persistence.room.Room;
import android.content.Context;

public class EarthquakeDatabaseAccessor {

    private static EarthquakeDatabase earthquakeDatabase;

    public static EarthquakeDatabase getInstance(Context c) {
        if(earthquakeDatabase == null) {
            earthquakeDatabase = Room.databaseBuilder(
                    c,
                    EarthquakeDatabase.class,
                    "earthquake_db"
            ).build();
        }

        return earthquakeDatabase;
    }
}
