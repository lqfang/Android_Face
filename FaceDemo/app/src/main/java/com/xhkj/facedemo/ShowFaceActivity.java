package com.xhkj.facedemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhkj.facedemo.R;

/**
 * created by ThinkPad on 2019/11/27
 * Describe 显示认证成功的注册人脸
 */

public class ShowFaceActivity extends Activity {

    private static final String PHOTO = "photo_path";
    private static final String NAME = "name";

    private ImageView ivBack, ivShow;
    private TextView tvTitle, tvName;

    private Activity activity;

    public static void gotoShowFace(Activity activity, String name, String path) {
        Intent intent = new Intent(activity, ShowFaceActivity.class);
        intent.putExtra(NAME, name);
        intent.putExtra(PHOTO, path);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_face);
        activity = this;
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        ivShow = findViewById(R.id.iv_show);
        tvName = findViewById(R.id.tv_name);

        tvTitle.setText("显示人脸");

        String name = getIntent().getStringExtra(NAME);
        String photoPath = getIntent().getStringExtra(PHOTO);

        tvName.setText("姓名：" + name);

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        if(bitmap == null) return;
        ivShow.setImageBitmap(bitmap);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(activity, MainActivity.class);
//                startActivity(intent);
                finish();
            }
        });
    }
}
