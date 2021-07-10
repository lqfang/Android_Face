package com.xhkj.facedemo.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * created by ThinkPad on 2019/11/20
 * Describe 自定义相机预览
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private Camera camera;
    private int cameraId = 1; // (前置摄像头：1， 后置摄像头：0)
    public static final int PREVIEW_WIDTH = 480;
    public static final int PREVIEW_HEIGHT = 270;

    public CameraPreview(Context context) {
        super(context);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = CameraUtils.openCamera(cameraId);
        if (camera != null) {
            try {
                camera.setPreviewDisplay(holder);
                Camera.Parameters parameters = camera.getParameters();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // 竖屏拍照时，需要设置旋转90，否则相机预览方向和界面方向不同
                    camera.setDisplayOrientation(90);
                    // Todo 设置相机采集照片的角度（后置摄像头角度用90，前置摄像头角度用270）
                    parameters.setRotation(270);
                } else {
                    camera.setDisplayOrientation(0);
                    parameters.setRotation(0);
                }

                Camera.Size size = getSize(parameters.getSupportedPreviewSizes());
                if (size != null) {
                    parameters.setPreviewSize(size.width, size.height);
                    parameters.setPictureSize(size.width, size.height);

//                    parameters.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
//                    parameters.setPictureSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
                } else {
                    parameters.setPreviewSize(1920, 1080);
                    parameters.setPictureSize(1920, 1080);
                }

                camera.setParameters(parameters);
                camera.startPreview();
                focus();
            } catch (IOException e) {
                try {
                    Camera.Parameters parameters = camera.getParameters();
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        //竖屏拍照时，需要设置旋转90度，否者看到的相机预览方向和界面方向不相同
                        camera.setDisplayOrientation(90);
                        // Todo 后置摄像头角度用90，前置摄像头角度用270
                        parameters.setRotation(270);
                    } else {
                        camera.setDisplayOrientation(0);
                        parameters.setRotation(0);
                    }
                    camera.setParameters(parameters);
                    camera.startPreview();
                    focus();
                } catch (Exception e1) {
                    e.printStackTrace();
                    camera = null;
                }
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //因为设置了固定屏幕方向，所以在实际使用中不会触发这个方法
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 释放资源
        release();
    }

    /**
     * Android相机的预览尺寸都是4:3或者16:9，这里遍历所有支持的预览尺寸，得到16:9的最大尺寸，保证成像清晰度
     *
     * @param sizes
     * @return 最佳尺寸
     */
    private Camera.Size getSize(List<Camera.Size> sizes) {
        Camera.Size bestSize = null;
        for (Camera.Size size : sizes) {
//            Log.e("tag", "width====="+ size.width + ", height=="+ size.height);
            if ((float) size.width / (float) size.height == 16.0f / 9.0f) {
                if (bestSize == null) {
                    bestSize = size;
                } else {
                    if (size.width > bestSize.width) {
                        bestSize = size;
                    }
                }
            }
        }
        return bestSize;
    }

    /**
     * 释放资源
     */
    private void release() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    /**
     * 对焦，触摸对焦
     */
    public void focus() {
        if (camera != null) {
            camera.autoFocus(null);
        }
    }

    /**
     * 开关闪光灯
     *
     * @return 闪光灯是否开启
     */
    public boolean flashLight() {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)){
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                return true;
            }else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                return false;
            }
        }
        return false;
    }

    /**
     * 拍摄照片
     *
     * @param pictureCallback 在pictureCallback处理拍照回调
     */
    public void takePhoto(Camera.PictureCallback pictureCallback){
        if(camera != null){
            camera.takePicture(null, null, pictureCallback);
        }
    }

    public void startPreview(){
        if(camera != null){
            camera.startPreview();
        }
    }


}
