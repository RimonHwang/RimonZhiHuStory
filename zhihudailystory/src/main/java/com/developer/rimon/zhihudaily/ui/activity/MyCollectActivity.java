package com.developer.rimon.zhihudaily.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.developer.rimon.zhihudaily.R;
import com.developer.rimon.zhihudaily.adapter.MyCollectAdpter;
import com.developer.rimon.zhihudaily.entity.Collect;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyCollectActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler)
    RecyclerView recycler;

    private ArrayList<Collect> collectArrayList = new ArrayList<>();
    private MyCollectAdpter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);
        ButterKnife.bind(this);

        initToolbar();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new MyCollectAdpter(this, collectArrayList);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);

        BmobUser bmobUser = BmobUser.getCurrentUser();
        BmobQuery<Collect> query = new BmobQuery<>();
        query.addWhereRelatedTo("collect", new BmobPointer(bmobUser));
        query.findObjects(new FindListener<Collect>() {
            @Override
            public void done(List<Collect> list, BmobException e) {
                if (e== null && list!= null){
                    collectArrayList.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initToolbar() {
        toolbar.setTitle("我的收藏");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

