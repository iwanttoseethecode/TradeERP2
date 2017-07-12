package gttrade.guantang.com.tradeerp.RecyclerViewDemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import gttrade.guantang.com.tradeerp.R;

public class RecyclerActivity extends AppCompatActivity {

    @BindView(R.id.myRecyclerView)
    RecyclerView myRecyclerView;

    private List<String> mList = new ArrayList<String>();
    private MyRecyclerAdapter myRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        ButterKnife.bind(this);
        init();
    }

    public void init(){
        for (int i = 0; i<22;i++){
            mList.add("item  "+i);
        }

        myRecyclerAdapter = new MyRecyclerAdapter(this);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
//        //设置布局管理器
//        myRecyclerView.setLayoutManager(linearLayoutManager);
        myRecyclerView.setLayoutManager(new GridLayoutManager(this,4));
//        myRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4,LinearLayoutManager.HORIZONTAL));
        myRecyclerView.setAdapter(myRecyclerAdapter);
        //设置增加或删除条目的动画
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        myRecyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL,R.color.blue,1));
        myRecyclerView.addItemDecoration(new DividerGridItemDecoration(this,R.color.blue,1));
        myRecyclerAdapter.setData(mList);
    }

}
