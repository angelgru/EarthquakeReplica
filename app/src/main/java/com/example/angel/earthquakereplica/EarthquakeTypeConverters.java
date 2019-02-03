package com.example.angel.earthquakereplica;

import android.arch.persistence.room.TypeConverter;
import android.location.Location;

import java.util.Date;

public class EarthquakeTypeConverters {

    @TypeConverter
    public static Date dateFromTimestamp(long time) {
        return new Date(time);
    }

    @TypeConverter
    public static long timestampFromDate(Date date) {
        return date.getTime();
    }

    @TypeConverter
    public static Location locationFromString(String l) {
        Location location = new Location("A");
        String[] locationArray = l.split(",");
        location.setLatitude(Double.parseDouble(locationArray[0]));
        location.setLongitude(Double.parseDouble(locationArray[1]));
        return location;
    }

    @TypeConverter
    public static String stringFromLocation(Location location) {
        String s = location.getLatitude() + "," + location.getLongitude();
        return s;
    }
}
