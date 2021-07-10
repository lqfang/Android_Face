package com.xhkj.facedemo.openglcamera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhkj.facedemo.MainActivity;
import com.xhkj.facedemo.R;
import com.xhkj.facedemo.ShowFaceActivity;
import com.xhkj.facedemo.db.HawkSave;
import com.xhkj.facedemo.module.NameBean;
import com.xhkj.facedemo.utils.AppUtils;
import com.xhkj.facesdk.FaceNative;
import com.xhkj.facesdk.module.QueryInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * created by ThinkPad on 2019/11/21
 * Describe
 */

public class PreviewActivity extends Activity {

    private Activity activity;

    TextView tvTitle;
    ImageView ivBack;

    private FaceNative faceNative;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("认证");
        activity = this;

        faceNative = MainActivity.faceNative;
        // 先判断本地是否有值，若有就取本地的
        if (HawkSave.getInstance().getUserInfo() != null) {
            nameBeanList = HawkSave.getInstance().getUserInfo();
        } else {
            nameBeanList = new ArrayList<>();
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        init();
    }

    private SurfaceView mSurfaceView;

    private CameraOverlap cameraOverlap;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private byte[] mNv21Data;
    private EGLUtils mEglUtils;
    private GLFrame mFrame;
    private GLFramebuffer mFramebuffer;
//    private GLPoints mPoints;
//    private GLBitmap mBitmap;
//    private GLRect mRect;

    private int mWidth = CameraOverlap.PREVIEW_WIDTH;
    private int mHeight = CameraOverlap.PREVIEW_HEIGHT;

    private void init() {
        cameraOverlap = new CameraOverlap(this);
        mNv21Data = new byte[mWidth * mHeight * 2];
        mFramebuffer = new GLFramebuffer();
        mFrame = new GLFrame();

        mHandlerThread = new HandlerThread("DrawFacePointsThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());


        mSurfaceView = findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(final SurfaceHolder holder, int format, final int width, final int height) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mEglUtils != null) {
                            mEglUtils.release();
                        }
                        mEglUtils = new EGLUtils();
                        mEglUtils.initEGL(holder.getSurface());
                        mFramebuffer.initFramebuffer();
                        mFrame.initFrame();
                        mFrame.setSize(width, height, mHeight, mWidth);
                        cameraOverlap.openCamera(mFramebuffer.getSurfaceTexture());
                    }
                });
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        cameraOverlap.release();
                        mFramebuffer.release();
                        mFrame.release();
                        if (mEglUtils != null) {
                            mEglUtils.release();
                            mEglUtils = null;
                        }
                    }
                });
            }
        });
        if (mSurfaceView.getHolder().getSurface() != null && mSurfaceView.getWidth() > 0) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mEglUtils != null) {
                        mEglUtils.release();
                    }
                    mEglUtils = new EGLUtils();
                    mEglUtils.initEGL(mSurfaceView.getHolder().getSurface());
                    mFramebuffer.initFramebuffer();
                    mFrame.initFrame();
                    mFrame.setSize(mSurfaceView.getWidth(), mSurfaceView.getHeight(), mHeight, mWidth);
                    cameraOverlap.openCamera(mFramebuffer.getSurfaceTexture());
                }
            });
        }

        cameraOverlap.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(final byte[] data, final Camera camera) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mEglUtils == null) {
                            return;
                        }
                        // 背景颜色
                        mFrame.setS(1.0f);
                        mFrame.setH(0.0f);
                        mFrame.setL(0.0f);

                        mFrame.drawFrame(0, mFramebuffer.drawFrameBuffer(), mFramebuffer.getMatrix());

                        i = i + 1;
                        // 取3张图片，取分数最高的那张，根据index获取图片名
                        if (i % temp == 0 && count < 3 && isContinue) {
                            //子线程处理图片，防止处理算法时卡顿
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    isContinue = false;
                                    Log.e("isContinue====", "----");
                                    QueryInfo queryInfo = faceNative.queryFace(data, mWidth, mHeight, 270);
                                    if (queryInfo != null && queryInfo.getCode() == 1) {
                                        count = count + 1;
                                        queryInfoList.add(queryInfo);
                                        doQuery();
                                    } else {
                                        // 没有查询到人脸继续查询
                                        isContinue = true;
                                    }
                                }
                            }).start();
                        }

                        mEglUtils.swap();
                    }
                });

            }
        });

    }

    // 阈值
    private double threshold = 0.8;
    // 判断是否是同一个人的最低分
    private double faceScore = 0.5;
    private int i = 0;
    private int temp = 10; // 每10帧取一次头像
    int count = 0; // 统计取预览头像次数，只取3次
    boolean isContinue = true; // 查询到结果后再执行下一次循环

    List<QueryInfo> queryInfoList = new ArrayList<>(); // 保存查询结果
    List<NameBean> nameBeanList; // 注册成功对应的index - name

    /**
     * 查询后的处理（比较三个分值大小，取最大的值，获取对应index,根据index取图片名）
     */
    private void doQuery() {
        Log.e("queryInfoList====", "" + queryInfoList.toString());
        if (queryInfoList == null || queryInfoList.size() == 0) return;

        if (nameBeanList == null || nameBeanList.size() == 0) return;
        Log.e("nameBeanList====", "" + nameBeanList.toString());

        int a = 0;
        float maxScore = queryInfoList.get(0).getScore();
        int index = queryInfoList.get(0).getIndex();

        String name = "";
        int nameIndex = 0;
        for (NameBean bean : nameBeanList) {
            nameIndex = bean.getIndex();
            if (index == nameIndex) {
                name = bean.getName();
            }

            // Todo 第一次认证的分数达到0.8，直接跳转到显示人脸界面
            if (maxScore > threshold) {
                if (index == nameIndex) {
                    isContinue = false;
                    // 取出照片名相同的照片，并显示
                    initDatafoot(name);
                    Log.e("name====0000", "" + name);
                }
            } else {  // 遍历获取最高分值
                // 当获取到查询数据后，再传入下一帧的数据
                isContinue = true;
                // 遍历获取最高分
                for (int i = 0; i < queryInfoList.size(); i++) {
                    QueryInfo queryInfo = queryInfoList.get(i);
                    float score = queryInfo.getScore();
                    if (score > maxScore) {
                        a = i;
                        maxScore = score;
                    }
                    index = queryInfoList.get(a).getIndex();
                }

                if (index == nameIndex && count == 3 && maxScore > faceScore) {
                    name = bean.getName();
                    // 取出照片名相同的照片，并显示
                    initDatafoot(name);
                }

            }

        }
    }

    /**
     * 查找的文件夹中的某个图片
     */
    private void initDatafoot(String name) {
        File folder = getExternalCacheDir();
        /**将文件夹下所有文件名存入数组*/
        String[] allFiles = folder.list();
        if (allFiles.length == 0) {//图片
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AppUtils.showToast(activity, "暂无图片");
                }
            });
        } else if (allFiles.length > 0) {
            for (int i = 0; i < allFiles.length; i++) {
                if (i < 0) {
                    Log.e("i====超出索引", "" + i);
                    return;
                }
                String scanpath = folder + "/" + allFiles[i];
                // 获取文件夹中照片的名称
                String fileName = allFiles[i].substring(0, allFiles[i].indexOf(".jpg"));
                // 认证成功的图片名和文件夹中图片比较，若有，取出
                if (name.equals(fileName)) {
                    ShowFaceActivity.gotoShowFace(activity, name, scanpath);
                    finish();
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(nameBeanList != null){
//            nameBeanList = null;
//        }
//        if(queryInfoList != null){
//            queryInfoList = null;
//        }
    }
}
