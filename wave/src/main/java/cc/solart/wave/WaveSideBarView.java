//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cc.solart.wave;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path.Direction;
import android.graphics.Path.Op;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import cc.solart.wave.R.array;
import cc.solart.wave.R.dimen;
import cc.solart.wave.R.styleable;
import java.util.Arrays;
import java.util.List;

public class WaveSideBarView extends View {
    private static final String TAG = "WaveSlideBarView";
    private static final double ANGLE = 0.7853981633974483D;
    private static final double ANGLE_R = 1.5707963267948966D;
    private WaveSideBarView.OnTouchLetterChangeListener listener;
    private List<String> mLetters;
    private int mChoose;
    private Paint mLettersPaint;
    private Paint mTextPaint;
    private Paint mWavePaint;
    private float mTextSize;
    private float mLargeTextSize;
    private int mTextColor;
    private int mWaveColor;
    private int mTextColorChoose;
    private int mWidth;
    private int mHeight;
    private int mItemHeight;
    private int mPadding;
    private Path mWavePath;
    private Path mBallPath;
    private int mCenterY;
    private int mRadius;
    private int mBallRadius;
    ValueAnimator mRatioAnimator;
    private float mRatio;
    private float mPosX;
    private float mPosY;
    private float mBallCentreX;

    public WaveSideBarView(Context context) {
        this(context, (AttributeSet)null);
    }

    public WaveSideBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveSideBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mChoose = -1;
        this.mLettersPaint = new Paint();
        this.mTextPaint = new Paint();
        this.mWavePaint = new Paint();
        this.mWavePath = new Path();
        this.mBallPath = new Path();
        this.init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mLetters = Arrays.asList(context.getResources().getStringArray(array.waveSideBarLetters));
        this.mTextColor = Color.parseColor("#969696");
        this.mWaveColor = Color.parseColor("#be69be91");
        this.mTextColorChoose = context.getResources().getColor(17170443);
        this.mTextSize = (float)context.getResources().getDimensionPixelSize(dimen.textSize_sidebar);
        this.mLargeTextSize = (float)context.getResources().getDimensionPixelSize(dimen.large_textSize_sidebar);
        this.mPadding = context.getResources().getDimensionPixelSize(dimen.textSize_sidebar_padding);
        if(attrs != null) {
            TypedArray a = this.getContext().obtainStyledAttributes(attrs, styleable.WaveSideBarView);
            this.mTextColor = a.getColor(styleable.WaveSideBarView_sidebarTextColor, this.mTextColor);
            this.mTextColorChoose = a.getColor(styleable.WaveSideBarView_sidebarChooseTextColor, this.mTextColorChoose);
            this.mTextSize = a.getFloat(styleable.WaveSideBarView_sidebarTextSize, this.mTextSize);
            this.mLargeTextSize = a.getFloat(styleable.WaveSideBarView_sidebarLargeTextSize, this.mLargeTextSize);
            this.mWaveColor = a.getColor(styleable.WaveSideBarView_sidebarBackgroundColor, this.mWaveColor);
            this.mRadius = a.getColor(styleable.WaveSideBarView_sidebarRadius, context.getResources().getDimensionPixelSize(dimen.radius_sidebar));
            this.mBallRadius = a.getColor(styleable.WaveSideBarView_sidebarBallRadius, context.getResources().getDimensionPixelSize(dimen.ball_radius_sidebar));
            a.recycle();
        }

        this.mWavePaint = new Paint();
        this.mWavePaint.setAntiAlias(true);
        this.mWavePaint.setStyle(Style.FILL);
        this.mWavePaint.setColor(this.mWaveColor);
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setColor(this.mTextColorChoose);
        this.mTextPaint.setStyle(Style.FILL);
        this.mTextPaint.setTextSize(this.mLargeTextSize);
        this.mTextPaint.setTextAlign(Align.CENTER);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        float y = event.getY();
        float x = event.getX();
        int oldChoose = this.mChoose;
        int newChoose = (int)(y / (float)this.mHeight * (float)this.mLetters.size());
        switch(event.getAction()) {
            case 0:
                if(x < (float)(this.mWidth - 2 * this.mRadius)) {
                    return false;
                }

                this.startAnimator(new float[]{this.mRatio, 1.0F});
                break;
            case 1:
            case 3:
                this.startAnimator(new float[]{this.mRatio, 0.0F});
                this.mChoose = -1;
                break;
            case 2:
                this.mCenterY = (int)y;
                if(oldChoose != newChoose && newChoose >= 0 && newChoose < this.mLetters.size()) {
                    this.mChoose = newChoose;
                    if(this.listener != null) {
                        this.listener.onLetterChange((String)this.mLetters.get(newChoose));
                    }
                }

                this.invalidate();
        }

        return true;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.mHeight = this.getHeight();
        this.mWidth = this.getWidth();
        this.mItemHeight = (this.mHeight - this.mPadding) / this.mLetters.size();
        this.mPosX = (float)this.mWidth - 1.6F * this.mTextSize;

