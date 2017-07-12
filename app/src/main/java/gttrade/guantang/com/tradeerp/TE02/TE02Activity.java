package gttrade.guantang.com.tradeerp.TE02;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE06.TE06Activity;
import gttrade.guantang.com.tradeerp.base.BaseActivity;
import gttrade.guantang.com.tradeerp.service.NetWorkStatusService;

public class TE02Activity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, RadioGroup.OnCheckedChangeListener,TE0201Fragment.Itogglelistener {

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.contentlayout)
    FrameLayout contentlayout;
    @BindView(R.id.nav_view)
    FrameLayout navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.barchartRadionBtn)
    RadioButton barchartRadionBtn;

    Intent serviceIntent;

    private HtmlbackInterface htmlbackInterface;



    public interface HtmlbackInterface {
        /*
        * 返回true 表示html已经退回到顶部了，返回false 表示html还可以继续后退页面
        * */
        boolean htmlbackExecute();
    }

    public void setHtmlbackInterface(HtmlbackInterface htmlbackInterface) {
        this.htmlbackInterface = htmlbackInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te02);
        ButterKnife.bind(this);
        radioGroup.setOnCheckedChangeListener(this);

        serviceIntent = new Intent(getApplicationContext(), NetWorkStatusService.class);
        startService(serviceIntent);

        initLeftFragment();



        createTE0201Fragment();
//        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(serviceIntent);
    }

    public void initLeftFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        LeftFragment leftFragment = LeftFragment.newInstance();
        fragmentTransaction.replace(R.id.nav_view, leftFragment).commit();
    }

    public void init() {


    }

    public void createTE0201Fragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        TE0201Fragment mTE0201Fragment = TE0201Fragment.newInstance();
        fragmentTransaction.replace(R.id.contentlayout, mTE0201Fragment).commit();
    }

    public void createTE0202Fragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        TE0202Fragment mTE0202Fragment = TE0202Fragment.newInstance();
        fragmentTransaction.replace(R.id.contentlayout, mTE0202Fragment).commit();
    }

    public void createTE0203Fragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        TE0203Fragment mTE0203Fragment = TE0203Fragment.newInstance();
        fragmentTransaction.replace(R.id.contentlayout, mTE0203Fragment).commit();
    }

    public void createTE0204Fragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        TE0204Fragment mTE0204Fragment = TE0204Fragment.newInstance();
        fragmentTransaction.replace(R.id.contentlayout, mTE0204Fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void closeNavView() {
        drawerLayout.closeDrawer(navView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.homepageRadioBtn:

                createTE0201Fragment();
//                myControlScrollViewPager.setCurrentItem(0);
                break;
            case R.id.orderRadioBtn:

                createTE0202Fragment();
//                myControlScrollViewPager.setCurrentItem(1);
                break;
            case R.id.goodsRadioBtn:
                createTE0203Fragment();
//                myControlScrollViewPager.setCurrentItem(2);
                break;
            case R.id.barchartRadionBtn:
                createTE0204Fragment();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (barchartRadionBtn.isChecked()) {
                if (htmlbackInterface.htmlbackExecute()){
                    existDialog();
                }
            }else{
                existDialog();
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    public void existDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定要退出程序？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Process.killProcess(Process.myPid());
                System.exit(0);
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void toggleExecute() {
        if (navView.isShown()) {
            drawerLayout.closeDrawer(navView);
        } else {
            drawerLayout.openDrawer(navView);
        }
    }
}
