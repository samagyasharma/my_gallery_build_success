package com.example.my_application;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MovingBackgroundMainDark extends View {
    private final Paint backgroundPaint = new Paint();
    
    public MovingBackgroundMainDark(Context context, AttributeSet attrs) {
        super(context, attrs);
        backgroundPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        // Create a darker gradient background
        LinearGradient gradient = new LinearGradient(
            0, 0,
            0, h,
            new int[]{
                Color.parseColor("#1A1A1A"),  // Very dark purple/black at top
                Color.parseColor("#2D2D2D"),  // Dark purple in middle
                Color.parseColor("#1A1A1A")   // Very dark purple/black at bottom
            },
            new float[]{0f, 0.5f, 1f},
            Shader.TileMode.CLAMP
        );
        backgroundPaint.setShader(gradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPaint(backgroundPaint);
    }
}