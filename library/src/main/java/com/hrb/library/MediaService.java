package com.hrb.library;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;


public class MediaService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnPreparedListener {

    public static final int OPTION_PLAY = 0;
    public static final int OPTION_PAUSE = 1;
    public static final int OPTION_CONTINUE = 2;
    public static final int OPTION_SEEK = 3;
    public static final int STATE_PLAY_COMPLETE = 4;
    public static final int STATE_PLAY_ERROR = 5;
    public static final int STATE_SEEK_COMPLETE = 6;
    public static final int STATE_PROGRESS_UPDATE = 7;
    public static final int STATE_MUSIC_PREPARE = 8;

    public static final String MUSIC_SERVICE_ACTION = "com.mini.media.service.action";
    public static final String MUSIC_STATE_ACTION = "com.mini.media.music.state.action";
    private static MediaPlayer mMediaPlayer;
    private MusicServiceReceiver mMusicServiceReceiver;
    private int mCurrPlayPosition = 0;
    private String mPlayUrl;
    private static ProgressTask mProgressTask;
    private boolean mIsStart = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
            mMediaPlayer.setOnPreparedListener(this);
        }

        if (mMusicServiceReceiver == null) {
            mMusicServiceReceiver = new MusicServiceReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(MUSIC_SERVICE_ACTION);
            registerReceiver(mMusicServiceReceiver, filter);
        }
    }

    private void playMusic(String path) {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepareAsync();
                if (mProgressTask == null) {
                    mProgressTask = new ProgressTask();
                    mProgressTask.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void pauseMusic() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    private void resumeMusic(String playPath) {
        playerToPosition(mCurrPlayPosition);
        if (TextUtils.isEmpty(mPlayUrl)) {
            mPlayUrl = playPath;
            playMusic(mPlayUrl);
        } else {
            mMediaPlayer.start();
        }
    }

    private void stopMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void playerToPosition(int CurrPlayPosition) {
        if (CurrPlayPosition > 0 && CurrPlayPosition < mMediaPlayer.getDuration()) {
            mMediaPlayer.seekTo(CurrPlayPosition);
        }
    }

    @Override
    public void onDestroy() {
        stopMusic();
        unregisterReceiver(mMusicServiceReceiver);
        if (mProgressTask != null) {
            mProgressTask.stopProgressUpdate();
            mProgressTask = null;
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent mediaIntent = new Intent();
        mediaIntent.setAction(MUSIC_SERVICE_ACTION);
        mediaIntent.putExtra("option", intent.getIntExtra("option", -1));
        mediaIntent.putExtra("playUrl", intent.getStringExtra("playUrl"));
        sendBroadcast(mediaIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Intent intent = new Intent();
        intent.setAction(MUSIC_STATE_ACTION);
        intent.putExtra("state", STATE_PLAY_COMPLETE);
        sendBroadcast(intent);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Intent intent = new Intent();
        intent.setAction(MUSIC_STATE_ACTION);
        intent.putExtra("state", STATE_PLAY_ERROR);
        sendBroadcast(intent);
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        Intent intent = new Intent();
        intent.setAction(MUSIC_STATE_ACTION);
        intent.putExtra("state", STATE_SEEK_COMPLETE);
        sendBroadcast(intent);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        Intent intent = new Intent();
        intent.setAction(MUSIC_STATE_ACTION);
        intent.putExtra("state", STATE_MUSIC_PREPARE);
        intent.putExtra("duration", mMediaPlayer.getDuration());
        sendBroadcast(intent);
        mIsStart = true;
    }

    private void seekToMusic(int pos) {
        if (mMediaPlayer != null && mIsStart && pos < mMediaPlayer.getDuration()) {
            mMediaPlayer.seekTo(pos);
        }
    }

    private class MusicServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int option = intent.getIntExtra("option", -1);
            switch (option) {
                case OPTION_PLAY:
                    mPlayUrl = intent.getStringExtra("playUrl");
                    playMusic(mPlayUrl);
                    break;
                case OPTION_PAUSE:
                    mCurrPlayPosition = mMediaPlayer.getCurrentPosition();
                    pauseMusic();
                    break;
                case OPTION_CONTINUE:
                    String path = intent.getStringExtra("playUrl");
                    resumeMusic(path);
                    break;
                case OPTION_SEEK:
                    int pos = intent.getIntExtra("seekPos", 0);
                    seekToMusic(pos);
                    break;
                default:
                    break;
            }
        }
    }

    private class ProgressTask extends AsyncTask<Void, Void, Void> {

        private boolean mIsUpdate = true;
        @Override
        protected Void doInBackground(Void... params) {
            while (mIsUpdate) {
                SystemClock.sleep(1000);
                publishProgress();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                Intent intent = new Intent();
                intent.setAction(MUSIC_STATE_ACTION);
                intent.putExtra("state", STATE_PROGRESS_UPDATE);
                intent.putExtra("currentPos", mMediaPlayer.getCurrentPosition());
                intent.putExtra("duration", mMediaPlayer.getDuration());
                sendBroadcast(intent);
            }
            super.onProgressUpdate(values);
        }

        public void stopProgressUpdate() {
            mIsUpdate = false;
        }
    }
}