        this.drawLetters(canvas);
        this.drawWavePath(canvas);
        this.drawBallPath(canvas);
        this.drawChooseText(canvas);
    }

    private void drawLetters(Canvas canvas) {
        RectF rectF = new RectF();
        rectF.left = this.mPosX - this.mTextSize;
        rectF.right = this.mPosX + this.mTextSize;
        rectF.top = this.mTextSize / 2.0F;
        rectF.bottom = (float)this.mHeight - this.mTextSize / 2.0F;
        this.mLettersPaint.reset();
        this.mLettersPaint.setStyle(Style.FILL);
        this.mLettersPaint.setColor(Color.parseColor("#F9F9F9"));
        this.mLettersPaint.setAntiAlias(true);
        canvas.drawRoundRect(rectF, this.mTextSize, this.mTextSize, this.mLettersPaint);
        this.mLettersPaint.reset();
        this.mLettersPaint.setStyle(Style.STROKE);
        this.mLettersPaint.setColor(this.mTextColor);
        this.mLettersPaint.setAntiAlias(true);
        canvas.drawRoundRect(rectF, this.mTextSize, this.mTextSize, this.mLettersPaint);

        for(int i = 0; i < this.mLetters.size(); ++i) {
            this.mLettersPaint.reset();
            this.mLettersPaint.setColor(this.mTextColor);
            this.mLettersPaint.setAntiAlias(true);
            this.mLettersPaint.setTextSize(this.mTextSize);
            this.mLettersPaint.setTextAlign(Align.CENTER);
            FontMetrics fontMetrics = this.mLettersPaint.getFontMetrics();
            float baseline = Math.abs(-fontMetrics.bottom - fontMetrics.top);
            float posY = (float)(this.mItemHeight * i) + baseline / 2.0F + (float)this.mPadding;
            if(i == this.mChoose) {
                this.mPosY = posY;
            } else {
                canvas.drawText((String)this.mLetters.get(i), this.mPosX, posY, this.mLettersPaint);
            }
        }

    }

    private void drawChooseText(Canvas canvas) {
        if(this.mChoose != -1) {
            this.mLettersPaint.reset();
            this.mLettersPaint.setColor(this.mTextColorChoose);
            this.mLettersPaint.setTextSize(this.mTextSize);
            this.mLettersPaint.setTextAlign(Align.CENTER);
            canvas.drawText((String)this.mLetters.get(this.mChoose), this.mPosX, this.mPosY, this.mLettersPaint);
            if(this.mRatio >= 0.9F) {
                String target = (String)this.mLetters.get(this.mChoose);
                FontMetrics fontMetrics = this.mTextPaint.getFontMetrics();
                float baseline = Math.abs(-fontMetrics.bottom - fontMetrics.top);
                float x = this.mBallCentreX;
                float y = (float)this.mCenterY + baseline / 2.0F;
                canvas.drawText(target, x, y, this.mTextPaint);
            }
        }

    }

    private void drawWavePath(Canvas canvas) {
        this.mWavePath.reset();
        this.mWavePath.moveTo((float)this.mWidth, (float)(this.mCenterY - 3 * this.mRadius));
        int controlTopY = this.mCenterY - 2 * this.mRadius;
        int endTopX = (int)((double)this.mWidth - (double)this.mRadius * Math.cos(0.7853981633974483D) * (double)this.mRatio);
        int endTopY = (int)((double)controlTopY + (double)this.mRadius * Math.sin(0.7853981633974483D));
        this.mWavePath.quadTo((float)this.mWidth, (float)controlTopY, (float)endTopX, (float)endTopY);
        int controlCenterX = (int)((double)this.mWidth - (double)(1.8F * (float)this.mRadius) * Math.sin(1.5707963267948966D) * (double)this.mRatio);
        int controlCenterY = this.mCenterY;
        int controlBottomY = this.mCenterY + 2 * this.mRadius;
        int endBottomY = (int)((double)controlBottomY - (double)this.mRadius * Math.cos(0.7853981633974483D));
        this.mWavePath.quadTo((float)controlCenterX, (float)controlCenterY, (float)endTopX, (float)endBottomY);
        this.mWavePath.quadTo((float)this.mWidth, (float)controlBottomY, (float)this.mWidth, (float)(controlBottomY + this.mRadius));
        this.mWavePath.close();
        canvas.drawPath(this.mWavePath, this.mWavePaint);
    }

    private void drawBallPath(Canvas canvas) {
        this.mBallCentreX = (float)(this.mWidth + this.mBallRadius) - (2.0F * (float)this.mRadius + 2.0F * (float)this.mBallRadius) * this.mRatio;
        this.mBallPath.reset();
        this.mBallPath.addCircle(this.mBallCentreX, (float)this.mCenterY, (float)this.mBallRadius, Direction.CW);
        if(VERSION.SDK_INT >= 19) {
            this.mBallPath.op(this.mWavePath, Op.DIFFERENCE);
        }

        this.mBallPath.close();
        canvas.drawPath(this.mBallPath, this.mWavePaint);
    }

    private void startAnimator(float... value) {
        if(this.mRatioAnimator == null) {
            this.mRatioAnimator = new ValueAnimator();
        }

        this.mRatioAnimator.cancel();
        this.mRatioAnimator.setFloatValues(value);
        this.mRatioAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator value) {
                WaveSideBarView.this.mRatio = ((Float)value.getAnimatedValue()).floatValue();
                WaveSideBarView.this.invalidate();
            }
        });
        this.mRatioAnimator.start();
    }

    public void setOnTouchLetterChangeListener(WaveSideBarView.OnTouchLetterChangeListener listener) {
        this.listener = listener;
    }

    public List<String> getLetters() {
        return this.mLetters;
    }

    public void setLetters(List<String> letters) {
        this.mLetters = letters;
        this.invalidate();
    }

    public interface OnTouchLetterChangeListener {
        void onLetterChange(String var1);
    }
}
