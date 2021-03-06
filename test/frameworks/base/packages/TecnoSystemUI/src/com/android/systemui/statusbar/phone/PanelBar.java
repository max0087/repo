/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.widget.Toast;
import com.android.systemui.R;

public abstract class PanelBar extends FrameLayout {
    public static final boolean DEBUG = false;
    public static final String TAG = PanelBar.class.getSimpleName();
    private static final boolean SPEW = false;
	
    // A BUG_ID:NONE by zyn 20150106 {
    private Context mContext;
    private Toast mToast;
    // A }

    public static final void LOG(String fmt, Object... args) {
        if (!DEBUG) return;
        Log.v(TAG, String.format(fmt, args));
    }

    public static final int STATE_CLOSED = 0;
    public static final int STATE_OPENING = 1;
    public static final int STATE_OPEN = 2;

    PanelHolder mPanelHolder;
    ArrayList<PanelView> mPanels = new ArrayList<PanelView>();
    PanelView mTouchingPanel;
    private int mState = STATE_CLOSED;
    private boolean mTracking;

    float mPanelExpandedFractionSum;

    public void go(int state) {
        if (DEBUG) LOG("go state: %d -> %d", mState, state);
        mState = state;
    }

    public PanelBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        // A BUG_ID:NONE by zyn 20150106 {
        mContext = context;
        // A }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void addPanel(PanelView pv) {
        mPanels.add(pv);
        pv.setBar(this);
    }

    public void setPanelHolder(PanelHolder ph) {
        if (ph == null) {
            Log.e(TAG, "setPanelHolder: null PanelHolder", new Throwable());
            return;
        }
        ph.setBar(this);
        mPanelHolder = ph;
        final int N = ph.getChildCount();
        for (int i=0; i<N; i++) {
            final View v = ph.getChildAt(i);
            if (v != null && v instanceof PanelView) {
                addPanel((PanelView) v);
            }
        }
    }

    public void setBouncerShowing(boolean showing) {
        int important = showing ? IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                : IMPORTANT_FOR_ACCESSIBILITY_AUTO;

        setImportantForAccessibility(important);

        if (mPanelHolder != null) {
            mPanelHolder.setImportantForAccessibility(important);
        }
    }

    public float getBarHeight() {
        return getMeasuredHeight();
    }

    public PanelView selectPanelForTouch(MotionEvent touch) {
        final int N = mPanels.size();
        return mPanels.get((int)(N * touch.getX() / getMeasuredWidth()));
    }

    public boolean panelsEnabled() {
        return true;
    }
	
     // A BUG_ID:NONE by zyn 20150106 {
    private boolean isSuperPowerSavingMode() {
        boolean isSuperPowerSavingMode = Settings.Global.getInt(mContext.getContentResolver(),
                Settings.Global.ULTRA_POWER_MODE, 0) == 1;
        Log.d(TAG,"isSuperPowerSavingMode: "+isSuperPowerSavingMode);
        return isSuperPowerSavingMode;
    }
    
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showToast();
        }
        
    };
    
    private Runnable mShowToastRunnable = new Runnable () {

        @Override
        public void run() {
            showToast();
        }
        
    };
    
    private void showToast() {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(mContext, R.string.super_power_saving_mode, Toast.LENGTH_SHORT);
        mToast.show();
    }
    // A }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Allow subclasses to implement enable/disable semantics
		
        // A BUG_ID:NONE by zyn 20150106 {
        if (isSuperPowerSavingMode()) {
            mHandler.removeCallbacks(mShowToastRunnable);
            mHandler.postDelayed(mShowToastRunnable, 200);
            return false;
        }
        // A }
        if (!panelsEnabled()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.v(TAG, String.format("onTouch: all panels disabled, ignoring touch at (%d,%d)",
                        (int) event.getX(), (int) event.getY()));
            }
            return false;
        }

        // figure out which panel needs to be talked to here
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            final PanelView panel = selectPanelForTouch(event);
            if (panel == null) {
                // panel is not there, so we'll eat the gesture
                Log.v(TAG, String.format("onTouch: no panel for touch at (%d,%d)",
                        (int) event.getX(), (int) event.getY()));
                mTouchingPanel = null;
                return true;
            }
            boolean enabled = panel.isEnabled();
            if (DEBUG) LOG("PanelBar.onTouch: state=%d ACTION_DOWN: panel %s %s", mState, panel,
                    (enabled ? "" : " (disabled)"));
            if (!enabled) {
                // panel is disabled, so we'll eat the gesture
                Log.v(TAG, String.format(
                        "onTouch: panel (%s) is disabled, ignoring touch at (%d,%d)",
                        panel, (int) event.getX(), (int) event.getY()));
                mTouchingPanel = null;
                return true;
            }
            startOpeningPanel(panel);
        }
        final boolean result = mTouchingPanel != null
                ? mTouchingPanel.onTouchEvent(event)
                : true;
        return result;
    }

    // called from PanelView when self-expanding, too
    public void startOpeningPanel(PanelView panel) {
        if (DEBUG) LOG("startOpeningPanel: " + panel);
        mTouchingPanel = panel;
        mPanelHolder.setSelectedPanel(mTouchingPanel);
        for (PanelView pv : mPanels) {
            if (pv != panel) {
                pv.collapse(false /* delayed */, 1.0f /* speedUpFactor */);
            }
        }
    }

    public abstract void panelScrimMinFractionChanged(float minFraction);

    /**
     * @param panel the panel which changed its expansion state
     * @param frac the fraction from the expansion in [0, 1]
     * @param expanded whether the panel is currently expanded; this is independent from the
     *                 fraction as the panel also might be expanded if the fraction is 0
     */
    public void panelExpansionChanged(PanelView panel, float frac, boolean expanded) {
        boolean fullyClosed = true;
        PanelView fullyOpenedPanel = null;
        if (SPEW) LOG("panelExpansionChanged: start state=%d panel=%s", mState, panel.getName());
        mPanelExpandedFractionSum = 0f;
        for (PanelView pv : mPanels) {
            pv.setVisibility(expanded ? View.VISIBLE : View.INVISIBLE);
            // adjust any other panels that may be partially visible
            if (expanded) {
                if (mState == STATE_CLOSED) {
                    go(STATE_OPENING);
                    onPanelPeeked();
                }
                fullyClosed = false;
                final float thisFrac = pv.getExpandedFraction();
                mPanelExpandedFractionSum += thisFrac;
                if (SPEW) LOG("panelExpansionChanged:  -> %s: f=%.1f", pv.getName(), thisFrac);
                if (panel == pv) {
                    if (thisFrac == 1f) fullyOpenedPanel = panel;
                }
            }
        }
        mPanelExpandedFractionSum /= mPanels.size();
        if (fullyOpenedPanel != null && !mTracking) {
            go(STATE_OPEN);
            onPanelFullyOpened(fullyOpenedPanel);
        } else if (fullyClosed && !mTracking && mState != STATE_CLOSED) {
            go(STATE_CLOSED);
            onAllPanelsCollapsed();
        }

        if (SPEW) LOG("panelExpansionChanged: end state=%d [%s%s ]", mState,
                (fullyOpenedPanel!=null)?" fullyOpened":"", fullyClosed?" fullyClosed":"");
    }

    public void collapseAllPanels(boolean animate, boolean delayed, float speedUpFactor) {
        boolean waiting = false;
        for (PanelView pv : mPanels) {
            if (animate && !pv.isFullyCollapsed()) {
                pv.collapse(delayed, speedUpFactor);
                waiting = true;
            } else {
                pv.resetViews();
                pv.setExpandedFraction(0); // just in case
                pv.cancelPeek();
            }
        }
        if (DEBUG) LOG("collapseAllPanels: animate=%s waiting=%s", animate, waiting);
        if (!waiting && mState != STATE_CLOSED) {
            // it's possible that nothing animated, so we replicate the termination
            // conditions of panelExpansionChanged here
            go(STATE_CLOSED);
            onAllPanelsCollapsed();
        }
    }

    public void onPanelPeeked() {
        if (DEBUG) LOG("onPanelPeeked");
    }

    public void onAllPanelsCollapsed() {
        if (DEBUG) LOG("onAllPanelsCollapsed");
    }

    public void onPanelFullyOpened(PanelView openPanel) {
        if (DEBUG) LOG("onPanelFullyOpened");
    }

    public void onTrackingStarted(PanelView panel) {
        mTracking = true;
    }

    public void onTrackingStopped(PanelView panel, boolean expand) {
        mTracking = false;
    }

    public void onExpandingFinished() {
        if (DEBUG) LOG("onExpandingFinished");
    }

    public void onClosingFinished() {

    }
}
