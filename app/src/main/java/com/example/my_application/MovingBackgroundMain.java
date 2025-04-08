package com.example.my_application;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MovingBackgroundMain extends View {
    private static class Shape {
        float x, y, radius;
        float dx, dy;
        int color;

        Shape(float x, float y, float radius, float dx, float dy, int color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.dx = dx;
            this.dy = dy;
            this.color = color;
        }
    }

    private final List<Shape> shapes = new ArrayList<>();
    private final Paint paint = new Paint();
    private final Paint backgroundPaint = new Paint();
    private final Random random = new Random();
    // Remove final modifier from colors array
    private int[] colors = {
        Color.parseColor("#FFB6C1"),  // Light pink for shapes
        Color.parseColor("#FF69B4")   // Hot pink for shapes
    };

    public MovingBackgroundMain(Context context, AttributeSet attrs) {
        super(context, attrs);
        backgroundPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initShapes(w, h);
        
        // Create a darker but still distinctly purple gradient background
        LinearGradient gradient = new LinearGradient(
            0, 0,
            0, h,
            new int[]{
                Color.parseColor("#BEB1E5"),  // Darker purple at top
                Color.parseColor("#A594D3"),  // Medium darker purple in middle
                Color.parseColor("#9B7CB9")   // Darkest purple at bottom (still not indigo)
            },
            new float[]{0f, 0.5f, 1f},
            Shader.TileMode.CLAMP
        );
        backgroundPaint.setShader(gradient);
    }

    private void initShapes(int width, int height) {
        shapes.clear();
        
        for (int i = 0; i < 10; i++) {
            float x = random.nextFloat() * width;
            float y = random.nextFloat() * height;
            float radius = 150 + random.nextFloat() * 200;
            float dx = (random.nextFloat() - 0.5f) * 4;
            float dy = (random.nextFloat() - 0.5f) * 4;
            int color = colors[random.nextInt(colors.length)];
            shapes.add(new Shape(x, y, radius, dx, dy, color));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the gradient background
        canvas.drawPaint(backgroundPaint);

        // Draw the floating shapes with softer colors
        for (Shape shape : shapes) {
            paint.setShader(new RadialGradient(
                shape.x, shape.y, shape.radius,
                shape.color, Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            ));
            canvas.drawCircle(shape.x, shape.y, shape.radius, paint);
        }

        updateShapes();
        postInvalidateDelayed(16);
    }

    private void updateShapes() {
        for (Shape shape : shapes) {
            shape.x += shape.dx;
            shape.y += shape.dy;

            if (shape.x - shape.radius < 0 || shape.x + shape.radius > getWidth()) {
                shape.dx *= -1;
            }
            if (shape.y - shape.radius < 0 || shape.y + shape.radius > getHeight()) {
                shape.dy *= -1;
            }
        }
    }
}