package com.dscnitp.freshersportal.Alumni;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dscnitp.freshersportal.Alumni.AlumniChatFragment;
import com.dscnitp.freshersportal.Alumni.AlumniGroupFragment;


public class TabAccessorAdapterAlumni extends FragmentPagerAdapter {
    public TabAccessorAdapterAlumni(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                AlumniChatFragment alumniChatFragment=new AlumniChatFragment();
                return alumniChatFragment;
            case 1:
                AlumniGroupFragment alumniGroupFragment=new AlumniGroupFragment();
                return alumniGroupFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Personal Chat";
            case 1:
                return "Groups";
            default:
                return null;

        }
    }
}
