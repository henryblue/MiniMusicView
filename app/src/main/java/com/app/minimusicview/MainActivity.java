package com.app.minimusicview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hrb.library.MiniMusicView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MiniMusicView musicView = (MiniMusicView) findViewById(R.id.mmv_music);
    }
}
