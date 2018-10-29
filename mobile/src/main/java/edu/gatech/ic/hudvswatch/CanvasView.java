package edu.gatech.ic.hudvswatch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CanvasView extends View {

    Bitmap mBitmap;
    Canvas mCanvas;
    Context context;
    Paint mTextPaint;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        // and we set a new Paint with the desired attributes
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setStrokeJoin(Paint.Join.ROUND);
        mTextPaint.setStrokeWidth(1f);
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        super.onDraw(canvas);
        this.drawNextGrid(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.invalidate();
        return super.onTouchEvent(event);
    }

    private void drawNextGrid(Canvas canvas) {
        int[][] numberGrid = RandomGridGenerator.getInstance().getNextGrid();

        final int BORDER = 100;
        int xCellCount = RandomGridGenerator.getInstance().getGridWidth();
        int yCellCount = RandomGridGenerator.getInstance().getGridHeight();
        final int X_DISTANCE = ((mCanvas.getWidth() - BORDER) / xCellCount);
        final int Y_DISTANCE = ((mCanvas.getHeight() - BORDER) / yCellCount);

//        final int X_DISTANCE = 10;
//        final int Y_DISTANCE = 10;

        Log.i("draw", String.format("Canvas Width = %d, Canvas Height = %d", mCanvas.getWidth(), mCanvas.getHeight()));
        Log.i("draw", String.format("Canvas X Dist = %d, Canvas Y Dist = %d", X_DISTANCE, Y_DISTANCE));

        for (int i = 0; i < xCellCount; i++) {
            for (int j = 0; j < yCellCount; j++) {
                int value = numberGrid[i][j];
                if (value == 0) {
                    continue;
                }
                canvas.drawText(Integer.toString(value), BORDER / 2 + i * X_DISTANCE, BORDER / 2 + j * Y_DISTANCE, mTextPaint);
            }
        }
    }

}