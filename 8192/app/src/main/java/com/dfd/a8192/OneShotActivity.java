package com.dfd.a8192;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by a61-201405-2055 on 16/06/24.
 */
public class OneShotActivity extends Activity implements View.OnClickListener {

    private CameraManager mCameraManager;
    private String mCameraID = null;
    private boolean isOn = true;
    private Timer mainTimer;
    private MainTimerTask mainTimerTask;
    private boolean imageOn = true;
    private Handler mHandler = new Handler();
    private AudioAttributes audioAttributes;
    private SoundPool soundpool;
    private int hitSound;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oneshot_layout);

        final Button pushBtn = (Button)findViewById(R.id.pushbtn);
        pushBtn.setOnClickListener(this);

        //ライトの管理
        mCameraManager = ((CameraManager) getSystemService(Context.CAMERA_SERVICE));
        mCameraManager.registerTorchCallback(new CameraManager.TorchCallback() {
            @Override
            public void onTorchModeChanged(String cameraId, boolean enabled) {
                mCameraID = cameraId;
            }
        }, new Handler());

        audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build();
        soundpool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(1).build();

        //ボタン非活性
        pushBtn.setEnabled(false);

        //BGM事前ロード
        hitSound = soundpool.load(this, R.raw.hitsound, 0);
        soundpool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                // 音声ロードが終わったためボタンを活性
                pushBtn.setEnabled(true);
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void onClick (View view) {
        double randomNumber = (int) (Math.random() * 8192) + 1;
        randomNumber = randomNumber / 1000;
        Log.d("randomNumber", String.valueOf(randomNumber));
        if (view.getId() == R.id.pushbtn) {
            if (randomNumber >= 7 && randomNumber < 8) {
                Log.d("Hit", "hit");
                //音楽再生
                soundpool.play(hitSound, 1.0f, 1.0f, 0, -1, 0);

                //画像切り替え
                ((ImageView)findViewById(R.id.off)).setImageResource(R.drawable.on);
                this.mainTimer = new Timer();
                this.mainTimerTask = new MainTimerTask();
                this.mainTimer.schedule(mainTimerTask, 0, 50);

                //バイブ
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                long[] pattern = {0, 500, 500, 1000};
                vibrator.vibrate(pattern, -1);

                //フラッシュ
                if (mCameraID == null) {
                    return;
                }
                try {
                    Log.d("Has Camera or Not", mCameraID);
                    mCameraManager.setTorchMode(mCameraID, isOn);
                    new Handler().postDelayed(off, 2000);
                } catch (CameraAccessException e) {

                }
            } else if (randomNumber <= 7) {

            } else {

            }
        }
    }

    private class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (imageOn) {
                        ((ImageView)findViewById(R.id.off)).setImageResource(R.drawable.off);
                        try {
                            Log.d("Has Camera or Not", mCameraID);
                            mCameraManager.setTorchMode(mCameraID, !isOn);
                        } catch (CameraAccessException e) {

                        }
                        imageOn = false;
                    } else {
                        ((ImageView)findViewById(R.id.off)).setImageResource(R.drawable.on);
                        try {
                            Log.d("Has Camera or Not", mCameraID);
                            mCameraManager.setTorchMode(mCameraID, isOn);
                        } catch (CameraAccessException e) {

                        }
                        imageOn = true;
                    }
                }
            });
        }
    }

    private final Runnable off = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d("Has Camera or Not", mCameraID);
                mainTimer.cancel();
                mCameraManager.setTorchMode(mCameraID, !isOn);
                ((ImageView)findViewById(R.id.off)).setImageResource(R.drawable.on);
                soundpool.release();
            } catch (CameraAccessException e) {

            }
        }
    };
}