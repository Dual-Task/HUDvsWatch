package edu.gatech.ic.hudvswatch.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import edu.gatech.ic.hudvswatch.utils.Assert;
import edu.gatech.ic.hudvswatch.utils.RandomGridGenerator;
import edu.gatech.ic.hudvswatch.models.VisualSearchTaskGrid;


/**
 * Handles drawing a number grid to the screen.
 */
public class VisualSearchView extends View {

    private static final String TAG = VisualSearchView.class.getName();

    //region Duration Values
    private static final int COUNTDOWN_DISPLAY_DURATION = 1000;
    private static final int VISUAL_SEARCH_DISPLAY_DURATION = 5000;
    //endregion Duration Values

    // Used to decide what state we are in displaying a search task
    private enum Mode {
        START,
        COUNTDOWN,
        VISUAL_SEARCH,
        COMPLETED,
    }

    //region Text Paints
    static class TextPaints {

        static Paint mStartTextPaint = new Paint() {{
            setAntiAlias(true);
            setColor(Color.BLACK);
            setStyle(Paint.Style.STROKE);
            setStrokeJoin(Paint.Join.ROUND);
            setStrokeWidth(6f);
            setTextSize(120f);
        }};

        static Paint mCompletedTextPaint = new Paint() {{
            setAntiAlias(true);
            setColor(Color.GREEN);
            setStyle(Paint.Style.STROKE);
            setStrokeJoin(Paint.Join.ROUND);
            setStrokeWidth(5f);
            setTextSize(96f);
        }};

        static Paint mCountdownTextPaint = new Paint() {{
            setAntiAlias(true);
            setColor(Color.GREEN);
            setStyle(Paint.Style.STROKE);
            setStrokeJoin(Paint.Join.ROUND);
            setStrokeWidth(6f);
            setTextSize(96f);
        }};

        static Paint mNumberTextPaint = new Paint() {{
            setAntiAlias(true);
            setColor(Color.BLACK);
            setStyle(Paint.Style.STROKE);
            setStrokeJoin(Paint.Join.ROUND);
            setStrokeWidth(2f);
            setTextSize(36f);
        }};

    }
    //endregion Text Paints

    //region Instance Members

    // For TimerTasks
    private int mStartConfirmTapsLeft = 3;
    private int mCurrentCountdownValue = 6;
    private int mVisualSearchTasksLeft = 10;

    // Other instance fields
    private Random mRandom = new Random(42);
    private Timer mVisualSearchTaskViewTimer = new Timer();
    private Timer mNotificationTimer = new Timer();
    VisualSearchTaskGrid mVisualSearchTaskGrid;
    private Mode mMode = Mode.START;
    Canvas mCanvas;
    VisualSearchViewEventsListener mVisualSearchViewEventsListener;

    private int mCanvasXStart, mCanvasXEnd, mCanvasYStart, mCanvasYEnd;
    private int mCanvasWidth, mCanvasHeight;

    //endregion Instance Members

    public VisualSearchView(Context c, AttributeSet attrs) {
        super(c, attrs);
    }

    //region Overrides

