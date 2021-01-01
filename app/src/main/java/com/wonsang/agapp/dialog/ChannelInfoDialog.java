package com.wonsang.agapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.wonsang.agapp.R;
import com.wonsang.agapp.model.YoutubeData;

public class ChannelInfoDialog extends Dialog {

    private YoutubeData youtubeData;

    public ChannelInfoDialog(@NonNull Context context, YoutubeData youtubeData) {
        super(context);
        this.youtubeData = youtubeData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setWindowAsBehind();
        setContentView(R.layout.channel_info_dialog);
    }

    private void setWindowAsBehind() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);
    }
}

