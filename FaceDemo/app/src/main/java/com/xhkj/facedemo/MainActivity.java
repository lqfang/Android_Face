package com.xhkj.facedemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhkj.facedemo.R;
import com.xhkj.facedemo.camera.CameraActivity;
import com.xhkj.facedemo.http.ApiModule;
import com.xhkj.facedemo.openglcamera.PreviewActivity;
import com.xhkj.facedemo.utils.AppUtils;
import com.xhkj.facesdk.FaceNative;
import com.xhkj.facesdk.module.IndexCount;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity implements View.OnClickListener {

    Button btnCollect, btnIdent;
    TextView tvTitle, tvName;
    ImageView ivBack, ivImage;

    public static FaceNative faceNative;

    private Activity activity;

    private String[] denied;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        btnCollect = findViewById(R.id.btn_collect);
        btnIdent = findViewById(R.id.btn_ident);
        ivImage = findViewById(R.id.iv_image);

        ivBack.setVisibility(View.GONE);
        tvTitle.setText("首页");

        btnCollect.setOnClickListener(this);
        btnIdent.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }else {
            init();
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ArrayList<String> list = new ArrayList<>();
//            for (int i = 0; i < requiredPermissions.length; i++) {
//                if (PermissionChecker.checkSelfPermission(this, requiredPermissions[i]) == PackageManager.PERMISSION_DENIED) {
//                    list.add(requiredPermissions[i]);
//                }
//            }
//            if (list.size() != 0) {
//                denied = new String[list.size()];
//                for (int i = 0; i < list.size(); i++) {
//                    denied[i] = list.get(i);
//                }
//                ActivityCompat.requestPermissions(this, denied, PERMISSIONS_REQUEST_CODE);
//            } else {
//                init();
//            }
//        } else {
//            init();
//        }
    }

    private void init() {
        // 产品码
        String productCode = "xhkj_face";
        // 设备Id
        String androidId;
        // 当SDK版本大于等于8时,必须添加获取手机状态的动态权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            androidId = Build.getSerial();
        } else {
            androidId = Build.SERIAL;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("tag", "activeCode====" + activeCode);
                Log.e("tag", "initCode====" + initCode);
//                Log.e("tag", "androidId====" + androidId);
                faceNative = new FaceNative(activity);
                // 不用每次进去此页面都激活和初始化
                if (activeCode != 1) {
                    activeCode = faceNative.active(faceNative.modelDir, productCode, androidId);
                }
                if (initCode != 1 || initCode != 2) {
                    initCode = faceNative.init(faceNative.modelDir, faceNative.dbDir);
                }

                IndexCount count = faceNative.count();
                Log.e("IndexCount====", "" + count);

            }
        }).start();
    }

    int activeCode = 0;
    int initCode = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_collect:
                skipActivity(1);
                break;
            case R.id.btn_ident:
                skipActivity(2);
                break;
        }
    }

    /**
     * 跳转：1. 拍照采集， 2. 预览认证
     */
    private void skipActivity(int type) {
        if (type == 1) {
            CameraActivity.openCamera(this);
        } else {
            startActivity(new Intent(this, PreviewActivity.class));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        faceNative.destroy();
    }



    /**
     * add  permissions at Runtime.
     */
    private static final String[] requiredPermissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_PHONE_STATE,
            };
    private static final int PERMISSIONS_REQUEST_ACCESS_CODE = 1;
    private static final int PERMISSIONS_REQUEST_CODE = 2;

    /**
     * checking  permissions at Runtime.
     */
    private void checkPermissions() {
        final List<String> neededPermissions = new ArrayList<>();
        for (final String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }
        if (!neededPermissions.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(neededPermissions.toArray(new String[]{}),
                        PERMISSIONS_REQUEST_ACCESS_CODE);
                return;
            }
        }else {
            init();
            Log.e("tag", "checkPermissions333====");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_CODE:
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkPermissions();
                }
                init();
                break;
            case PERMISSIONS_REQUEST_CODE:
                boolean isDenied = false;
                for (int i = 0; i < denied.length; i++) {
                    String permission = denied[i];
                    for (int j = 0; j < permissions.length; j++) {
                        if (permissions[j].equals(permission)) {
                            if (grantResults[j] != PackageManager.PERMISSION_GRANTED) {
                                isDenied = true;
                                break;
                            }
                        }
                    }
                }
                // 查看添加的权限是否是动态权限
                if (isDenied) {
                    AppUtils.showToast(activity, "请开启权限");
                } else {
                    init();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getRequest() {
        JSONObject json = new JSONObject();

        String sJson = json.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), sJson);
        ApiModule.getInstance().getApiService().getResponse().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("tag", " ===response====>:" + response);
                String result = doJson(response.body());
                Log.e("tag", " ===result====>:" + result);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("tag", " ===t====>:" + t.getMessage());
            }
        });
    }

    /**
     * ResponseBody 处理成Json
     */
    private String doJson(ResponseBody responseBody) {
        long contentLength = responseBody.contentLength();
        BufferedSource source = responseBody.source();
        try {
            source.request(Long.MAX_VALUE); // Buffer the entire body.
        } catch (IOException e) {
            e.printStackTrace();
        }
        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8);
            } catch (UnsupportedCharsetException e) {
                e.printStackTrace();
            }
        }
        String result = "";
        // 拦截器，
        if (contentLength != 0) {
            result = buffer.clone().readString(charset);
//            Log.e("MainActivity", " doJson====>:" + result);
        }
        return result;
    }

    private static final Charset UTF8 = Charset.forName("UTF-8");


}
