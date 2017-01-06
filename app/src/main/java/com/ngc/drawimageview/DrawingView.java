package com.ngc.drawimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by neogeekscamp on 1/6/17.
 */

public class DrawingView extends View {

    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint, circlePaint, outercirclePaint;
    private ArrayList<Path> paths = new ArrayList<>();
    private ArrayList<Path> undonePaths = new ArrayList<>();

    public DrawingView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        circlePaint = new Paint();
        mPaint = new Paint();
        outercirclePaint = new Paint();
        outercirclePaint.setAntiAlias(true);
        circlePaint.setAntiAlias(true);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        outercirclePaint.setColor(0x44FFFFFF);
        circlePaint.setColor(0xAADD5522);
        outercirclePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStyle(Paint.Style.FILL);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);
        outercirclePaint.setStrokeWidth(6);
        mCanvas = new Canvas();

        mPath = new Path();
        paths.add(mPath);
    }

    public void colorChanged(int color) {
        mPaint.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Path p : paths) {
            canvas.drawPath(p, mPaint);
        }
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 0;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath = new Path();
        paths.add(mPath);
    }

    public void onClickClear() {
        if (paths.size() > 0) {
            undonePaths.clear();
            paths.clear();
            mPath = new Path();
            paths.add(mPath);
            invalidate();
        }
    }

    public void onClickUndo() {
        if (paths.size() - 2 >= 0) {
            undonePaths.add(paths.remove(paths.size() - 2));
            invalidate();
        }
    }

    public void onClickRedo() {
        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            invalidate();
        }
    }


    public Bitmap getBitmap() {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();

        if (getBackground() != null) {
            Drawable drawable = getBackground();
            drawable.draw(mCanvas);
        }

        Bitmap bitmap = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);

        return bitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
}