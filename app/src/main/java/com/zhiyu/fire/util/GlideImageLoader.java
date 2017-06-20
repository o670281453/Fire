package com.zhiyu.fire.util;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.imagepicker.loader.ImageLoader;
import com.zhiyu.fire.R;

import java.io.File;

/**
 * Created by Administrator on 2017/6/9 0009.
 * 给ImagePicker用的图片加载类
 */

public class GlideImageLoader implements ImageLoader {

    private static final long serialVersionUID = 1L;

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        Glide.with(activity)
                .load(Uri.fromFile(new File(path)))
                .dontAnimate()
                .error(R.mipmap.default_image)
                .placeholder(R.mipmap.default_image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {}

}