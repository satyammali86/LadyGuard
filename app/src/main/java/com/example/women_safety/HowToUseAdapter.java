package com.example.women_safety;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HowToUseAdapter extends FragmentStateAdapter {

    public HowToUseAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Screen1Fragment();
            case 1:
                return new Screen2Fragment();
            default:
                return new Screen1Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;  // Number of screens
    }
}
