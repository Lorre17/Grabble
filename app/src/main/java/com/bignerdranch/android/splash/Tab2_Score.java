package com.bignerdranch.android.splash;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Lorena on 23/01/2017.
 */

public class Tab2_Score extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_score, container, false);

        TextView lastWordSubmited = (TextView) rootView.findViewById(R.id.score_textview);
        TextView totalScore = (TextView) rootView.findViewById(R.id.totalScore_textView);

        lastWordSubmited.setText("Last word submitted: " + Maps_Activity.score);
        totalScore.setText("" + Maps_Activity.totalScore);

        return rootView;
    }
}
