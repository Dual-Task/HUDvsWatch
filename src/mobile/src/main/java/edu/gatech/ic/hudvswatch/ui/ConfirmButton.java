package edu.gatech.ic.hudvswatch.ui;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;


/**
 * A button that requires two taps to confirm an action
 */
public class ConfirmButton extends AppCompatButton implements View.OnClickListener {
    boolean mIsTappedOnce;
    String mDefaultText;
    ConfirmButtonListener mConfirmButtonListener;

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
        if (!mIsTappedOnce) {
            mIsTappedOnce = true;
            mConfirmButtonListener.onFirstTap();
            setText(String.format("%s (tap again to confirm)", mDefaultText));
        } else {
            mConfirmButtonListener.onConfirmed();
            resetButton();
        }
    }

    public void setDefaultText(String text) {
        mDefaultText = text;
    }

    public void resetButton() {
        mIsTappedOnce = false;
        setText(mDefaultText);
    }

    public void setOnConfirmedListener(ConfirmButtonListener confirmButtonListener) {
        mConfirmButtonListener = confirmButtonListener;
    }

    public interface ConfirmButtonListener {
        void onFirstTap();
        void onConfirmed();
    }
}
