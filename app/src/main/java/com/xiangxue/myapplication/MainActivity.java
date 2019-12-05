package com.xiangxue.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.xiangxue.myapplication.activity.CustomProgressActivity;
import com.xiangxue.myapplication.activity.FlowActivity;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        LinearLayout rootLayout = findViewById(R.id.ll_layout);
        if (rootLayout != null) {
            setItemClick(rootLayout);
        }
    }

    private void setItemClick(LinearLayout rootLayout) {
        int childCount = rootLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = rootLayout.getChildAt(i);
            child.setTag(i);
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch ((int) v.getTag()) {
                        case 0:
                            startActivity(new Intent(MainActivity.this, FlowActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(MainActivity.this, CustomProgressActivity.class));
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                    }

                }
            });
        }
    }

}
