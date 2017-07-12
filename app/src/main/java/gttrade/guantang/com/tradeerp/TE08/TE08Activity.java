package gttrade.guantang.com.tradeerp.TE08;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE01.TE01Activity;

public class TE08Activity extends AppCompatActivity {

    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.point1)
    ImageView point1;
    @BindView(R.id.point2)
    ImageView point2;
    @BindView(R.id.point3)
    ImageView point3;
    @BindView(R.id.point4)
    ImageView point4;
    @BindView(R.id.pagerpointlayout)
    LinearLayout pagerpointlayout;

    public List<View> viewList = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te08);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pager.removeOnPageChangeListener(onPageChangeListener);
    }

    public void init(){
        pager.addOnPageChangeListener(onPageChangeListener);

        LayoutInflater inflater = LayoutInflater.from(this);
        ViewGroup viewGroup1 = (ViewGroup) inflater.inflate(R.layout.imageviewlayout, null);
        viewGroup1.setBackgroundResource(R.mipmap.navigation1);
        viewList.add(viewGroup1);

        ViewGroup viewGroup2 = (ViewGroup) inflater.inflate(R.layout.imageviewlayout, null);
        viewGroup2.setBackgroundResource(R.mipmap.navigation2);
        viewList.add(viewGroup2);

        ViewGroup viewGroup3 = (ViewGroup) inflater.inflate(R.layout.imageviewlayout, null);
        viewGroup3.setBackgroundResource(R.mipmap.navigation3);
        viewList.add(viewGroup3);

        ViewGroup viewGroup4 = (ViewGroup) inflater.inflate(R.layout.imageviewlayout, null);
        viewGroup4.setBackgroundResource(R.mipmap.navigation4);
        TextView imgButton = (TextView) viewGroup4.findViewById(R.id.imgbtn);
        imgButton.setBackgroundResource(R.mipmap.usebtn);
        imgButton.setVisibility(View.VISIBLE);
        imgButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                Intent intent=new Intent(TE08Activity.this,TE01Activity.class);
                startActivity(intent);
                finish();
            }
        });
        viewList.add(viewGroup4);

        ViewpagerAdapter mViewpagerAdapter = new ViewpagerAdapter();
        pager.setAdapter(mViewpagerAdapter);
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch(position){
                case 0:
//			pagerpointlayout.setVisibility(View.VISIBLE);
                    point1.setImageResource(R.mipmap.point_new);
                    point2.setImageResource(R.mipmap.point);
                    point3.setImageResource(R.mipmap.point);
                    point4.setImageResource(R.mipmap.point);
                    break;
                case 1:
//			pagerpointlayout.setVisibility(View.VISIBLE);
                    point1.setImageResource(R.mipmap.point);
                    point2.setImageResource(R.mipmap.point_new);
                    point3.setImageResource(R.mipmap.point);
                    point4.setImageResource(R.mipmap.point);
                    break;
                case 2:
//			pagerpointlayout.setVisibility(View.GONE);
                    point1.setImageResource(R.mipmap.point);
                    point2.setImageResource(R.mipmap.point);
                    point3.setImageResource(R.mipmap.point_new);
                    point4.setImageResource(R.mipmap.point);
                    break;
                case 3:
//			pagerpointlayout.setVisibility(View.GONE);
                    point1.setImageResource(R.mipmap.point);
                    point2.setImageResource(R.mipmap.point);
                    point3.setImageResource(R.mipmap.point);
                    point4.setImageResource(R.mipmap.point_new);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    class ViewpagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
