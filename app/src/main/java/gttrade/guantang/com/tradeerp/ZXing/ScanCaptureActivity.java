package gttrade.guantang.com.tradeerp.ZXing;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.ZXing.View.ViewfinderView;
import gttrade.guantang.com.tradeerp.ZXing.camera.CameraManager;
import gttrade.guantang.com.tradeerp.ZXing.decoding.CaptureActivityHandler;
import gttrade.guantang.com.tradeerp.ZXing.decoding.InactivityTimer;
import gttrade.guantang.com.tradeerp.permission.AfterPermissionGranted;
import gttrade.guantang.com.tradeerp.permission.EasyPermissions;

/**
 * Initial the camera
 * @author Ryan.Tang
 */
public class ScanCaptureActivity extends ParentScanActivity implements OnClickListener{

	private ViewfinderView viewfinderView;


	ImageButton lightImgBtn,backBtn;
	Camera.Parameters parameter;
	boolean flag = false;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		backBtn = (ImageButton) findViewById(R.id.back);
		backBtn.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		lightImgBtn = (ImageButton) findViewById(R.id.light);
		lightImgBtn.setOnClickListener(this);
		lightImgBtn.setImageResource(R.mipmap.flash_off);


		decodeFormats = null;
		characterSet = null;


	}


	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}


	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	public void handleDecode(Result obj, Bitmap barcode) {
		inactivityTimer.onActivity();
		// viewfinderView.drawResultBitmap(barcode);
		playBeepSoundAndVibrate();



		Intent i = new Intent();
		i.putExtra("result", obj.getText());
		setResult(1, i);
		finish();
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.light:// 启动摄像头灯光
				Camera camera = CameraManager.get().getCamera();
				if (camera != null) {
					camera.startPreview();
					parameter = camera.getParameters();
					if (flag) {
						parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
						lightImgBtn.setImageResource(R.mipmap.flash_off);
						flag = false;
					} else {
						parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
						lightImgBtn.setImageResource(R.mipmap.flash_on);
						flag = true;
					}
					camera.setParameters(parameter);
				}

				break;
			case R.id.back:
				finish();
				break;
			default:
				break;
		}
	}


}