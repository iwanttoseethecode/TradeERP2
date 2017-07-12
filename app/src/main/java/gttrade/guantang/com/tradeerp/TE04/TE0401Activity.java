package gttrade.guantang.com.tradeerp.TE04;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.base.BaseActivity;
import gttrade.guantang.com.tradeerp.util.PagerSlidingTabStrip;

public class TE0401Activity extends BaseActivity {

    @BindView(R.id.back)
    ImageButton back;
    @BindView(R.id.titleTxtView)
    TextView titleTxtView;
    @BindView(R.id.titlelayout)
    LinearLayout titlelayout;
    @BindView(R.id.myPagerSlidingTabStrip)
    PagerSlidingTabStrip myPagerSlidingTabStrip;
    @BindView(R.id.ViewPagerContent)
    ViewPager ViewPagerContent;

    private String orderid;

    private List<String> titleList = new ArrayList<String>();
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te0401);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        orderid = intent.getStringExtra("orderid");

        init();
    }

    public void init(){
        titleList.add("订单信息");
        titleList.add("收件人信息");
        titleList.add("货品信息");

        fragmentList.add(TE040101Fragment.newInstance(orderid));
        fragmentList.add(TE040102Fragment.newInstance(orderid));
        fragmentList.add(new TE040103Fragment().newInstance(orderid));

        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        ViewPagerContent.setAdapter(myPagerAdapter);
        ViewPagerContent.setOffscreenPageLimit(3);
        myPagerSlidingTabStrip.setViewPager(ViewPagerContent);

        setPageTitlesValue();
    }

    /**
     * 设置Titile相关属性
     */
    private void setPageTitlesValue() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        // 设置为true 均匀分配title位置
        myPagerSlidingTabStrip.setShouldExpand(true);

        myPagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);

        //(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm)  将数值1转成dp为单位
        //设置下划分割线高度
        myPagerSlidingTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm));

        //设置指示条高条
        myPagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));

        //设置文本字大小
        myPagerSlidingTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, dm));

        //设置指示条颜色
        myPagerSlidingTabStrip.setIndicatorColor(Color.parseColor("#ff7070"));

        //设置选中文本颜色
        myPagerSlidingTabStrip.setSelectedTextColor(Color.parseColor("#ff7070"));

        //设置Title背景颜色
        myPagerSlidingTabStrip.setTabBackground(0);//android.R.color.darker_gray
    }

    @OnClick(R.id.back)
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }

    class MyPagerAdapter extends FragmentPagerAdapter{

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }

}