    /**
     * Ensures that the width is constrained to the height, maintaining a square search grid.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        setMeasuredDimension(height, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bitmap);

        mCanvasXStart = 0;
        mCanvasXEnd = mCanvas.getHeight();
        mCanvasYStart = 0;
        mCanvasYEnd = mCanvas.getHeight();

        mCanvasWidth = mCanvasXEnd - mCanvasXStart;
        mCanvasHeight = mCanvasYEnd - mCanvasYStart;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mCanvas = canvas;
        switch (mMode) {
            case START:
                this.drawStartView();
                break;
            case COUNTDOWN:
                this.drawNextCountdownValue();
                break;
            case VISUAL_SEARCH:
                this.drawNextGrid();
                break;
            case COMPLETED:
                this.drawCompletedView();
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Make sure the user taps the Start screen a few times to confirm
        if (mMode == Mode.START) {
            if (--mStartConfirmTapsLeft == 0) {
                mMode = Mode.COUNTDOWN;
                mVisualSearchTaskViewTimer.cancel();
                mVisualSearchTaskViewTimer.purge();
                mVisualSearchTaskViewTimer = new Timer();
                mVisualSearchTaskViewTimer.scheduleAtFixedRate(new CountdownTimerTask(), 0, COUNTDOWN_DISPLAY_DURATION);
            } else {
                this.triggerRedraw();
            }
        }

        return super.onTouchEvent(event);
    }

    //endregion Overrides

    //region Drawing

    private void drawStartView() {
        this.drawTextInViewCenter(String.format("START (%d)", mStartConfirmTapsLeft), TextPaints.mStartTextPaint);
    }

    private void drawCompletedView() {
        this.drawTextInViewCenter("COMPLETED", TextPaints.mCompletedTextPaint);
    }

    private void drawNextCountdownValue() {
        this.drawTextInViewCenter(Integer.toString(mCurrentCountdownValue), TextPaints.mCountdownTextPaint);
    }

    private void drawNextGrid() {
        mVisualSearchTaskGrid = RandomGridGenerator.getInstance().getNextGrid();

        final int BORDER = 100;
        int xCellCount = RandomGridGenerator.getInstance().getGridWidth();
        int yCellCount = RandomGridGenerator.getInstance().getGridHeight();
        final int X_DISTANCE = ((mCanvasWidth - BORDER) / xCellCount);
        final int Y_DISTANCE = ((mCanvasHeight - BORDER) / yCellCount);

        Log.i(TAG, String.format("Canvas Width = %d, Canvas Height = %d", mCanvas.getWidth(), mCanvas.getHeight()));
        Log.i(TAG, String.format("Canvas X Dist = %d, Canvas Y Dist = %d", X_DISTANCE, Y_DISTANCE));

        for (int i = 0; i < xCellCount; i++) {
            for (int j = 0; j < yCellCount; j++) {
                int value = mVisualSearchTaskGrid.getGrid()[i][j];
                if (value == 0) {
                    continue;
                }

                int x = mCanvasXStart + BORDER / 2 + i * X_DISTANCE;
                int y = mCanvasYStart + BORDER / 2 + j * Y_DISTANCE;
                mCanvas.drawText(Integer.toString(value), x, y, TextPaints.mNumberTextPaint);
            }
        }

        mCanvas.drawRect(0, 0, mCanvasXEnd, mCanvasYEnd, TextPaints.mNumberTextPaint);

        Log.i(TAG, String.format("Drew grid %s target number containing %d values.", mVisualSearchTaskGrid.doesContainTargetNumber() ? "with" : "without", mVisualSearchTaskGrid.getNumberOfValuesInGrid()));
    }

    //endregion Drawing

    //region Timer Methods

    public void startNewVisualSearchTaskAfterYesOrNoPressed() {
        Assert.that(mMode == Mode.VISUAL_SEARCH);
        mVisualSearchTaskViewTimer.cancel();
        mVisualSearchTaskViewTimer.purge();
        mVisualSearchTaskViewTimer = new Timer();
        mVisualSearchTaskViewTimer.scheduleAtFixedRate(new VisualSearchTimerTask(), 0, VISUAL_SEARCH_DISPLAY_DURATION);
    }

    private void queueNextNotification() {
        mNotificationTimer.cancel();
        mNotificationTimer.purge();
        mNotificationTimer = new Timer();
        mNotificationTimer.schedule(new SendNotificationTask(), getNextNotificationDelayMS());
    }

    //endregion Timer Methods

    //region Helpers

    private void triggerRedraw() {
        ((Activity) VisualSearchView.this.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                VisualSearchView.this.invalidate();
            }
        });
    }

    private void drawTextInViewCenter(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (mCanvasXStart + mCanvasXEnd) / 2 - (bounds.width() / 2);
        int y = (mCanvasYStart + mCanvasYEnd) / 2 + (bounds.height() / 2);
        mCanvas.drawText(text, x, y, paint);
    }

    private int getNextNotificationDelayMS() {
        // Values from [4, 5, 6, 7]
        return (4 + mRandom.nextInt(4)) * 1000;
    }

    private int getNextNotificationNumber() {
        return 1 + mRandom.nextInt(3);
    }

    //endregion Helpers

    private class CountdownTimerTask extends TimerTask {
        @Override
        public void run() {
            Assert.that(mMode == Mode.COUNTDOWN);

            if (mCurrentCountdownValue == 1) {
                // Cancel TimerTask and Timer
                this.cancel();
                mVisualSearchTaskViewTimer.cancel();
                mVisualSearchTaskViewTimer.purge();
                // Start the VisualSearchTimerTask
                mMode = Mode.VISUAL_SEARCH;
                mVisualSearchTaskViewTimer = new Timer();
                mVisualSearchTaskViewTimer.scheduleAtFixedRate(new VisualSearchTimerTask(), 0, VISUAL_SEARCH_DISPLAY_DURATION);
                mVisualSearchViewEventsListener.onVisualSearchTaskFirstStart();

                queueNextNotification();

                return;
            }

            VisualSearchView.this.triggerRedraw();

            Log.d(TAG, "Current countdown: " + mCurrentCountdownValue);
            mCurrentCountdownValue--;

        }
    }

    private class VisualSearchTimerTask extends TimerTask {
        @Override
        public void run() {
            Assert.that(mMode == Mode.VISUAL_SEARCH);

            if (mVisualSearchTasksLeft == 0) {
                this.cancel();
                mVisualSearchTaskViewTimer.cancel();
                // Show the completed screen
                mMode = Mode.COMPLETED;
                mVisualSearchViewEventsListener.onVisualSearchTaskCompleted();
                VisualSearchView.this.triggerRedraw();
                return;
            }

            VisualSearchView.this.triggerRedraw();

            Log.d(TAG, "Number of remaining search tasks: " + mVisualSearchTasksLeft);
            mVisualSearchTasksLeft--;
        }
    }

    private class SendNotificationTask extends TimerTask {
        @Override
        public void run() {
            // Send the notification now
            int nextNotificationNumber = getNextNotificationNumber();
            mVisualSearchViewEventsListener.onActivityShouldSendNotification(nextNotificationNumber);
            // Queue the next one
            queueNextNotification();
        }
    }

    public void setVisualSearchTaskEventsListener(VisualSearchViewEventsListener listener) {
        mVisualSearchViewEventsListener = listener;
    }

    public interface VisualSearchViewEventsListener {
        void onVisualSearchTaskFirstStart();
        void onActivityShouldSendNotification(int number);
        void onVisualSearchTaskCompleted();
    }
}
