package com.devil.library.media.ui.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.devil.library.media.common.MediaTempListener;

/**
 * 基类
 */
public class DVBaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //防止多重回调释放回调监听
        MediaTempListener.isCanRelease = false;
    }
}
