package com.casc.pmtools.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.casc.pmtools.R;
import com.casc.pmtools.message.DecodeResultMessage;
import com.casc.pmtools.utils.ActivityCollector;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DecoderActivity extends BaseActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private static final String TAG = DecoderActivity.class.getSimpleName();

    public static void actionStart(Context context) {
        if (!(ActivityCollector.getTopActivity() instanceof DecoderActivity)) {
            Intent intent = new Intent(context, DecoderActivity.class);
            context.startActivity(intent);
        }
    }

    private boolean mIsResultPosted;

    @BindView(R.id.qr_decoder) QRCodeReaderView mQRDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoder);
        ButterKnife.bind(this);

        mQRDecoder.setOnQRCodeReadListener(this);
        mQRDecoder.setQRDecodingEnabled(true);
        mQRDecoder.setAutofocusInterval(500L);
        mQRDecoder.setTorchEnabled(true);
    }

    @Override
    public synchronized void onQRCodeRead(String text, PointF[] points) {
        if (!mIsResultPosted && !TextUtils.isEmpty(text)) {
            mIsResultPosted = true;
            EventBus.getDefault().post(new DecodeResultMessage(text));
            finish();
        }
    }
}
