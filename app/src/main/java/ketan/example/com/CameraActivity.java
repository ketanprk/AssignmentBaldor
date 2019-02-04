package ketan.example.com.assignmentidfy;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import ketan.example.com.assignmentidfy.widget.CircleOverlayFarView;
import ketan.example.com.assignmentidfy.widget.CircleOverlayNearView;

import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity {


    @BindView(R.id.btnCam)
    Button capture;
    @BindView(R.id.cPreview)
    LinearLayout cameraPreview;
    @BindView(R.id.ll_near)
    CircleOverlayNearView ll_near;
    @BindView(R.id.ll_far)
    CircleOverlayFarView ll_far;

    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private Context myContext;
    private boolean cameraFront = false;
    public static Bitmap bitmap;
    private CircleOverlayNearView ll_overlay;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        capture = (Button) findViewById(R.id.btnCam);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;

        init();


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init() {


        if (getIntent().getStringExtra("SelfieMode").equalsIgnoreCase("FAR")) {
            ll_far.setVisibility(View.VISIBLE);
            ll_near.setVisibility(View.GONE);
        } else {
            ll_near.setVisibility(View.VISIBLE);
            ll_far.setVisibility(View.GONE);
        }

        try {
            releaseCameraAndPreview();
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mCamera.setDisplayOrientation(90);
        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);


        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
            }
        });

        mCamera.startPreview();
    }

    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onResume() {

        super.onResume();
        if (mCamera == null) {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
            Log.d("nu", "null");
        } else {
            Log.d("nu", "no null");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Intent intent = new Intent(CameraActivity.this, PictureActivity.class);
                startActivity(intent);
            }
        };
        return picture;
    }


}
