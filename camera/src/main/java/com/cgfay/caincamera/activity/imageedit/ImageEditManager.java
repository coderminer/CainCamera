package com.cgfay.caincamera.activity.imageedit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cgfay.cainfilter.ImageFilter.ImageFilter;
import com.cgfay.cainfilter.ImageFilter.NativeFilter;
import com.cgfay.cainfilter.type.ImageFilterType;
import com.cgfay.utilslibrary.BitmapUtils;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * 图片编辑管理器
 * Created by cain on 2017/11/15.
 */

public final class ImageEditManager {

    private static final String TAG = "ImageEditManager";

    private final WeakReference<Context> mWeakContext;
    private final WeakReference<ImageView> mWeakImageView;

    // 原图像
    private Bitmap mSourceBitmap;
    // 当前图像
    private Bitmap mCurrentBitmap;
    // 图像路径
    private String mImagePath;

    private Handler mEditHandler;
    private HandlerThread mEditHandlerThread;

    private Handler mMainHandler;

    private int mScreenWidth;
    private int mScreenHeight;

    public ImageEditManager(Context context, String imagePath, ImageView imageView) {
        mWeakContext = new WeakReference<Context>(context);
        mWeakImageView = new WeakReference<ImageView>(imageView);
        mImagePath = imagePath;
        mMainHandler = new Handler(Looper.getMainLooper());
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();
    }

    /**
     * 开始图片编辑线程
     */
    public void startImageEditThread() {
        mEditHandlerThread = new HandlerThread("Image Edit Thread");
        mEditHandlerThread.start();
        mEditHandler = new Handler(mEditHandlerThread.getLooper());
    }

    /**
     * 停止图片编辑线程
     */
    public void stopImageEditThread() {
        if (mEditHandler != null) {
            mEditHandler.removeCallbacksAndMessages(null);
        }
        if (mEditHandlerThread != null) {
            mEditHandlerThread.quitSafely();
        }
        try {
            mEditHandlerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mEditHandlerThread = null;
        mEditHandler = null;
    }


    /**
     * 释放持有的资源
     */
    public void release() {
        mWeakContext.clear();
        mWeakImageView.clear();
        if (mSourceBitmap != null && !mSourceBitmap.isRecycled()) {
            mSourceBitmap.recycle();
            mSourceBitmap = null;
        }
        if (mCurrentBitmap != null && !mCurrentBitmap.isRecycled()) {
            mCurrentBitmap.recycle();
            mCurrentBitmap = null;
        }
    }


    /**
     * 显示图片
     */
    public void setSourceImage() {
        if (mEditHandler != null) {
            mEditHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSourceBitmap = BitmapUtils.getBitmapFromFile(new File(mImagePath),
                            mScreenWidth, mScreenHeight);
                    mCurrentBitmap = Bitmap.createBitmap(mSourceBitmap.getWidth(),
                            mSourceBitmap.getHeight(), mSourceBitmap.getConfig());
                    setImageBitmap(mCurrentBitmap);
                }
            });
        }
    }

    /**
     * 设置图片
     * @param bitmap
     */
    private void setImageBitmap(final Bitmap bitmap) {
        if (mMainHandler != null && mWeakImageView.get() != null) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mWeakImageView.get().setImageBitmap(bitmap);
                }
            });
        }
    }

    /**
     * 切换滤镜
     * @param type
     */
    public void changeFilter(final ImageFilterType type) {
        if (mEditHandler != null) {
            mEditHandler.post(new Runnable() {
                @Override
                public void run() {
                    ImageFilter.getInstance().changeFilter(type, mSourceBitmap, mCurrentBitmap);
                    setImageBitmap(mCurrentBitmap);
                }
            });
        }
    }

    /**
     * 设置亮度 -255 ~ 255
     * @param brightness
     */
    public void setBrightness(final float brightness) {
        if (mEditHandler != null) {
            mEditHandler.post(new Runnable() {
                @Override
                public void run() {
                    ImageFilter.getInstance().setBrightness(brightness, mSourceBitmap, mCurrentBitmap);
                    setImageBitmap(mCurrentBitmap);
                }
            });
        }
    }

    /**
     * 设置对比度
     * @param contrast
     */
    public void setContrast(final float contrast) {
        if (mEditHandler != null) {
            mEditHandler.post(new Runnable() {
                @Override
                public void run() {
                    ImageFilter.getInstance().setContrast(contrast, mSourceBitmap, mCurrentBitmap);
                    setImageBitmap(mCurrentBitmap);
                }
            });
        }
    }

    /**
     * 设置曝光
     * @param exposure
     */
    public void setExposure(final float exposure) {
        if (mEditHandler != null) {
            mEditHandler.post(new Runnable() {
                @Override
                public void run() {
                    ImageFilter.getInstance().setExposure(exposure, mSourceBitmap, mCurrentBitmap);
                    setImageBitmap(mCurrentBitmap);
                }
            });
        }
    }

    /**
     * 设置色调 0 ~ 360度
     * @param hue
     */
    public void setHue(final float hue) {
        if (mEditHandler != null) {
            mEditHandler.post(new Runnable() {
                @Override
                public void run() {
                    ImageFilter.getInstance().setHue(hue, mSourceBitmap, mCurrentBitmap);
                    setImageBitmap(mCurrentBitmap);
                }
            });
        }
    }

    /**
     * 设置饱和度 0.0 ~ 2.0之间
     * @param saturation
     */
    public void setSaturation(final float saturation) {
        if (mEditHandler != null) {
            mEditHandler.post(new Runnable() {
                @Override
                public void run() {
                    ImageFilter.getInstance().setSaturation(saturation, mSourceBitmap, mCurrentBitmap);
                    setImageBitmap(mCurrentBitmap);
                }
            });
        }
    }

    /**
     * 设置锐度
     * @param sharpness
     */
    public void setSharpness(final float sharpness) {
        if (mEditHandler != null) {
            mEditHandler.post(new Runnable() {
                @Override
                public void run() {
                    ImageFilter.getInstance().setSharpness(sharpness, mSourceBitmap, mCurrentBitmap);
                    setImageBitmap(mCurrentBitmap);
                }
            });
        }
    }

}
