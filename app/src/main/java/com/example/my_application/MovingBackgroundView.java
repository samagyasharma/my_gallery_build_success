package com.example.my_application;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MovingBackgroundView extends View {
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
    private final Random random = new Random();
    private final int[] colors = {Color.parseColor("#FED981"), Color.parseColor("#FAA0A0")}; // Gold, Hot Pink

    public MovingBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //FF69B4//FFD700
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initShapes(w, h);
    }

    private void initShapes(int width, int height) {
        shapes.clear();
        for (int i = 0; i < 10; i++) {
            float x = random.nextFloat() * width;
            float y = random.nextFloat() * height;
            float radius = 150 + random.nextFloat() * 200; // Larger shapes
            float dx = (random.nextFloat() - 0.5f) * 6;
            float dy = (random.nextFloat() - 0.5f) * 6;
            int color = colors[random.nextInt(colors.length)];
            shapes.add(new Shape(x, y, radius, dx, dy, color));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Fill the background with a gradient effect
        canvas.drawColor(Color.parseColor("#F4C2C2")); // Light Pink background

        for (Shape shape : shapes) {
            paint.setShader(new RadialGradient(
                    shape.x, shape.y, shape.radius,
                    shape.color, Color.TRANSPARENT,
                    Shader.TileMode.CLAMP
            ));
            canvas.drawCircle(shape.x, shape.y, shape.radius, paint);
        }

        updateShapes();
        postInvalidateDelayed(16); // Smooth animation at 60 FPS
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
