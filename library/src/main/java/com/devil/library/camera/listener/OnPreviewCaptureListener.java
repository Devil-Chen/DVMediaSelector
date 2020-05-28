package com.devil.library.camera.listener;

import android.support.annotation.IntDef;
import android.support.annotation.RestrictTo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 媒体拍摄回调
 */
public interface OnPreviewCaptureListener {

    int MediaTypePicture = 0;
    int MediaTypeVideo = 1;
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @IntDef(value = {MediaTypePicture, MediaTypeVideo})
    @Retention(RetentionPolicy.SOURCE)
    @interface MediaType {}

    // 拍摄完成
    void onPreviewCapture(String path, @MediaType int type);
}
