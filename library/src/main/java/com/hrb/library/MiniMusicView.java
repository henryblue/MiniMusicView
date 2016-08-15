package com.hrb.library;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MiniMusicView extends FrameLayout {

    private Context mContext;
    private ImageButton mIcon;
    private ImageButton mControllBtn;
    private TextView mMusicTitle;
    private TextView mMusicSinger;
    private SeekBar mProgressBar;

    public MiniMusicView(Context context) {
        this(context, null);
    }

    public MiniMusicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiniMusicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        View.inflate(mContext, R.layout.layout_mini_music, this);
        mIcon = (ImageButton) this.findViewById(R.id.iv_music_icon);
        mControllBtn = (ImageButton) this.findViewById(R.id.ib_control_btn);
        mMusicTitle = (TextView) this.findViewById(R.id.tv_music_title);
        mProgressBar = (SeekBar) this.findViewById(R.id.sb_progress);
    }

    @Override
    public void addView(View child) {
        removeAllViews();
        super.addView(child);
    }
}
