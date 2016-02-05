package com.example.sanggon.locationviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final String[] daysOfTheWeek = {
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    private static final String SOURCE_URL = "http://letabc.xyz/";
    private LocationListAdapter mAdapter;

    public String saveImageFromBitmap (Bitmap bitmap){
        String fileName = "temp_image_file";
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new LocationListAdapter(getApplicationContext());
        ListView listView = (ListView)findViewById(R.id.mainListView);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationItem locationItem = mAdapter.getItem(position);
                if (locationItem == null) {
                    Log.e(TAG, "The clicked item is null! Investigate!");
                    return;
                }

                Intent intent = new Intent(view.getContext(), ItemViewActivity.class);
                intent.putExtra("title", locationItem.getTitle());
                intent.putExtra("description", locationItem.getDescription());
                intent.putExtra("address", locationItem.getAddress());
                intent.putExtra("hours", locationItem.getHours());
                intent.putExtra("url", locationItem.getUrl());
                intent.putExtra("image_filename", saveImageFromBitmap(locationItem.getImage()));

                startActivity(intent);
            }
        });

        DownloadDataTask task = new DownloadDataTask();
        task.execute(SOURCE_URL);
    }

    public String convertStreamToString(InputStream stream) {
        try(java.util.Scanner s = new java.util.Scanner(stream)) {
            return s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }
    }

    public String getJsonStringFromURL(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            return convertStreamToString(new BufferedInputStream(urlConnection.getInputStream()));
        } catch (MalformedURLException e) {
            Log.e(TAG, e.toString());
            return null;
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Bitmap loadImageFromUrl(String url) {
        try {
            InputStream stream = (InputStream) new URL(url).getContent();
            return BitmapFactory.decodeStream(stream);
        } catch (Exception e) {
            return null;
        }
    }

    private class DownloadDataTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... params) {
            String jsonString = getJsonStringFromURL(params[0]);
            if (jsonString != null) {
                Log.d(TAG, "JSON retrieved: " + jsonString);
                try {
                    JSONArray jsonArray = new JSONArray(jsonString);
                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        // retrieve image from url
                        Bitmap image = loadImageFromUrl(jsonObject.getString("image"));

                        // get all the hours and pass as a map
                        JSONObject hoursJson = jsonObject.getJSONObject("hours");
                        HashMap<String, String> hours = new HashMap<>();
                        for (String day : daysOfTheWeek) {
                            hours.put(day, hoursJson.getString(day));
                        }

                        mAdapter.add(new LocationItem(
                                jsonObject.getString("title"),
                                jsonObject.getString("description"),
                                jsonObject.getString("address"),
                                hours,
                                image,
                                jsonObject.getString("url")
                        ));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.toString());
                }
            }
            return jsonString;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute");
            mAdapter.notifyDataSetChanged();
        }
    }
}
