package libs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.animation.ObjectAnimator;

import com.bacon.corey.audiotimeshift.MainActivity;
import com.bacon.corey.audiotimeshift.R;

public class CircleButton extends ImageView {

        private static final int PRESSED_COLOR_LIGHTUP = 255 / 25;
        private static final int PRESSED_RING_ALPHA = 95;
        private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 4;
        private static final int ANIMATION_TIME_ID = android.R.integer.config_shortAnimTime;

        private int centerY;
        private int centerX;
        private int outerRadius;
        private int pressedRingRadius;

        private Paint circlePaint;
        private Paint focusPaint;

        private float animationProgress;

        private int pressedRingWidth;
        private int defaultColor;
        private int pressedColor;
        private ObjectAnimator pressedAnimator;
        boolean colorSet = false;
        int notPressedColor;
        int mPressedColor;
        public CircleButton(Context context) {
            super(context);
            init(context, null);
        }

        public CircleButton(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context, attrs);
        }

        public CircleButton(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init(context, attrs);
        }

        @Override
        public void setPressed(boolean pressed) {
            super.setPressed(pressed);

            if (circlePaint != null) {
                circlePaint.setColor(pressed ? pressedColor : defaultColor);
            }

            if (pressed) {
                showPressedRing();
            } else {
                hidePressedRing();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawCircle(centerX, centerY, pressedRingRadius + animationProgress, focusPaint);
            canvas.drawCircle(centerX, centerY, outerRadius - pressedRingWidth, circlePaint);
            super.onDraw(canvas);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            centerX = w / 2;
            centerY = h / 2;
            outerRadius = Math.min(w, h) / 2;
            pressedRingRadius = outerRadius - pressedRingWidth - pressedRingWidth / 2;
        }

        public float getAnimationProgress() {
            return animationProgress;
        }

        public void setAnimationProgress(float animationProgress) {
            this.animationProgress = animationProgress;
            this.invalidate();
        }

        public void setColor(int color) {
            this.defaultColor = color;
            this.pressedColor = getHighlightColor(color, PRESSED_COLOR_LIGHTUP);

            circlePaint.setColor(defaultColor);
            focusPaint.setColor(defaultColor);
            focusPaint.setAlpha(PRESSED_RING_ALPHA);

            this.invalidate();
        }
    public void setDefaultColor(int color) {
        this.notPressedColor = color;
        this.mPressedColor = MainActivity.darkenColorRGB(color);

        circlePaint.setColor(notPressedColor);
        focusPaint.setColor(notPressedColor);
        focusPaint.setAlpha(PRESSED_RING_ALPHA);

        this.invalidate();
    }
        private void hidePressedRing() {
            pressedAnimator.setFloatValues(pressedRingWidth, 0f);
            pressedAnimator.start();
        }

        private void showPressedRing() {
            pressedAnimator.setFloatValues(animationProgress, pressedRingWidth);
            pressedAnimator.start();
        }

        private void init(Context context, AttributeSet attrs) {
            this.setFocusable(true);
            this.setScaleType(ScaleType.CENTER_INSIDE);
            setClickable(true);
            circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            circlePaint.setStyle(Paint.Style.FILL);

            focusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            focusPaint.setStyle(Paint.Style.STROKE);

            pressedRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PRESSED_RING_WIDTH_DIP, getResources()
                    .getDisplayMetrics());

            int color = Color.BLACK;
            if (attrs != null) {
                final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleButton);
                color = a.getColor(R.styleable.CircleButton_cb_color, color);
                pressedRingWidth = (int) a.getDimension(R.styleable.CircleButton_cb_pressedRingWidth, pressedRingWidth);

                a.recycle();
                notPressedColor = color;
                mPressedColor = MainActivity.darkenColorRGB(notPressedColor);

            }

            setColor(color);


            focusPaint.setStrokeWidth(pressedRingWidth);
            final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
            pressedAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 0f);
            pressedAnimator.setDuration(pressedAnimationTime);
        }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!colorSet && event.getAction() == MotionEvent.ACTION_DOWN) {
            setColor(mPressedColor);
            colorSet = true;
        }
        else if (colorSet && event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
            setColor(notPressedColor);
            colorSet = false;

        }
        return super.onTouchEvent(event);
    }

    private int getHighlightColor(int color, int amount) {
            return Color.argb(Math.min(255, Color.alpha(color)), Math.min(255, Color.red(color) + amount),
                    Math.min(255, Color.green(color) + amount), Math.min(255, Color.blue(color) + amount));
        }
    }
