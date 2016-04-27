package com.example.arzubek.popularmoviesapp.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.app.*;

import com.squareup.picasso.Picasso;

import junit.framework.Test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.StringBuilder;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    GridView gridView;
//    private ArrayAdapter<String>

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridview_id);
        MovieTask mTask = new MovieTask();
        mTask.execute();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (RELOAD) {
            MovieTask movieTask = new MovieTask();
            movieTask.execute();
        }
    }

    public static boolean RELOAD = false;

    public class MovieTask extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG = "arzubekNawayi";

        private String sortOrder(String order) {

            String THEMOVIE_URL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=8473b2e3a954caef16c397478a9feb32";
            String THEMOVIE_TOPRATED_URL = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count.gte=50&api_key=8473b2e3a954caef16c397478a9feb32";
            String ret;

            if (order.equals(getString(R.string.pref_sort_item))) {
                ret = THEMOVIE_URL;
            }
            else ret = THEMOVIE_TOPRATED_URL;

            return ret;
        }

        ArrayList<String> orgTitle = new ArrayList<String>();
        ArrayList<String> relDate = new ArrayList<String>();
        ArrayList<String> voteAvg = new ArrayList<String>();
        ArrayList<String> overview = new ArrayList<String>();
        ArrayList<String> posterPath = new ArrayList<String>();

        private String[] getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

            final String MD_BASE_URL = "http://image.tmdb.org/t/p/w185/";
            final String MD_ARRAY = "results";
            final String MD_IMAGE = "poster_path";
            final String MD_ORIGINAL_TITLE = "original_title";
            final String MD_RELEASE_DATE = "release_date";
            final String MD_VOTE_AVERAGE = "vote_average";
            final String MD_OVERVIEW = "overview";
            String urlAddition = "";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MD_ARRAY);

            String[] arrImageUrl = new String[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {

                arrImageUrl[i] = MD_BASE_URL + movieArray.getJSONObject(i).getString(MD_IMAGE);
                posterPath.add( MD_BASE_URL + movieArray.getJSONObject(i).getString(MD_IMAGE) );
                orgTitle.add( movieArray.getJSONObject(i).getString(MD_ORIGINAL_TITLE) );
                relDate.add( movieArray.getJSONObject(i).getString(MD_RELEASE_DATE) );
                voteAvg.add(movieArray.getJSONObject(i).getString(MD_VOTE_AVERAGE));
                overview.add( movieArray.getJSONObject(i).getString(MD_OVERVIEW) );
            }
            return arrImageUrl;
        }

        @Override
        protected String[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;

            try {

                String order = PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getString(getString(R.string.pref_sort_key),
                                getString(R.string.pref_sort_item));
                URL url = new URL(sortOrder(order));

                // Construct the URL for the OpenWeatherMap query
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Read the input stream into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    //forecastJsonStr = null;
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    //forecastJsonStr = null;
                    return null;
                }
                movieJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Forecast JSON String: " + movieJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error " + e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                //forecastJsonStr = null;
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {

            super.onPostExecute(strings);

            final CustomAdapter adapter = new CustomAdapter(getActivity(), 0, strings);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String str = orgTitle.get(position);
                    String relD = relDate.get(position);
                    String voteA = voteAvg.get(position) + "/10";
                    String overv = overview.get(position);
                    String posteP = posterPath.get(position);
                    Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, str)
                            .putExtra(Intent.EXTRA_REFERRER_NAME, relD.substring(0, 4))
                            .putExtra(Intent.EXTRA_SHORTCUT_NAME, voteA)
                            .putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, overv)
                            .putExtra(Intent.EXTRA_REFERRER, posteP);
                    startActivity(intent);
                }
            });
        }

        class CustomAdapter extends ArrayAdapter<String> {
            LayoutInflater mInflate;
            public CustomAdapter(Context context, int resource, String[] objects) {
                super(context, resource, objects);
                mInflate = LayoutInflater.from(context);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = mInflate.inflate(R.layout.image_list, null);
                }

                String url = getItem(position);

                PosterImageView imgView = (PosterImageView) convertView.findViewById(R.id.image_list_id);
                Context context = getActivity();
                Picasso.with(context).load(url).into(imgView);

                return convertView;
            }
        }
    }
}
