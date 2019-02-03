package com.example.angel.earthquakereplica;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EarthquakeViewModel extends AndroidViewModel {

    LiveData<List<Earthquake>> earthquakes = null;

    public EarthquakeViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Earthquake>> getEarthquakes() {
        if(earthquakes  == null) {
            earthquakes = EarthquakeDatabaseAccessor
                    .getInstance(getApplication())
                    .earthquakeDAO().loadAllEarthquakes();
            loadEarthquakes();
        }

        return earthquakes;
    }

    @SuppressLint("StaticFieldLeak")
    public void loadEarthquakes() {
        new AsyncTask<Void, Void, List<Earthquake>>() {

            @Override
            protected List<Earthquake> doInBackground(Void... voids) {
                List<Earthquake> earthquakes = new ArrayList<>();
                Log.e("RANDOMLOG", "doInBackground started");
                try {
                    String feed = getApplication().getString(R.string.earthquake_feed);
                    URL url = new URL(feed);
                    URLConnection urlConnection = url.openConnection();
                    HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                    if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Log.e("RANDOMLOG", "starting loading data");
                        InputStream inputStream = httpURLConnection.getInputStream();
                        earthquakes = parseJson(inputStream);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                EarthquakeDatabaseAccessor.getInstance(getApplication())
                        .earthquakeDAO().insertEarthquakes(earthquakes);

                return earthquakes;
            }

            @Override
            protected void onPostExecute(List<Earthquake> earthquakesList) {
                super.onPostExecute(earthquakesList);
            }
        }.execute();
    }

    private List<Earthquake> parseJson(InputStream in) throws IOException {

        List<Earthquake> earthquakes = new ArrayList<>();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(in));
        jsonReader.beginObject();
        while(jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if(name.equals("features")) {
                earthquakes = readEarthquakeArray(jsonReader);
            } else
                jsonReader.skipValue();
        }
        jsonReader.endObject();
        Log.e("RANDOMLOG", String.valueOf(earthquakes.size()));
        return earthquakes;
    }

    private List<Earthquake> readEarthquakeArray(JsonReader jsonReader) throws IOException {
        List<Earthquake> earthquakes = new ArrayList<>();
        jsonReader.beginArray();
        while(jsonReader.hasNext()) {
            earthquakes.add(readEarthquake(jsonReader));
        }
        jsonReader.endArray();

        return earthquakes;
    }

    private Earthquake readEarthquake(JsonReader reader) throws IOException {
        Earthquake earthquake = null;
        String id = null;
        Location location = null;

        reader.beginObject();
        while (reader.hasNext()){
            Log.e("RANDOMLOG", "reading object");
            String name = reader.nextName();
            if(name.equals("id")) {
                id = reader.nextString();
            } else if(name.equals("geometry")) {
                location = readLocation(reader);
            } else if(name.equals("properties")) {
                earthquake = readProperties(reader);
            } else
                reader.skipValue();
        }
        reader.endObject();
        return new Earthquake(id, earthquake.getDate(), earthquake.getDetails(), location,
                earthquake.getMagnitude(), earthquake.getLink());
    }

    private Location readLocation(JsonReader reader) throws IOException {
        Location location = new Location("T");
        List<Double> coord = new ArrayList<>();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals("coordinates")) {
                coord = readDouble(reader);
            } else
                reader.skipValue();
        }
        reader.endObject();
        location.setLatitude(coord.get(0));
        location.setLongitude(coord.get(1));
        Log.e("RANDOMLOG", "location works");
        return location;
    }

    private List<Double> readDouble(JsonReader reader) throws IOException {
        List<Double> doubles = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            doubles.add(reader.nextDouble());
        }
        reader.endArray();
        return doubles;
    }

    private Earthquake readProperties(JsonReader jsonReader) throws IOException {
        Earthquake earthquake;
        Date date = null;
        String link = null;
        double mag = 0;
        String details = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if(name.equals("time")) {
                long time = jsonReader.nextLong();
                date = new Date(time);
            } else if(name.equals("mag")) {
                mag = jsonReader.nextDouble();
            } else if(name.equals("url")) {
                link = jsonReader.nextString();
            } else if(name.equals("place")) {
                details = jsonReader.nextString();
            } else
                jsonReader.skipValue();
        }
        jsonReader.endObject();

        earthquake = new Earthquake(null, date, details, null, mag, link);
        return earthquake;
    }
}
