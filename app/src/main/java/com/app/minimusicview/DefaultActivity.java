package com.app.minimusicview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hrb.library.MiniMusicView;

public class DefaultActivity extends AppCompatActivity {

    private MiniMusicView mMusicView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMusicView = (MiniMusicView) findViewById(R.id.mmv_music);
        mMusicView.startPlayMusic("http://luoo-mp3.kssws.ks-cdn.com/low/luoo/radio847/06.mp3");
        mMusicView.setTitleText("Night Divides The Girls-Halou");
    }

    @Override
    protected void onDestroy() {
        mMusicView.stopPlayMusic();
        super.onDestroy();
    }
}
