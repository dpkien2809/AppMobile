package com.example.mobileproject;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mobileproject.CustomFragmentAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Calendar;
import java.util.TimeZone;

public class Dashboard extends AppCompatActivity {


    BottomNavigationView mBottomNavigationView;
   public static ViewPager2 viewPager;
    public static String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        Intent current_intent = getIntent();
        Dashboard.token = current_intent.getStringExtra("access_token");
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                return;
            }
        });

        
        viewPager = findViewById(R.id.viewPager);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setFocusable(View.FOCUSABLE);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(new CustomFragmentAdapter(this));
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay >= 5 && hourOfDay <= 17) {
        } else {
            mBottomNavigationView.setBackgroundColor(getResources().getColor(R.color.buttonColor));
        }
        viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                final float MIN_SCALE = 0.75f;
                int pageWidth = page.getWidth();
                if (position < -1) {
                    page.setAlpha(0f);
                } else if (position <= 0) {
                    page.setAlpha(1f);
                    page.setTranslationX(0f);
                    page.setScaleX(1f);
                    page.setScaleY(1f);
                } else if (position <= 1) {
                    page.setAlpha(1 - position);
                    page.setTranslationX(pageWidth * -position);
                    float scaleFactor = MIN_SCALE
                            + (1 - MIN_SCALE) * (1 - Math.abs(position));
                    page.setScaleX(scaleFactor);
                    page.setScaleY(scaleFactor);
                } else {
                    page.setAlpha(0f);
                }
            }
        });


        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        mBottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
                        viewPager.setUserInputEnabled(true);

                        break;
                    case 1:
                        mBottomNavigationView.getMenu().findItem(R.id.menu_map).setChecked(true);
                        viewPager.setUserInputEnabled(false);
                        break;
                    case 2:
                        mBottomNavigationView.getMenu().findItem(R.id.menu_graph).setChecked(true);
                        viewPager.setUserInputEnabled(true);
                        break;
                    case 3:
                        mBottomNavigationView.getMenu().findItem(R.id.menu_user).setChecked(true);
                        viewPager.setUserInputEnabled(true);
                        break;
                }
            }
        });


       mBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               int itemId = item.getItemId();
               if (itemId == R.id.menu_home){
                   {
                       viewPager.setCurrentItem(0);
                       viewPager.setUserInputEnabled(true);
                   }
               } else if (itemId == R.id.menu_map) {
                   viewPager.setUserInputEnabled(false);
                   viewPager.setCurrentItem(1);

               } else if (itemId==R.id.menu_graph) {
                   viewPager.setCurrentItem(2);
                   viewPager.setUserInputEnabled(true);
               } else if (itemId==R.id.menu_user) {
                   viewPager.setCurrentItem(3);
                   viewPager.setUserInputEnabled(true);
               }
               return true;
           }
       });

       viewPager.setCurrentItem(1);

    }
}