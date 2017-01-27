package com.bignerdranch.android.splash;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

public class DailyReward extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Dialog rewardDialog = new Dialog(this);
        //rewardDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        rewardDialog.setContentView(getLayoutInflater().inflate(R.layout.activity_daily_reward, null));
        rewardDialog.show();
        rewardDialog.setCanceledOnTouchOutside(true);

    }
}
