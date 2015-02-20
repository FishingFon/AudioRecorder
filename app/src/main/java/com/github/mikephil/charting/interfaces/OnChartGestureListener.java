
package com.github.mikephil.charting.interfaces;

import android.view.MotionEvent;

/**
 * Listener for callbacks when doing gestures on the chart.
 * 
 * @author Philipp Jahoda
 */
public interface OnChartGestureListener {
    public void onChartTouched(MotionEvent me);


    /**
     * Callbacks when the chart is longpressed.
     * 
     * @param me
     */
    public void onChartLongPressed(MotionEvent me);

    /**
     * Callbacks when the chart is double-tapped.
     * 
     * @param me
     */
    public void onChartDoubleTapped(MotionEvent me);

    /**
     * Callbacks when the chart is single-tapped.
     * 
     * @param me
     */
    public void onChartSingleTapped(MotionEvent me);

    /**
     * Callbacks then a fling gesture is made on the chart.
     * 
     * @param me1
     * @param me2
     * @param velocityX
     * @param velocityY
     */
    public void onChartTouchReleased(MotionEvent me);
    public void onChartZoomed(MotionEvent me);

    public void onChartDragged(MotionEvent me);
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY);
}
