package com.doublehammerstudios.classcube;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public interface ClassHandler {
    void onClassClicked(Class selectedClass);
}
