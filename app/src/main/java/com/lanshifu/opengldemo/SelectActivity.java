package com.lanshifu.opengldemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lanshifu.opengldemo.camera.Camera2Activity;
import com.lanshifu.opengldemo.camera.Camera2Demo_TextureView_Activity;
import com.lanshifu.opengldemo.camera.camera2_surface_demo.Camera2Demo_SurfaceView_Activity;
import com.lanshifu.opengldemo.image.ImageFilterActivity;

import java.io.IOException;

public class SelectActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SelectActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        findViewById(R.id.btn_trangle01).setOnClickListener(this);
        findViewById(R.id.btn_gesture).setOnClickListener(this);
        findViewById(R.id.btn_filter).setOnClickListener(this);
        findViewById(R.id.btn_vary).setOnClickListener(this);
        findViewById(R.id.btn_camera).setOnClickListener(this);
        findViewById(R.id.btn_camera_demo).setOnClickListener(this);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("picture.png"));
            ((ImageView)findViewById(R.id.iv_01)).setImageBitmap(bitmap);
        } catch (IOException e) {

            Log.e(TAG, "onCreate: " + e.getMessage());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_trangle01:
                MainActivity.start(this,MainActivity.TYPE_TRANGLE);
                break;
            case R.id.btn_gesture:
                MainActivity.start(this,MainActivity.TYPE_GESTURE);
                break;
            case R.id.btn_filter:
                startActivity(new Intent(SelectActivity.this,ImageFilterActivity.class));
                break;
            case R.id.btn_vary:
                startActivity(new Intent(SelectActivity.this,VaryActivity.class));
                break;
          case R.id.btn_camera_demo:
                startActivity(new Intent(SelectActivity.this, Camera2Demo_SurfaceView_Activity.class));
                break;
          case R.id.btn_camera:
                startActivity(new Intent(SelectActivity.this,Camera2Activity.class));
                break;


        }
    }
}
