package com.app.minimusicview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hrb.library.MiniMusicView;

public class CustomActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "CustomActivity";
    private MiniMusicView mMusicView;
    private boolean isPlay = true;
    private ImageButton controlBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mMusicView = new MiniMusicView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(mMusicView, params);
        init();
    }

    private void init() {
        View view = View.inflate(CustomActivity.this, R.layout.layout_custom_music, null);
        TextView title = (TextView) view.findViewById(R.id.tv_music_play_title);
        title.setText("前前前世");
        controlBtn = (ImageButton) view.findViewById(R.id.ib_play);
        controlBtn.setOnClickListener(this);

        ImageButton preBtn = (ImageButton) view.findViewById(R.id.ib_pre);
        preBtn.setOnClickListener(this);

        ImageButton nextBtn = (ImageButton) view.findViewById(R.id.ib_next);
        nextBtn.setOnClickListener(this);

        mMusicView.addView(view);
        mMusicView.startPlayMusic("http://up.haoduoge.com:82/mp3/2016-10-07/1475810104.mp3");

        mMusicView.setOnMusicStateListener(new MiniMusicView.OnMusicStateListener() {
            @Override
            public void onPrepared(int duration) {
                Log.i(TAG, "start prepare play_mini_music music");
            }

            @Override
            public void onError(int what, int extra) {
                Log.i(TAG, "start play_mini_music music error");
                Toast.makeText(CustomActivity.this, "加载音乐出错", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInfo(int what, int extra) {
                Log.i(TAG, "start play_mini_music music info");
            }

            @Override
            public void onMusicPlayComplete() {
                Log.i(TAG, "start play_mini_music music completed");
            }

            @Override
            public void onSeekComplete() {
                Log.i(TAG, "seek play_mini_music music completed");
            }

            @Override
            public void onProgressUpdate(int duration, int currentPos) {
                Log.i(TAG, "play_mini_music music progress update");
            }

            @Override
            public void onHeadsetPullOut() {
                Log.i(TAG, "headset pull out");
                mMusicView.stopPlayMusic();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mMusicView.stopPlayMusic();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_play:
                if (isPlay) {
                    mMusicView.pausePlayMusic();
                    controlBtn.setImageResource(R.drawable.music_play);
                    isPlay = false;
                } else {
                    mMusicView.resumePlayMusic();
                    controlBtn.setImageResource(R.drawable.music_pause);
                    isPlay = true;
                }
                break;
            case R.id.ib_pre:
//                mMusicView.startPlayMusic("the pre music url");
                break;
            case R.id.ib_next:
//                mMusicView.startPlayMusic("the next music url");
                break;
            default:
                break;
        }
    }
}
