package com.xhkj.facedemo.camera;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xhkj.facedemo.R;
import com.xhkj.facedemo.RegisterFaceActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * created by ThinkPad on 2019/11/20
 * Describe Camera
 */

public class CameraActivity extends Activity implements View.OnClickListener {

    public static void openCamera(Activity activity) {
        Intent intent = new Intent(activity, CameraActivity.class);
        activity.startActivity(intent);
    }

    private CameraPreview cameraPreview;
    private View containerView;
    private ImageView cropView;
    private ImageView takePhoto;
    private ImageView ivBack;
    private TextView tvTitle;

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        activity = this;

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        cameraPreview = (CameraPreview) findViewById(R.id.camera_surface);
        containerView = findViewById(R.id.camera_crop_container);
        cropView = (ImageView) findViewById(R.id.camera_crop);
        takePhoto = findViewById(R.id.camera_take);

        tvTitle.setText("拍照");

        initView();
    }

    private void initView() {
        //获取屏幕最小边，设置为cameraPreview较窄的一边
        float screenMinSize = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        //根据screenMinSize，计算出cameraPreview的较宽的一边，长宽比为标准的16:9
        float maxSize = screenMinSize / 9.0f * 16.0f;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) screenMinSize, (int) maxSize);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        cameraPreview.setLayoutParams(layoutParams);


        float width = (int) (screenMinSize * 0.8);
        float height = (int) (width * 43.0f / 30.0f) - 100;
//        float height = width - 100;
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) height);
        containerView.setLayoutParams(containerParams);
        LinearLayout.LayoutParams cropParams = new LinearLayout.LayoutParams((int) width, (int) height);
        cropView.setLayoutParams(cropParams);

        ivBack.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        cameraPreview.setOnClickListener(this);

        // 进入相机自动聚焦
        cameraPreview.focus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.camera_surface:
                // 手动触摸聚焦
                cameraPreview.focus();
                break;
            case R.id.camera_take:
                // 点击拍照
                takePhoto();
                break;
        }
    }

    private void takePhoto() {
        cameraPreview.takePhoto(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] data, Camera camera) {
                camera.stopPreview();
                //子线程处理图片，防止ANR
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File originalFile = getOriginalFile();
                            FileOutputStream fos = new FileOutputStream(originalFile);
                            fos.write(data);
                            fos.close();

                            // Todo 拍照完成后： 跳到注册人脸
                            RegisterFaceActivity.gotoRegisterFace(activity, originalFile.getPath());
                            finish();
                            return;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }

    /**
     * @return 拍摄图片原始文件
     * <p>
     * Uri destination = Uri.fromFile(new File(getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME));
     */
    String SAMPLE_CROPPED_IMAGE_NAME = "cropImage_" + System.currentTimeMillis() + ".jpg";

    private File getOriginalFile() {
//        return new File(getExternalCacheDir(), SAMPLE_CROPPED_IMAGE_NAME); // "/storage/emulated/0/Android/data/com.lqfang.facedemo/cache/cropImage_1574391237059.jpg"
        return new File(getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME); // "/data/user/0/com.lqfang.facedemo/cache/cropImage_1574391027043.jpg"
    }


}
