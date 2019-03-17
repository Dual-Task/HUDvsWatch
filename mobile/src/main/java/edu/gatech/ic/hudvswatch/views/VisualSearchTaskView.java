package edu.gatech.ic.hudvswatch.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import edu.gatech.ic.hudvswatch.utils.RandomGridGenerator;
import edu.gatech.ic.hudvswatch.models.VisualSearchTaskGrid;


/**
 * Handles drawing a number grid to the screen.
 */
public class VisualSearchTaskView extends View {

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int h = getMeasuredHeight();
        setMeasuredDimension(h, h);
    }

    private static final String TAG = VisualSearchTaskView.class.getName();

    // Constants
    private static final float TEXT_STROKE_WIDTH = 2f;
    private static final float TEXT_SIZE = 24f;

    VisualSearchTaskGrid mVisualSearchTaskGrid;

    Canvas mCanvas;
    Paint mTextPaint = new Paint() {{
        setAntiAlias(true);
        setColor(Color.BLACK);
        setStyle(Paint.Style.STROKE);
        setStrokeJoin(Paint.Join.ROUND);
        setStrokeWidth(TEXT_STROKE_WIDTH);
        setTextSize(TEXT_SIZE);
    }};

    private int canvasXStart, canvasXEnd, canvasYStart, canvasYEnd;
    private int canvasWidth, canvasHeight;

    public VisualSearchTaskView(Context c, AttributeSet attrs) {
        super(c, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bitmap);

        canvasXStart = 0;
        canvasXEnd = mCanvas.getHeight();
        canvasYStart = 0;
        canvasYEnd = mCanvas.getHeight();

        canvasWidth = canvasXEnd - canvasXStart;
        canvasHeight = canvasYEnd - canvasYStart;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        super.onDraw(canvas);
        this.drawNextGrid(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.invalidate();  // causes re-draw
        return super.onTouchEvent(event);
    }

    private void drawNextGrid(Canvas canvas) {
        mVisualSearchTaskGrid = RandomGridGenerator.getInstance().getNextGrid();

        final int BORDER = 100;
        int xCellCount = RandomGridGenerator.getInstance().getGridWidth();
        int yCellCount = RandomGridGenerator.getInstance().getGridHeight();
        final int X_DISTANCE = ((canvasWidth - BORDER) / xCellCount);
        final int Y_DISTANCE = ((canvasHeight - BORDER) / yCellCount);

        Log.i(TAG, String.format("Canvas Width = %d, Canvas Height = %d", mCanvas.getWidth(), mCanvas.getHeight()));
        Log.i(TAG, String.format("Canvas X Dist = %d, Canvas Y Dist = %d", X_DISTANCE, Y_DISTANCE));

        for (int i = 0; i < xCellCount; i++) {
            for (int j = 0; j < yCellCount; j++) {
                int value = mVisualSearchTaskGrid.getGrid()[i][j];
                if (value == 0) {
                    continue;
                }

                int x = canvasXStart + BORDER / 2 + i * X_DISTANCE;
                int y = canvasYStart + BORDER / 2 + j * Y_DISTANCE;
                canvas.drawText(Integer.toString(value), x, y, mTextPaint);
            }
        }

        Log.i(TAG, String.format("Drew grid %s target number containing %d values.", mVisualSearchTaskGrid.doesContainTargetNumber() ? "with" : "without", mVisualSearchTaskGrid.getNumberOfValuesInGrid()));
    }

    public VisualSearchTaskGrid getVisualSearchTaskGrid() {
        return mVisualSearchTaskGrid;
    }
}
