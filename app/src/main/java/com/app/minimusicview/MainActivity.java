package com.app.minimusicview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hrb.library.MiniMusicView;

public class MainActivity extends AppCompatActivity {

    private MiniMusicView mMusicView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMusicView = (MiniMusicView) findViewById(R.id.mmv_music);
        mMusicView.startPlayMusic("http://luoo-mp3.kssws.ks-cdn.com/low/luoo/radio847/05.mp3");
    }

    @Override
    protected void onDestroy() {
        mMusicView.stopPlayMusic();
        super.onDestroy();
    }
}
