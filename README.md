# MiniMusicView
A music player widget to add custom layout.
##ScreenShot
![](https://github.com/henry-blue/MiniMusicView/raw/master/default_bg_new.png)...
![](https://github.com/henry-blue/MiniMusicView/raw/master/custom_bg_new.png)
##Usage
First you can add gradle dependency with command :
```groovy
dependencies {
    ......
    compile 'com.henryblue.minimusicview:library:0.9.8'
   }

```
To add gradle dependency you need to open build.gradle (in your app folder,not in a project folder) then copy and add the dependencies there in the dependencies block;

###Use default layout

1.Add MiniMusicView in your layout
```
    <com.hrb.library.MiniMusicView
        android:id="@+id/mmv_music"
        app:isLoadLayout="true"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
2.set music url and play music
```
   mMusicView = (MiniMusicView) findViewById(R.id.mmv_music);
   mMusicView.setTitleText("music name");
   mMusicView.startPlayMusic("music url");
   
   // Or through the new way to create view object
   // mMusicView = new MiniMusicView(this);
   // mMusicView.initDefaultView();
   // mMusicView.setTitleText("music name");
   // mMusicView.startPlayMusic("music url");
```
3.stop play music
```
    @Override
    protected void onDestroy() {
        mMusicView.stopPlayMusic();
        super.onDestroy();
    }
```
Achieve the effect of the first picture above.

###Use custom layout
1.Add MiniMusicView in your layout
```
    <com.hrb.library.MiniMusicView
        android:id="@+id/mmv_music"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
2.set layout, music url and play music
```
   mMusicView = (MiniMusicView) findViewById(R.id.mmv_music);
   View view = View.inflate(CustomActivity.this, R.layout.layout_custom_music, null);
   TextView title = (TextView) view.findViewById(R.id.tv_music_play_title);
   title.setText("music name");
   mMusicView.addView(view);
   mMusicView.startPlayMusic("music url");
   // Or through the new way to create view object
   // mMusicView = new MiniMusicView(this);
   // mMusicView.addView(view);
   // mMusicView.startPlayMusic("music url");
```
3.you can also set MiniMusicView listener
```
   mMusicView.setOnMusicStateListener(new MiniMusicView.OnMusicStateListener() {
            @Override
            public void onPrepared(int duration) {
                Log.i(TAG, "start prepare play music");
            }

            @Override
            public void onError() {
                Log.i(TAG, "start play music error");
            }

            @Override
            public void onMusicPlayComplete() {
                Log.i(TAG, "start play music completed");
            }

            @Override
            public void onSeekComplete() {
                Log.i(TAG, "seek play music completed");
            }

            @Override
            public void onProgressUpdate(int duration, int currentPos) {
                Log.i(TAG, "play music progress update");
            }

            @Override
            public void onHeadsetPullOut() {
                Log.i(TAG, "headset pull out");
            }
        });
```
