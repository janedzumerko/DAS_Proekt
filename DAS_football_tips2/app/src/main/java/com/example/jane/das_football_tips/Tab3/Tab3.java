package com.example.jane.das_football_tips.Tab3;

/**
 * Created by Jane on 10/12/2015.
 */

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jane.das_football_tips.R;


public class Tab3 extends Fragment {

    SQLiteDatabase sqLiteDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_3, container, false);

        return v;
    }
}