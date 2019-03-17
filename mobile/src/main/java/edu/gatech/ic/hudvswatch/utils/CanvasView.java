package edu.gatech.ic.hudvswatch.utils;

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

    private static final String TAG = CanvasView.class.getName();

    // Constants
    static final int TEXT_PADDING_LEFT = 10;
    static final float TEXT_STROKE_WIDTH = 2f;
    static final float TEXT_SIZE = 24f;
    static final int CANVAS_MARGIN_TOP = 50;

    Bitmap mBitmap;
    Canvas mCanvas;
    Context context;
    Paint mTextPaint;

    int canvasXStart, canvasXEnd, canvasYStart, canvasYEnd;
    int canvasWidth, canvasHeight;
    int detailXStart, detailXEnd, detailYStart, detailYEnd;

    StudyRunInformation studyRunInformation;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setStrokeJoin(Paint.Join.ROUND);
        mTextPaint.setStrokeWidth(TEXT_STROKE_WIDTH);
        mTextPaint.setTextSize(TEXT_SIZE);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        canvasXStart = 0;
        canvasXEnd = (int) (mCanvas.getWidth() * 0.80);
        canvasYStart = 0;
        canvasYEnd = mCanvas.getHeight();

        canvasWidth = canvasXEnd - canvasXStart;
        canvasHeight = canvasYEnd - canvasYStart;

        detailXStart = canvasXEnd;
        detailXEnd = mCanvas.getWidth();
        detailYStart = 0;
        detailYEnd = mCanvas.getHeight();

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
        VisualSearchTaskGrid visualSearchTaskGrid = RandomGridGenerator.getInstance().getNextGrid();

        final int BORDER = 100;
        int xCellCount = RandomGridGenerator.getInstance().getGridWidth();
        int yCellCount = RandomGridGenerator.getInstance().getGridHeight();
        final int X_DISTANCE = ((canvasWidth - BORDER) / xCellCount);
        final int Y_DISTANCE = ((canvasHeight - BORDER) / yCellCount);

        Log.i(TAG, String.format("Canvas Width = %d, Canvas Height = %d", mCanvas.getWidth(), mCanvas.getHeight()));
        Log.i(TAG, String.format("Canvas X Dist = %d, Canvas Y Dist = %d", X_DISTANCE, Y_DISTANCE));

        for (int i = 0; i < xCellCount; i++) {
            for (int j = 0; j < yCellCount; j++) {
                int value = visualSearchTaskGrid.getGrid()[i][j];
                if (value == 0) {
                    continue;
                }

                int x = canvasXStart + BORDER / 2 + i * X_DISTANCE;
                int y = canvasYStart + BORDER / 2 + j * Y_DISTANCE;
                canvas.drawText(Integer.toString(value), x, y, mTextPaint);
            }
        }

        canvas.drawLine(canvasXEnd, canvasYStart, canvasXEnd, canvasYEnd, mTextPaint);

        canvas.drawText(studyRunInformation.condition, detailXStart + TEXT_PADDING_LEFT, detailYStart + CANVAS_MARGIN_TOP, mTextPaint);
        canvas.drawText(studyRunInformation.subjectId, detailXStart + TEXT_PADDING_LEFT, detailYStart + CANVAS_MARGIN_TOP + 50, mTextPaint);
        canvas.drawText(studyRunInformation.isTraining ? "Training" : "Testing", detailXStart + TEXT_PADDING_LEFT, detailYStart + CANVAS_MARGIN_TOP + 100, mTextPaint);

        Log.i(TAG, String.format("Drew grid %s target number containing %d values.", visualSearchTaskGrid.doesContainTargetNumber() ? "with" : "without", visualSearchTaskGrid.getNumberOfValuesInGrid()));
    }

    public void setStudyRunInformation(StudyRunInformation studyRunInformation) {
        this.studyRunInformation = studyRunInformation;
    }
}
