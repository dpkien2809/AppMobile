package com.example.mobileproject.map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;


import androidx.fragment.app.Fragment;

import com.example.mobileproject.Dashboard;
import com.example.mobileproject.R;
import com.example.mobileproject.api.AssetApi;


import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapFragment extends Fragment {

    View view;
    MapView map;
    Handler ui_handler = new Handler();
    Map<String,String> asset_data;

    Thread createWeatherAssetThread(String asset_id){
        Thread thread = new Thread(new Runnable() {
            final Map<String,String> header = new HashMap<>();

            @Override
            public void run() {
                header.put("Authorization", "Bearer ".concat(Dashboard.token));
                header.put("accept","application/json");
                try {
                    AssetApi request = new AssetApi(
                            String.format("https://uiot.ixxc.dev/api/master/asset/%s",asset_id),
                            "GET",
                            Dashboard.token
                    );
                    asset_data = request.GeData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return thread;
    }





    void showDefaultWeatherMarker(View current_view, String title) throws InterruptedException {
        Thread thread = createWeatherAssetThread("5zI6XqkQVSfdgOrZ1MyWEf");
        thread.start();
        thread.join();
        Dialog dialog = new Dialog(current_view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.weather_asset_marker);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        TextView title_textview = dialog.findViewById(R.id.txtview_title);
        TextView humidity_value = dialog.findViewById(R.id.txtview_humidity_value);
        TextView manufacturer = dialog.findViewById(R.id.txtview_manufacturer_value);
        TextView place = dialog.findViewById(R.id.txtview_place_value);
        TextView rainfall_value = dialog.findViewById(R.id.txtview_rainfall_value);
        TextView sun_altitude = dialog.findViewById(R.id.txtview_sunaltitude_value);
        TextView sun_zenith = dialog.findViewById(R.id.txtview_zenith_value);
        TextView tags = dialog.findViewById(R.id.txtview_tags_value);
        TextView temp_value = dialog.findViewById(R.id.txtview_temperature_value);
        TextView uv_index = dialog.findViewById(R.id.txtview_uvindex_value);
        TextView wind_direction = dialog.findViewById(R.id.txtview_winddirection_value);
        TextView wind_speed = dialog.findViewById(R.id.txtview_windspeed_value);

        TextView sun_azimuth = dialog.findViewById(R.id.txtview_azimuth_value);
        TextView sun_ir_radiance = dialog.findViewById(R.id.txtview_sunirradiance_value);
        sun_ir_radiance.append(asset_data.get("sunIrradiance"));
        sun_azimuth.append(asset_data.get("sunAzimuth"));

        humidity_value.append(asset_data.get("humidity"));
        temp_value.append(asset_data.get("temperature"));
        manufacturer.append(asset_data.get("manufacturer"));
        place.append(asset_data.get("place"));
        rainfall_value.append(asset_data.get("rainfall"));
        sun_altitude.append(asset_data.get("sunAltitude"));
        sun_zenith.append(asset_data.get("sunZenith"));
        tags.append(asset_data.get("tags"));
        uv_index.append(asset_data.get("uVIndex"));
        wind_speed.append(asset_data.get("windSpeed"));
        wind_direction.append(asset_data.get("windDirection"));

        title_textview.setText(title);
        dialog.show();
    }

    void showLightMarker(View current_view,String title) throws InterruptedException {
        Thread thread = createWeatherAssetThread("6iWtSbgqMQsVq8RPkJJ9vo");
        thread.start();
        thread.join();
        Dialog dialog = new Dialog(current_view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.light_marker);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        TextView brightness = dialog.findViewById(R.id.txtview_brightness_value);
        TextView colour_r  = dialog.findViewById(R.id.txtview_colour_r_value);
        TextView colour_k  = dialog.findViewById(R.id.txtview_colour_k_value);
        TextView email = dialog.findViewById(R.id.txtview_email_value);
        TextView status = dialog.findViewById(R.id.txtview_status_value);
        TextView tags = dialog.findViewById(R.id.txtview_tags_value);
        brightness.append(asset_data.get("brightness"));
        colour_r.append(asset_data.get("colourRGB"));
        colour_k.append(asset_data.get("colourTemperature"));
        email.append(asset_data.get("email"));
        status.append(asset_data.get("onOff"));
        tags.append(asset_data.get("tags"));
        dialog.show();
    }









    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false);
        Context ctx = view.getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));


        map = view.findViewById(R.id.map);
        map.setMultiTouchControls(true);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMinZoomLevel(0.0);
        map.setMaxZoomLevel(19.0);


        IMapController mapController = map.getController();
        mapController.setZoom(16.0);

        Thread thread = createWeatherAssetThread("5zI6XqkQVSfdgOrZ1MyWEf");
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        GeoPoint startPoint = new GeoPoint(
                Double.parseDouble(Objects.requireNonNull(asset_data.get("latitude"))),
                Double.parseDouble(Objects.requireNonNull(asset_data.get("longitude")))
                );
        mapController.setCenter(startPoint);


         RotationGestureOverlay  mRotationGestureOverlay = new RotationGestureOverlay(map);
         mRotationGestureOverlay.setEnabled(true);
         map.getOverlays().add(mRotationGestureOverlay);


        CompassOverlay mCompassOverlay = new CompassOverlay(view.getContext(), new InternalCompassOrientationProvider(view.getContext()), map);
        mCompassOverlay.enableCompass();
        map.getOverlays().add(mCompassOverlay);




        Marker defaultweather_marker = new Marker(map);
        int a = R.drawable.weather_partly_cloudy;
        Resources res = getResources();
        Drawable icon =  res.getDrawable(R.drawable.weather_partly_cloudy, getContext().getTheme());

        defaultweather_marker.setImage(icon);
        defaultweather_marker.setTitle("Default weather");
        defaultweather_marker.setPosition(new GeoPoint(
                Double.parseDouble(Objects.requireNonNull(asset_data.get("latitude"))),
                Double.parseDouble(Objects.requireNonNull(asset_data.get("longitude")))
        ));

        defaultweather_marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        defaultweather_marker.setIcon(res.getDrawable(R.drawable.weather_partly_cloudy, ctx.getTheme()));
        defaultweather_marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                try {
                    showDefaultWeatherMarker(mapView,marker.getTitle());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }
        });


        thread = createWeatherAssetThread("6iWtSbgqMQsVq8RPkJJ9vo");
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Marker light_marker = new Marker(map);
        light_marker.setTitle("Light");
        light_marker.setPosition(new GeoPoint(
                Double.parseDouble(Objects.requireNonNull(asset_data.get("latitude"))),
                Double.parseDouble(Objects.requireNonNull(asset_data.get("longitude")))
        ));
        light_marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);

        light_marker.setIcon(res.getDrawable(R.drawable.light_bulb_color_icon, ctx.getTheme()));


        light_marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                try {
                    showLightMarker(mapView,marker.getTitle());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }
        });


        map.getOverlays().add(defaultweather_marker);
        map.getOverlays().add(light_marker);
        map.invalidate();

        return view;
    }


}