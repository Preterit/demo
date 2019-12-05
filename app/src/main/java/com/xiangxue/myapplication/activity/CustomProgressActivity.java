package com.xiangxue.myapplication.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.xiangxue.myapplication.R;
import com.xiangxue.myapplication.widget.HorizontalProgressBar;
import com.xiangxue.myapplication.widget.RoundProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CustomProgressActivity extends AppCompatActivity {

    private HorizontalProgressBar mProgress;
    private RoundProgressBar mRoundBar;
    private static final int MSG_UPDATE = 0X110;
    private static final int MSG_UPDATE_2 = 0X120;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_UPDATE:
                    int progress = mProgress.getProgress();
                    mProgress.setProgress(++progress);
                    if (progress >= 100) {
                        mHandler.removeMessages(MSG_UPDATE);
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 100);
                    break;
                case MSG_UPDATE_2:
                    int mRoundBarProgress = mRoundBar.getProgress();
                    mRoundBar.setProgress(++mRoundBarProgress);
                    if (mRoundBarProgress >= 100) {
                        mHandler.removeMessages(MSG_UPDATE_2);
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_2, 100);
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_progress);
        mProgress = findViewById(R.id.progress);
        mRoundBar = findViewById(R.id.roundBar);

        mHandler.sendEmptyMessage(MSG_UPDATE);
        mHandler.sendEmptyMessage(MSG_UPDATE_2);
    }
}
