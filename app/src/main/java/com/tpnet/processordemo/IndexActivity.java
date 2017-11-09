package com.tpnet.processordemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.tpnet.apt.BindView;
import com.tpnet.apt.TPButterKnife;

/**
 * Created by Litp on 2017/11/7.
 */

public class IndexActivity extends AppCompatActivity {


    @BindView(R.id.btn_open)
    Button mBtnOpen;


    @BindView(R.id.listView)
    ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_index);

        TPButterKnife.inject(this);

        mBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(IndexActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        String[] city = {"广州","深圳","北京","上海","香港","澳门","天津","广州","深圳","北京","上海","香港","澳门","天津"};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, city);
        mListView.setAdapter(arrayAdapter);
    }
}
