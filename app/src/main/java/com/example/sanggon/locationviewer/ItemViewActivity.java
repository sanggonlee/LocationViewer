package com.example.sanggon.locationviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;

public class ItemViewActivity extends AppCompatActivity {
    private static final String TAG = "ItemViewActivity";

    public static Integer parseTimeString(String time) {
        Integer hour = 0, minute = 0;
        if (time.charAt(time.length()-2) == 'P') {
            hour = 12;
        }
        String[] temp = time.substring(0, time.length()-2).split(":");
        hour += Integer.parseInt(temp[0]);
        if (temp.length == 2) {
            minute = Integer.parseInt(temp[1]);
        }
        return hour * 60 + minute;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        TextView titleView = (TextView)findViewById(R.id.title);
        titleView.setText(getIntent().getStringExtra("title"));

        TextView descriptionView = (TextView)findViewById(R.id.description);
        descriptionView.setText(getIntent().getStringExtra("description"));

        try {
            ImageView imageView = (ImageView)findViewById(R.id.image);
            Bitmap bitmap = BitmapFactory.decodeStream(this.
                    openFileInput(getIntent().getStringExtra("image_filename")));
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 300, 300, false));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        final TextView addressView = (TextView)findViewById(R.id.address);
        addressView.setText(getIntent().getStringExtra("address"));
        addressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + addressView.getText().toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        final TextView urlView = (TextView)findViewById(R.id.website);
        urlView.setText(getIntent().getStringExtra("url"));

        Log.d("IVA", "working with times");
        TextView statusNow = (TextView)findViewById(R.id.status_now);
        HashMap<String, String> daysOfTheWeek = (HashMap) getIntent().getSerializableExtra("hours");

        Calendar rightNow = Calendar.getInstance();
        int today = rightNow.get(Calendar.DAY_OF_WEEK);
        String todayTime = daysOfTheWeek.get(MainActivity.daysOfTheWeek[today]);
        if (todayTime.equals("Closed")) {
            statusNow.setText("Closed now");
        } else {
            String[] times = todayTime.split("-");
            Integer startTime = parseTimeString(times[0]);
            Integer endTime = parseTimeString(times[1]);

            // get current time
            long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
            long sinceMidnight = (rightNow.getTimeInMillis() + offset) / (60 * 1000) % (24 * 60);

            if (sinceMidnight >= startTime && sinceMidnight < endTime) {
                statusNow.setText("Open now");
            } else {
                statusNow.setText("Closed now");
            }
        }

        // Hours, Monday through Friday
        TextView hoursMonday = (TextView)findViewById(R.id.hours_monday);
        hoursMonday.setText("Monday: " + daysOfTheWeek.get(MainActivity.daysOfTheWeek[0]));
        TextView hoursTuesday = (TextView)findViewById(R.id.hours_tuesday);
        hoursTuesday.setText("Tuesday: " + daysOfTheWeek.get(MainActivity.daysOfTheWeek[1]));
        TextView hoursWednesday = (TextView)findViewById(R.id.hours_wednesday);
        hoursWednesday.setText("Wednesday: " + daysOfTheWeek.get(MainActivity.daysOfTheWeek[2]));
        TextView hoursThursday = (TextView)findViewById(R.id.hours_thursday);
        hoursThursday.setText("Thursday: " + daysOfTheWeek.get(MainActivity.daysOfTheWeek[3]));
        TextView hoursFriday = (TextView)findViewById(R.id.hours_friday);
        hoursFriday.setText("Friday: " + daysOfTheWeek.get(MainActivity.daysOfTheWeek[4]));
        TextView hoursSaturday = (TextView)findViewById(R.id.hours_saturday);
        hoursSaturday.setText("Saturday: " + daysOfTheWeek.get(MainActivity.daysOfTheWeek[5]));
        TextView hoursSunday = (TextView)findViewById(R.id.hours_sunday);
        hoursSunday.setText("Sunday: " + daysOfTheWeek.get(MainActivity.daysOfTheWeek[6]));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_view, menu);
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
}
