package edu.gatech.ic.hudvswatch.ui;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;


public class ConfirmButton extends AppCompatButton implements View.OnClickListener {
    private static final int DEFAULT_CONFIRM_TAPS = 2;
    int mConfirmTapsLeft = DEFAULT_CONFIRM_TAPS;
    String mDefaultText;
    OnConfirmedListener mOnConfirmedListener;

    public ConfirmButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ConfirmButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ConfirmButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (--mConfirmTapsLeft == 0) {
            mOnConfirmedListener.onConfirmed(this);
            resetButton();
        } else {
            setText(String.format("%s (%d x confirm?)", mDefaultText, mConfirmTapsLeft));
        }
    }

    public void setDefaultText(String text) {
        mDefaultText = text;
    }

    public void resetButton() {
        mConfirmTapsLeft = DEFAULT_CONFIRM_TAPS;
        setText(mDefaultText);
    }

    public void setOnConfirmedListener(OnConfirmedListener onConfirmedListener) {
        mOnConfirmedListener = onConfirmedListener;
    }

    public interface OnConfirmedListener {
        void onConfirmed(View v);
    }
}
