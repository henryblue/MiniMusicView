package com.hrb.library;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;


public class MediaService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener {

    private static MediaPlayer mMediaPlayer;
    private int mCurrPlayPosition = 0;
    private String mPlayUrl;
    private static ProgressTask mProgressTask;
    private boolean mIsStart = false;

    private MediaBinder mBinder = new MediaBinder();
    private IMediaStateListener mMediaStateListener;

    class MediaBinder extends Binder {

        MediaService getService(){
            return MediaService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
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
            mMediaPlayer.setOnInfoListener(this);
        }
    }

    public void playMusic(String path) {
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


    public void setMediaStateListener(IMediaStateListener listener) {
        mMediaStateListener = listener;
    }

    public void pauseMusic() {
        mCurrPlayPosition = mMediaPlayer.getCurrentPosition();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void resumeMusic(String playPath) {
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
        if (mProgressTask != null) {
            mProgressTask.stopProgressUpdate();
            mProgressTask = null;
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mMediaStateListener != null) {
            mMediaStateListener.onCompletion();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        if (mMediaStateListener != null) {
            mMediaStateListener.onError(what, extra);
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (mMediaStateListener != null) {
            mMediaStateListener.onInfo(what, extra);
        }
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        if (mMediaStateListener != null) {
            mMediaStateListener.onSeekComplete();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        if (mMediaStateListener != null) {
            mMediaStateListener.onPrepared(mMediaPlayer.getDuration());
        }
        mIsStart = true;
    }

    public void seekToMusic(int pos) {
        if (mMediaPlayer != null && mIsStart && pos < mMediaPlayer.getDuration()) {
            mMediaPlayer.seekTo(pos);
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
                if (mMediaStateListener != null) {
                    mMediaStateListener.onProgressUpdate(mMediaPlayer.getCurrentPosition(),
                            mMediaPlayer.getDuration());
                }
            }
            super.onProgressUpdate(values);
        }

        void stopProgressUpdate() {
            mIsUpdate = false;
        }
    }

    interface IMediaStateListener {
        void onPrepared(int duration);
        void onProgressUpdate(int currentPos, int duration);
        void onSeekComplete();
        void onCompletion();
        boolean onInfo(int what, int extra);
        boolean onError(int what, int extra);
    }
}
