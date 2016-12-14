package com.app.minimusicview;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.hrb.library.MiniMusicView;

public class DefaultActivity extends AppCompatActivity {

    private MiniMusicView mMusicView;
    private boolean isChange = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_default_music);
        mMusicView = (MiniMusicView) findViewById(R.id.mmv_music);
        mMusicView.initDefaultView();
        mMusicView.setTitleText("前前前世");
        mMusicView.setAuthor("RADWIMPS");
        mMusicView.setIconDrawable(ContextCompat.getDrawable(DefaultActivity.this, R.drawable.img_bg));
        mMusicView.startPlayMusic("http://up.haoduoge.com:82/mp3/2016-10-07/1475810104.mp3");

        mMusicView.setOnNextBtnClickListener(new MiniMusicView.OnNextButtonClickListener() {
            @Override
            public void OnClick() {
                // 这里只是简单模拟切换
                if (!isChange) {
                    mMusicView.setTitleText("Night Divides");
                    mMusicView.setAuthor("The Girls-Halou");
                    mMusicView.setIconDrawable(ContextCompat.getDrawable(DefaultActivity.this, R.drawable.thumb));
                    mMusicView.startPlayMusic("http://mp3-cdn.luoo.net/low/luoo/radio847/02.mp3");
                    isChange = true;
                } else {
                    mMusicView.setTitleText("前前前世");
                    mMusicView.setAuthor("RADWIMPS");
                    mMusicView.setIconDrawable(ContextCompat.getDrawable(DefaultActivity.this, R.drawable.img_bg));
                    mMusicView.startPlayMusic("http://up.haoduoge.com:82/mp3/2016-10-07/1475810104.mp3");
                    isChange = false;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        mMusicView.stopPlayMusic();
        super.onDestroy();
    }
}
