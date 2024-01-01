package com.example.mobileproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mobileproject.home.HomeFragment;
import com.example.mobileproject.graph.GraphFragment;
import com.example.mobileproject.map.MapFragment;
import com.example.mobileproject.user.UserSettingFragment;

public class CustomFragmentAdapter extends FragmentStateAdapter {

    public CustomFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);


    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();

            case 1:
                return new MapFragment();

            case 2:
                return new GraphFragment();

            case 3:
                return new UserSettingFragment();


        }
        return new  MapFragment();
    }
    @Override
    public int getItemCount() {
        return 4;
    }


}
