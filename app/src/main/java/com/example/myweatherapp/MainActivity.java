package com.example.myweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText set_location;
    private Button search_btn;
    private TextView result_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        set_location = findViewById(R.id.set_location);
        search_btn = findViewById(R.id.search_btn);
        result_info = findViewById(R.id.result_info);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(set_location.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.no_location, Toast.LENGTH_LONG)
                    .show();
                else {
                    String city = set_location.getText().toString();
                    String key = "0942db6aa58b762fcf7b0844bcd0bb2e";
                    // TODO:
                    String url3 = String.format("https://api.openweathermap.org/geo/1.0/direct?q={0}&limit=5&appid={1}&units=metric", city, key);
                    String url = "https://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=5&appid=" + key + "&units=metric";

                    new GetWeatherData().execute(url);
                }
            }
        });
    }

    // extends - inheritance
    private class GetWeatherData extends AsyncTask<String,String, String> {

        protected void waitResponse(){
            super.onPreExecute();
            result_info.setText("Calculating...");
        }

        // (String... strings) - no limit input parameters
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                // (HttpUrlConnection) - convert to this type
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                // Using StringBuffer because we use for response BufferReader
                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null)
                    connection.disconnect();

                try {
                    if(reader != null)
                        reader.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
            }

            return "Something went wrong.";
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                result_info.setText("Temperature: " + jsonObject.getJSONObject("main").getDouble("temp"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            result_info.setText(result);
        }
    }
}