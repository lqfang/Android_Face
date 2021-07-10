package com.xhkj.facedemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhkj.facedemo.R;
import com.xhkj.facedemo.db.HawkSave;
import com.xhkj.facedemo.module.NameBean;
import com.xhkj.facedemo.utils.AppUtils;
import com.xhkj.facesdk.FaceNative;
import com.xhkj.facesdk.module.IndexCount;
import com.xhkj.facesdk.module.RegisterInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;

/**
 * created by ThinkPad on 2019/11/22
 * Describe 注册人脸（录入姓名作为保存图片的名称，确定后录入成功）
 */

public class RegisterFaceActivity extends Activity implements View.OnClickListener {

    private static final String PHOTO_PATH = "photo_path";

    private ImageView ivBack, ivPhoto;
    private TextView tvTitle;
    private EditText etName;
    private Button btnOK;

    private Bitmap bitmap;
    private String name;
    private Activity activity;

    private FaceNative faceNative;

    public static void gotoRegisterFace(Activity activity, String path) {
        Intent intent = new Intent(activity, RegisterFaceActivity.class);
        intent.putExtra(PHOTO_PATH, path);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_face);
        activity = this;

        faceNative = MainActivity.faceNative;

        ivBack = findViewById(R.id.iv_back);
        ivPhoto = findViewById(R.id.iv_photo);
        tvTitle = findViewById(R.id.tv_title);
        etName = findViewById(R.id.et_name);
        btnOK = findViewById(R.id.btn_ok);

        tvTitle.setText("注册人脸");

        ivBack.setOnClickListener(this);
        btnOK.setOnClickListener(this);

        initData();
    }

    private void initData() {
        String path = getIntent().getStringExtra(PHOTO_PATH);
        bitmap = BitmapFactory.decodeFile(path);
        if (!TextUtils.isEmpty(path)) {
            ivPhoto.setImageBitmap(bitmap);
        }
    }

    /**
     * 保存相片到本地（先判断文件夹是否有同名的文件，若有存：姓名+1，注册成功后保存返回的index值和对应的文件名）
     */
    private void savePhoto() {
        name = etName.getText().toString().trim() + ".jpg";
        if (TextUtils.isEmpty(name)) {
            AppUtils.showToast(activity, "姓名不能为空！");
            return;
        }
        // 子线程中处理耗时操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 判断是否有同名的文件
                name = setFileReleaseNames(name);
                try {
                    File file = new File(getExternalCacheDir(), name);
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    // 保存本地成功后，注册人脸
                    register(file.getPath());
                    // 1:1人脸注册
//                    if(MyApp.list != null && MyApp.list.size() > 0){
//                        int code = faceNative.deleteInfo(MyApp.list.get(0).getIndex());
//                        Log.e("deleteInfo====", "" + code);
//                        if(code == 1 ){
//                            // 第二次之后，先删除之前注册的，成功后再走注册接口
//                            register(file.getPath());
//                        }
//                    }else {
//                        // 第一次注册
//                        register(file.getPath());
//                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    // 文件最终名字
    private String fileReleaseName;

    /**
     * 判断文件夹中是否有同名的文件（传入文件默认名）
     **/
    private String setFileReleaseNames(String mFileName) {
        String fileName = mFileName.substring(0, mFileName.indexOf(".jpg"));
        File f = getExternalCacheDir();
        if (f.exists()) {//判断路径是否存在
            File[] files = f.listFiles();
            HashSet<String> hashSet = new HashSet<>();
            for (File file : files) {
                if (file.isFile()) {
                    String name = file.getName();
                    hashSet.add(name);
                }
            }
            int a = 0;
            while (true) {
                if (a != 0) {
                    String[] split = mFileName.split("\\.");
//                     mFileName = split[0] + "(" + a + ")." + split[1];
                    mFileName = fileName + "(" + a + ")." + split[1];
                }
                if (!hashSet.contains(mFileName)) {
                    fileReleaseName = mFileName;
                    break;
                } else {
                    a++;
                }
            }
        }
        return fileReleaseName;
    }

    /**
     * 注册人脸
     */
    private void register(String path) {
        //图片路径转Bitmap
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        final Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        RegisterInfo registerInfo = faceNative.registerFace(bitmap);
        Log.e("registerInfo====", "" + registerInfo);

        if (registerInfo.getCode() == 1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AppUtils.showToast(activity, "注册成功");
                }
            });

            int index = registerInfo.getIndex();
            String name = fileReleaseName.substring(0, fileReleaseName.length() - 4);

            NameBean bean = new NameBean();
            bean.setIndex(index);
            bean.setName(name);
            MyApp.list.add(bean);
            HawkSave.getInstance().saveUserInfo(MyApp.list);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AppUtils.showToast(activity, "注册失败" + registerInfo.getCode());
                }
            });
        }
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_ok:
                savePhoto();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (faceNative != null) {
            faceNative = null;
        }
        if (bitmap != null) {
            bitmap = null;
        }
    }
}
