package com.example.my_application;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Random;

public class DynamicBackground extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread;
    private ArrayList<MovingShape> shapes;
    private Random random;
    private int[] colors = {Color.parseColor("#FFDDDD"), Color.parseColor("#FFCC66"), Color.parseColor("#FF9999")};

    public DynamicBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        shapes = new ArrayList<>();
        random = new Random();
    }

    private void initShapes() {
        shapes.clear();
        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0) return; // Prevent invalid random bounds
        for (int i = 0; i < 20; i++) {
            shapes.add(new MovingShape(random.nextInt(width), random.nextInt(height),
                    random.nextInt(50) + 30, colors[random.nextInt(colors.length)], random));
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initShapes(); // Ensure shapes are initialized after the view size is available
        drawThread = new DrawThread(getHolder(), shapes);
        drawThread.setRunning(true);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class MovingShape {
        int x, y, radius, color;
        int dx, dy;

        MovingShape(int x, int y, int radius, int color, Random random) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
            this.dx = random.nextInt(10) - 5;
            this.dy = random.nextInt(10) - 5;
        }

        void move(int width, int height) {
            x += dx;
            y += dy;
            if (x <= 0 || x >= width) dx *= -1;
            if (y <= 0 || y >= height) dy *= -1;
        }
    }

    private class DrawThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private boolean running;
        private Paint paint;
        private ArrayList<MovingShape> shapes;

        public DrawThread(SurfaceHolder holder, ArrayList<MovingShape> shapes) {
            this.surfaceHolder = holder;
            this.shapes = shapes;
            paint = new Paint();
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            while (running) {
                Canvas canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    if (canvas == null) continue;
                    canvas.drawColor(Color.WHITE);
                    for (MovingShape shape : shapes) {
                        paint.setColor(shape.color);
                        canvas.drawCircle(shape.x, shape.y, shape.radius, paint);
                        shape.move(canvas.getWidth(), canvas.getHeight());
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
