package gttrade.guantang.com.tradeerp.permission;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by grove on 2016/9/23.
 */
public class TestPermission extends Activity implements EasyPermissions.PermissionCallbacks {
    public static final String READ_EXTERNAL_STORAGE= Manifest.permission.READ_EXTERNAL_STORAGE;//读取外部存储器
    public static final String WRITE_EXTERNAL_STORAGE= Manifest.permission.WRITE_EXTERNAL_STORAGE;//写入外部存储器
    public static final String CAMERA= Manifest.permission.CAMERA;//相机
    private static final int REQUECT_CODE_SDCARD = 4;//请求码
    private String []permission={WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,CAMERA};
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        //这个界面开启就检查权限，有就执行逻辑，没有就去去那个请求权限
        //这个方法你也可以写在你需要权限操作的事件里
        getPermiss();
    }
    //比如用到调用相机，调用的方法写在getPermiss（）方法里边
    @AfterPermissionGranted(REQUECT_CODE_SDCARD)
    private void getPermiss(){
        //检查是否已经赋予了所有权限
        if (EasyPermissions.hasPermissions(this, permission)) {
            // 成功
            //自己的逻辑（比如调用相机）

        } else {
            //有权限没有被赋予去请求权限
            EasyPermissions.requestPermissions(this, "需要你的相机权限", REQUECT_CODE_SDCARD, permission);
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
