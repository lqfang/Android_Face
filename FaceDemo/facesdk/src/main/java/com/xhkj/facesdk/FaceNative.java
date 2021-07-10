package com.xhkj.facesdk;

import android.content.Context;
import android.graphics.Bitmap;

import com.xhkj.facesdk.module.IndexCount;
import com.xhkj.facesdk.module.QueryInfo;
import com.xhkj.facesdk.module.RegisterInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * created by ThinkPad on 2019/11/25
 * Describe
 */

public class FaceNative {

    static {
        System.loadLibrary("JNIFace");
    }

    public String modelDir;
    public String dbDir;
    String name;
    String modelPath;

    // Todo 测试版modelDir传"",需要联网, 永久版传model（打包aar时确定）
    boolean isRelease = true;

    public FaceNative(Context context) {
        if (isRelease) {
            // 指定模型存放本地的文件夹
            modelDir = context.getExternalFilesDir("model").getPath();
        } else {
            modelDir = "";
        }

        dbDir = context.getExternalFilesDir("db").getPath();
        getFDPath(context);
        getFLPath(context);
        getFRPath(context);
    }

    public native int active(String modelDir, String productCode, String androidId);

    public native int init(String modelDir, String dbDir);

    public native RegisterInfo registerFace(Bitmap bitmap);

    public native QueryInfo query(Bitmap bitmap);

    public native QueryInfo queryFace(byte[] data, int width, int height, int degree);

    public native int deleteInfo(int index);

    public native IndexCount count();

    public native int destroy();

    /**
     * 获取FD的路径
     */
    public String getFDPath(Context context) {
        name = "fd_2_00.dat";
        modelPath = modelDir + "/" + name;
        boolean isExists = fileIsExists(modelPath);
        String fdModel;
        if (isExists) {
            fdModel = modelPath;
        } else {
            fdModel = getPath(context, modelDir, name) + "/" + name;
        }
        return fdModel;
    }

    /**
     * 获取FL的路径
     * 81点的模型：pd_2_00_pts81.dat
     * 5点的模型：pd_2_00_pts5.dat
     */
    public String getFLPath(Context context) {
        name = "pd_2_00_pts5.dat";
        modelPath = modelDir + "/" + name;
        boolean isExists = fileIsExists(modelPath);
        String flModel;
        if (isExists) {
            flModel = modelPath;
        } else {
            flModel = getPath(context, modelDir, name) + "/" + name;
        }
        return flModel;
    }

    /**
     * 获取FR的路径
     */
    public String getFRPath(Context context) {
        name = "fr_2_10.dat";
        modelPath = modelDir + "/" + name;
        boolean isExists = fileIsExists(modelPath);
        String frModel;
        if (isExists) {
            frModel = modelPath;
        } else {
            frModel = getPath(context, modelDir, name) + "/" + name;
        }
        return frModel;
    }

    /**
     * 判断文件是否存在
     */
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * assets中资源存入本地文件夹
     */
    public String getPath(Context context, String filePath, String fileName) {
        byte[] mData = new byte[0];
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            //缓冲
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                //缓冲  bytes:写多少  0 从哪开始写   len 内容
                baos.write(bytes, 0, len);
            }
            mData = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (filePath == null) return null;

        File mFile = new File(filePath);
        if (!mFile.exists()) {
            mFile.mkdir();//创建文件夹
        }
        //存 SD, 获取路径
        try {
            FileOutputStream fos = new FileOutputStream(new File(mFile, fileName));
            fos.write(mData, 0, mData.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String modelPath = String.valueOf(mFile);
        return modelPath;
    }
}
