package com.hrb.library;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MiniMusicView extends FrameLayout {

    private Context mContext;
    private ViewStub mViewStub;
    private LinearLayout mLayout;
    private ImageButton mIcon;
    private ImageButton mControlBtn;
    private ProgressBar mLoadMusic;
    private TextView mMusicTitle;
    private SeekBar mProgressBar;
    private boolean mIsAddView;
    private Intent mServiceIntent;
    private Intent mPauseIntent;
    private Intent mResumeIntent;
    private boolean mIsPlay;
    private MusicStateUpdateReceiver mMusicUpdateReceiver;
    private OnMusicStateListener mMusicStateListener;
    private HeadsetPlugReceiver mHeadsetPlugReceiver;
    private int mMusicDuration;

    public MiniMusicView(Context context) {
        this(context, null);
    }

    public MiniMusicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiniMusicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mIsAddView = false;
        mIsPlay = true;
        initView();
        initAttributeSet(attrs);
    }

    private void initAttributeSet(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray arr = mContext.obtainStyledAttributes(attrs, R.styleable.MiniMusicView);
        final int titleColor = arr.getColor(R.styleable.MiniMusicView_titleColor, Color.parseColor("#000000"));
        setTitleColor(titleColor);
        final int titleSize = arr.getDimensionPixelOffset(R.styleable.MiniMusicView_titleTextSize, -1);
        if (titleSize != -1) {
            setTitleTextSize(titleSize);
        }
        final int iconBgColor = arr.getColor(R.styleable.MiniMusicView_musicIconBackgroundColor, Color.parseColor("#e0e0e0"));
        setIconBackgroundColor(iconBgColor);

        final int backColor = arr.getColor(R.styleable.MiniMusicView_musicBackgroundColor, Color.parseColor("#eeeeee"));
        setMusicBackgroundColor(backColor);

        final Drawable progressDrawable = arr.getDrawable(R.styleable.MiniMusicView_progressDrawable);
        if (progressDrawable != null) {
            setProgressDrawable(progressDrawable);
        }
        final Drawable iconDrawable = arr.getDrawable(R.styleable.MiniMusicView_musicIcon);
        if (iconDrawable != null) {
            setIconDrawable(iconDrawable);
        }
        arr.recycle();
    }

    private void initView() {
        View.inflate(mContext, R.layout.layout_default_viewstup, this);
        mViewStub = (ViewStub) findViewById(R.id.vs_mini_view);
        initReceiver();
    }

    private void initDefaultView() {
        View view = mViewStub.inflate();
        mLayout = (LinearLayout) view.findViewById(R.id.ll_layout);
        mIcon = (ImageButton) view.findViewById(R.id.iv_music_icon);
        mControlBtn = (ImageButton) view.findViewById(R.id.ib_control_btn);
        mLoadMusic = (ProgressBar) view.findViewById(R.id.pb_loading);
        mMusicTitle = (TextView) view.findViewById(R.id.tv_music_title);
        mProgressBar = (SeekBar) view.findViewById(R.id.sb_progress);

        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onControlBtnClick(view);
            }
        });
    }

    private void initReceiver() {
        mMusicUpdateReceiver = new MusicStateUpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MediaService.MUSIC_STATE_ACTION);
        mContext.registerReceiver(mMusicUpdateReceiver, filter);
        registerHeadsetPlugReceiver();
    }

    private void registerHeadsetPlugReceiver() {
        mHeadsetPlugReceiver = new HeadsetPlugReceiver();
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        mContext.registerReceiver(mHeadsetPlugReceiver, intentFilter);
    }

    private void onControlBtnClick(View v) {
        if (mIsPlay) {
            pausePlayMusic();
            changeControlBtnState(false);
        } else {
            resumePlayMusic();
            changeControlBtnState(true);
        }
    }

    @Override
    public void addView(View child) {
        removeAllViews();
        super.addView(child);
        mIsAddView = true;
    }

    public void changeControlBtnState(boolean isPlay) {
        if (!mIsAddView && mControlBtn != null) {
            if (isPlay) {
                mControlBtn.setImageResource(R.drawable.pause_mini_music);
                mIsPlay = true;
            } else {
                mControlBtn.setImageResource(R.drawable.play_mini_music);
                mIsPlay = false;
            }
        }
    }

    public void setOnMusicStateListener(OnMusicStateListener listener) {
        mMusicStateListener = listener;
    }

    public void startPlayMusic(String path) {
        if (!mIsAddView) {
            initDefaultView();
        }

        if (mServiceIntent == null) {
            mServiceIntent = new Intent(mContext, MediaService.class);
            mServiceIntent.putExtra("option", MediaService.OPTION_PLAY);
            mServiceIntent.putExtra("playUrl", path);
            mContext.startService(mServiceIntent);
        } else {
            Intent playIntent = new Intent();
            playIntent.setAction(MediaService.MUSIC_SERVICE_ACTION);
            playIntent.putExtra("option", MediaService.OPTION_PLAY);
            playIntent.putExtra("playUrl", path);
            mContext.sendBroadcast(playIntent);
        }
    }

    public void resumePlayMusic() {
        if (mResumeIntent == null) {
            mResumeIntent = new Intent();
            mResumeIntent.setAction(MediaService.MUSIC_SERVICE_ACTION);
            mResumeIntent.putExtra("option", MediaService.OPTION_CONTINUE);
        }
        mContext.sendBroadcast(mResumeIntent);
    }

    public void pausePlayMusic() {
        if (mPauseIntent == null) {
            mPauseIntent = new Intent();
            mPauseIntent.setAction(MediaService.MUSIC_SERVICE_ACTION);
            mPauseIntent.putExtra("option", MediaService.OPTION_PAUSE);
        }
        mContext.sendBroadcast(mPauseIntent);
    }

    public void seekToMusic(int pos) {
        Intent intent = new Intent();
        intent.setAction(MediaService.MUSIC_SERVICE_ACTION);
        intent.putExtra("option", MediaService.OPTION_SEEK);
        intent.putExtra("seekPos", pos);
        mContext.sendBroadcast(intent);
    }

    public void stopPlayMusic() {
        if (mServiceIntent != null) {
            mContext.stopService(mServiceIntent);
        }
        if (mMusicUpdateReceiver != null) {
            mContext.unregisterReceiver(mMusicUpdateReceiver);
        }
        if (mHeadsetPlugReceiver != null) {
            mContext.unregisterReceiver(mHeadsetPlugReceiver);
        }
    }

    public void setTitleColor(int color) {
        if (!mIsAddView && mMusicTitle != null) {
            mMusicTitle.setTextColor(color);
        }
    }

    public void setTitleTextSize(int dimen) {
        if (!mIsAddView && mMusicTitle != null) {
            mMusicTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen);
        }
    }

    public void setIconBackgroundColor(int color) {
        if (!mIsAddView && mIcon != null) {
            mIcon.setBackgroundColor(color);
        }
    }

    public void setMusicBackgroundColor(int color) {
        if (!mIsAddView && mLayout != null) {
            mLayout.setBackgroundColor(color);
        }
    }

    public void setIconDrawable(Drawable background) {
        if (!mIsAddView && mIcon != null) {
            mIcon.setImageDrawable(background);
        }
    }

    public void setProgressDrawable(Drawable drawable) {
        if (!mIsAddView && mProgressBar != null) {
            mProgressBar.setProgressDrawable(drawable);
        }
    }

    public void setProgressMax(int max) {
        if (!mIsAddView && mProgressBar != null) {
            mProgressBar.setMax(max);
        }
    }

    public void setTitleText(String text) {
        if (!mIsAddView && mMusicTitle != null) {
            mMusicTitle.setText(text);
        }
    }

    public boolean isPlaying() {
        return mIsPlay;
    }

    public int getMusicDuration() {
        return mMusicDuration;
    }

    public interface OnMusicStateListener {
        void onPrepared(int duration);
        void onError();
        void onMusicPlayComplete();
        void onSeekComplete();
        void onProgressUpdate(int duration, int currentPos);
        void onHeadsetPullOut();
    }

    public class MusicStateUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case MediaService.STATE_PLAY_COMPLETE:
                    if (mMusicStateListener != null) {
                        mMusicStateListener.onMusicPlayComplete();
                    }
                    break;
                case MediaService.STATE_PLAY_ERROR:
                    if (mMusicStateListener != null) {
                        mMusicStateListener.onError();
                    }
                    if (!mIsAddView) {
                        Toast.makeText(mContext, getResources().getString(R.string.load_error),
                                Toast.LENGTH_SHORT).show();
                        mLoadMusic.setVisibility(View.GONE);
                        if (mControlBtn.getVisibility() != View.VISIBLE) {
                            mControlBtn.setVisibility(View.VISIBLE);
                        }
                        changeControlBtnState(false);
                    }
                    break;
                case MediaService.STATE_SEEK_COMPLETE:
                    if (mMusicStateListener != null) {
                        mMusicStateListener.onSeekComplete();
                    }
                    break;
                case MediaService.STATE_MUSIC_PREPARE:
                    mMusicDuration = intent.getIntExtra("duration", -1);
                    if (mMusicStateListener != null) {
                        mMusicStateListener.onPrepared(mMusicDuration);
                    }
                    if (!mIsAddView) {
                        mLoadMusic.setVisibility(View.GONE);
                        mControlBtn.setVisibility(View.VISIBLE);
                        setProgressMax(mMusicDuration);
                    }
                    break;
                case MediaService.STATE_PROGRESS_UPDATE:
                    int duration = intent.getIntExtra("duration", 0);
                    int currentPos = intent.getIntExtra("currentPos", 0);
                    if (mMusicStateListener != null) {
                        mMusicStateListener.onProgressUpdate(duration, currentPos);
                    }
                    if (!mIsAddView) {
                        mProgressBar.setProgress(currentPos);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private class HeadsetPlugReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                if (mIsPlay) {
                    if (mMusicStateListener != null) {
                        mMusicStateListener.onHeadsetPullOut();
                    }
                    if (!mIsAddView) {
                        pausePlayMusic();
                        changeControlBtnState(false);
                    }
                }
            }
        }
    }
}
