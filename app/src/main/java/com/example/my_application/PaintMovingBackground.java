package com.example.my_application;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.animation.ValueAnimator;

public class PaintMovingBackground extends View {
    private Paint paint;
    private Shader shader;
    private Matrix matrix;
    private float shiftX = 0, shiftY = 0;
    private int viewWidth, viewHeight;
    private ValueAnimator animator;

    public PaintMovingBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        matrix = new Matrix();

        // Start animation loop
        startAnimation();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        createShader();
    }

    private void createShader() {
        // Create a gradient shader with light pink and yellow shades
        shader = new LinearGradient(
                0, 0, viewWidth, viewHeight,
                new int[]{0xFFffcccb, 0xFFffeb99, 0xFF87CEFA, 0xFF6495ED},
                null,
                Shader.TileMode.MIRROR
        );

        paint.setShader(shader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Apply transformation to create movement
        matrix.setTranslate(shiftX, shiftY);
        ((LinearGradient) shader).setLocalMatrix(matrix);

        // Draw the moving gradient
        canvas.drawRect(0, 0, viewWidth, viewHeight, paint);
    }

    private void startAnimation() {
        animator = ValueAnimator.ofFloat(0, 1000);
        animator.setDuration(8000); // 8 seconds cycle
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            shiftX = (float) (50 * Math.sin(value * 0.005)); // Creates slow sinusoidal movement
            shiftY = (float) (50 * Math.cos(value * 0.005));
            invalidate(); // Refresh the view
        });
        animator.start();
    }
}
