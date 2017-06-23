package com.zhiyu.fire.util;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;

/**
 * Created by Administrator on 2017/6/20 0020.
 * ImagePicker的设置器
 */

public class ImagePickerSetter {

    private static ImagePicker imagePicker;

    public static void init() {
        if (imagePicker == null) {
            imagePicker = ImagePicker.getInstance();
            imagePicker.setImageLoader(new GlideImageLoader());
            imagePicker.setShowCamera(true);
            imagePicker.setCrop(true);
            imagePicker.setMultiMode(false);
            imagePicker.setFocusWidth(1200);
            imagePicker.setFocusHeight(800);
            imagePicker.setStyle(CropImageView.Style.RECTANGLE);
            imagePicker.setSaveRectangle(true);
            imagePicker.setOutPutX(800);
            imagePicker.setOutPutY(800);
        }
    }

}