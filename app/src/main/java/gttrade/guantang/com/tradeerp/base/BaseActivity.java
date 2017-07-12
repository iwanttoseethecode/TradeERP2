package gttrade.guantang.com.tradeerp.base;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.permission.AfterPermissionGranted;
import gttrade.guantang.com.tradeerp.permission.EasyPermissions;
import gttrade.guantang.com.tradeerp.util.ActivityAnimUtil;
import gttrade.guantang.com.tradeerp.util.ScreenMeasure;

public class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    public ProgressDialog mDialog = null;
    private static final int WHAT_DELAY = 1;
    private static final long DELAY = 10000;// 超时关闭dialog;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (WHAT_DELAY == msg.what) {
                dismissDialog();
            }
        };
    };

    //申请的权限集合，6.0以上版本要用到的
    private String[] permission;
    private static final int REQUECT_CODE_SDCARD = 4;//请求码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAnimUtil.setRightIn(this);

//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED){
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){//判断用户上一次是否拒绝过你
//                    //在这里弹框提示用户需要权限的原因，并作处理
//            }else{
//                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
//            }
//        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch(requestCode){
//            case 1:
//
//                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    //权限申请成功的处理
//                }
//
//                break;
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void finish() {
        super.finish();
        ActivityAnimUtil.setRightOut(this);
    }

    public ScreenMeasure getScreenMeasure(){
        ScreenMeasure sm = new ScreenMeasure();
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        sm.width = outMetrics.widthPixels;
        sm.height = outMetrics.heightPixels;
        return sm;
    }

    public void showProgressDialog(String string){
        showProgressDialog(string,true,true);
    }

    /**
     * @param string  文字描述
     * @param cancel  点击是否可以取消dialog
     * @param overtimeClose true 超时关闭dialog，false 不关闭；
     */
    public void showProgressDialog(String string, boolean cancel, final boolean overtimeClose) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this,R.style.yuanjiao_dialog);
        }
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(cancel);
        mDialog.setCanceledOnTouchOutside(cancel);
        mDialog.show();
        mDialog.setContentView(R.layout.progress_dialog_layout);
        TextView msg = (TextView) mDialog.getWindow()
                .findViewById(R.id.message);
        msg.setText(string);
        new Thread(new Runnable() {

            @Override
            public void run() {
                if(overtimeClose){
                    try {
                        Thread.sleep(DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(overtimeClose){
                        mHandler.sendEmptyMessage(WHAT_DELAY);
                    }
                }
            }
        }).start();
    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    //比如用到调用相机，调用的方法写在getPermiss（）方法里边
    @AfterPermissionGranted(REQUECT_CODE_SDCARD)
    public void getPermiss(String[] mPermission){
        permission = mPermission;
        //检查是否已经赋予了所有权限
        if (EasyPermissions.hasPermissions(this, permission)) {
            // 成功
            //自己的逻辑（比如调用相机）

        } else {
            //有权限没有被赋予去请求权限
            EasyPermissions.requestPermissions(this, "现在的操作需要您的运行时权限", REQUECT_CODE_SDCARD, permission);
        }
    }

    //重写onRequestPermissionsResult将值传递给EasyPermissions.onRequestPermissionsResult();
    //固定写法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //请求后的回掉，成功
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //判断请求成功的权限数量和需要请求的权限数量是否相同，相同表示全部权限都请求成功
        if (perms.size()==permission.length){
            //获得或有权限后的逻辑
        }
    }

    //失败
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //这里返回请求失败的权限
        //这里的逻辑可写可不写。请随意。
    }

}
