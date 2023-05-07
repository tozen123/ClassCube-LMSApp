package com.doublehammerstudios.classcube;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.doublehammerstudios.classcube.Fragment.InsideStudentClassActivityRecordFragment;

import java.util.ArrayList;

public class ClassRecordTabAdapter extends FragmentPagerAdapter {
    private ArrayList<Class> objClass;

    public ClassRecordTabAdapter(@NonNull FragmentManager fm, ArrayList<Class> objClass) {
        super(fm);
        this.objClass = objClass;
    }

    @Override
    public Fragment getItem(int position) {
        return InsideStudentClassActivityRecordFragment.newInstance(objClass.get(position));
    }

    @Override
    public int getCount() {
        return objClass.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return objClass.get(position).getClassName() + " | " + objClass.get(position).getClassSubject();
    }


}
