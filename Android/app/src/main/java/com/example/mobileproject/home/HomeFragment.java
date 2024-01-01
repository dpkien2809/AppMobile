package com.example.mobileproject.home;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mobileproject.api.APIClient;
import com.example.mobileproject.api.APIInterface;
import com.example.mobileproject.Dashboard;
import com.example.mobileproject.R;
import com.example.mobileproject.api.AssetApi;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    View view;
    Thread background_Thread;
    TextView temp,hum,win_dir,win_speed,rainfall, username, daytime;
    Handler ui_handler = new Handler();
    ImageView img;
    APIInterface apiInterface;
    private LinearLayout backgroundLayout, backgroundLayout1, backgroundLayout2;

    @Override
    public void onStart() {
        background_Thread.start();
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        background_Thread.interrupt();
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_home, container, false);


        temp = view.findViewById(R.id.temp_value);
        hum = view.findViewById(R.id.humidity_value);
        win_dir = view.findViewById(R.id.wind_direction_value);
        win_speed = view.findViewById(R.id.win_speed_value);
        rainfall =view.findViewById(R.id.rainfall_value);
        username = view.findViewById(R.id.name);
        daytime = view.findViewById(R.id.daytime);
        img = view.findViewById(R.id.imageView1);
        backgroundLayout = view.findViewById(R.id.background);
        backgroundLayout1 = view.findViewById(R.id.background1);
        backgroundLayout2 = view.findViewById(R.id.background2);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call User = apiInterface.getUser();
        User.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("API Call", response.code() + "");
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API Call", "Successful");
                    Log.d("API Call", response.toString());
                    Log.d("API Call", "response: " + new Gson().toJson(response.body()));
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        String userValue = jsonObject.getString("username");
                        String firstnameValue = jsonObject.getString("firstName");
                        String lastnameValue = jsonObject.getString("lastName");
                        if(firstnameValue == null || lastnameValue == null || firstnameValue == null && lastnameValue == null){
                            username.setText(userValue);
                        } else {
                            username.setText(firstnameValue + " " + lastnameValue);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("API CALL", t.getMessage().toString());

                //t.printStackTrace();

            }
        });


        background_Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){


                    try {
                        AssetApi request = new AssetApi(
                                String.format("https://uiot.ixxc.dev/api/master/asset/%s","5zI6XqkQVSfdgOrZ1MyWEf"),
                                "GET",
                                Dashboard.token
                        );
                        Map<String,String> response = request.GeData();
                        ui_handler.post(new Runnable() {
                            @Override
                            public void run() {
                                temp.setText(Objects.requireNonNull(response.get("temperature")).concat(" °C"));
                                hum.setText(Objects.requireNonNull(response.get("humidity")).concat(" %"));
                                win_dir.setText(response.get("windDirection") + " degrees");
                                win_speed.setText(Objects.requireNonNull(response.get("windSpeed")).concat(" km/s"));
                                rainfall.setText(Objects.requireNonNull(response.get("rainfall")).concat(" mm"));
                                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
                                int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                                int minuteOfDay = calendar.get(Calendar.MINUTE);
                                int secondOfDay = calendar.get(Calendar.SECOND);
                                daytime.setText(String.valueOf(hourOfDay) + "h" + String.valueOf(minuteOfDay) + "m" + String.valueOf(secondOfDay) + "s" + " " + "\n(last updated)");
                                if(hourOfDay >= 5 && hourOfDay <= 17)
                                {
                                    img.setImageResource(R.drawable.sunny);
                                } else {
                                    img.setImageResource(R.drawable.night);
                                    backgroundLayout.setBackgroundResource(R.drawable.background1);
                                    backgroundLayout1.setBackgroundResource(R.drawable.background3);
                                    backgroundLayout2.setBackgroundResource(R.drawable.background3);
                                }
                                Log.d("interrupt","false");


                            }
                        });


                        Thread.sleep(1800000);
                    } catch (IOException | InterruptedException e) {
                        ui_handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("interrupt","true");
                            }
                        });
                        return;
                    }
                }
            }
        });


        return view;
    }
}