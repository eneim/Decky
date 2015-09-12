/*
 * Copyright (c) 2015 Eneim Labs
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package im.ene.lab.decky;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

/**
 * Created by dionysis_lorentzos on 5/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinausaurs might appear!
 */
abstract class OnTopViewTouchListener implements View.OnTouchListener {

  static final int INVALID_POINTER_ID = -1;
  static final int TOUCH_ABOVE = 0;
  static final int TOUCH_BELOW = 1;
  static final int SELECT_ITEM_DURATION = 250;
  static final float MAX_COS = (float) Math.cos(Math.toRadians(45));
  private static final String TAG = OnTopViewTouchListener.class.getSimpleName();
  private static final Object LOCK = new Object();
  private final float mFrameX;
  private final float mFrameY;
  private final int mFrameHeight;
  private final int mFrameWidth;
  private final int mParentWidth;
  private final float mFrameHalfWidth;
  private float mBaseRotateDegree;
  private float mPosX;
  private float mPosY;
  private float mDownTouchX;
  private float mDownTouchY;
  private ValueAnimator mSwipeAnimator;
  // The active pointer is the one currently moving our object.
  private int mActivePointerId = INVALID_POINTER_ID;
  private ViewGroup mParent = null;
  private View mFrame = null;
  private int mTouchPosition;
  private boolean mIsAnimationRunning = false;

  public OnTopViewTouchListener(ViewGroup parent, View view) {
    this(parent, view, 15.f);
  }

  public OnTopViewTouchListener(ViewGroup parent, View frame, float rotationDegree) {
    this.mParent = parent;
    this.mFrame = frame;
    this.mFrameX = frame.getX();
    this.mFrameY = frame.getY();
    this.mFrameHeight = frame.getHeight();
    this.mFrameWidth = frame.getWidth();
    this.mFrameHalfWidth = mFrameWidth / 2f;
    this.mParentWidth = parent.getWidth();
    this.mBaseRotateDegree = rotationDegree;
  }

  @Override public boolean onTouch(View view, MotionEvent event) {
    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        // from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
        // Save the ID of this pointer
        mActivePointerId = event.getPointerId(0);
        float eventX = 0;
        float eventY = 0;
        boolean success = false;
        try {
          eventX = event.getX(mActivePointerId);
          eventY = event.getY(mActivePointerId);
          success = true;
        } catch (IllegalArgumentException e) {
          Log.w(TAG, "Exception in onTouch(view, event) : " + mActivePointerId, e);
        }
        if (success) {
          // Remember where we started
          mDownTouchX = eventX;
          mDownTouchY = eventY;
          //to prevent an initial jump of the magnifier, aposX and mPosY must
          //have the values from the magnifier mFrame
          if (mPosX == 0) {
            mPosX = mFrame.getX();
          }
          if (mPosY == 0) {
            mPosY = mFrame.getY();
          }

          if (eventY < mFrameHeight / 2) {
            mTouchPosition = TOUCH_ABOVE;
          } else {
            mTouchPosition = TOUCH_BELOW;
          }
        }

        view.getParent().requestDisallowInterceptTouchEvent(true);
        break;

      case MotionEvent.ACTION_UP:
        mActivePointerId = INVALID_POINTER_ID;
        onTouchUpAction();
        view.getParent().requestDisallowInterceptTouchEvent(false);
        break;

      case MotionEvent.ACTION_POINTER_DOWN:
        break;

      case MotionEvent.ACTION_POINTER_UP:
        // Extract the index of the pointer that left the touch sensor
        final int pointerIndex = (event.getAction()
            & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = event.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
          // This was our active pointer going up. Choose a new
          // active pointer and adjust accordingly.
          final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
          mActivePointerId = event.getPointerId(newPointerIndex);
        }
        break;
      case MotionEvent.ACTION_MOVE:
        // Find the index of the active pointer and fetch its position
        final int pointerIndexMove = event.findPointerIndex(mActivePointerId);
        final float xMove = event.getX(pointerIndexMove);
        final float yMove = event.getY(pointerIndexMove);

        //from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
        // Calculate the distance moved
        final float dx = xMove - mDownTouchX;
        final float dy = yMove - mDownTouchY;

        // Move the mFrame
        mPosX += dx;
        mPosY += dy;

        // calculate the rotation degrees
        float objectDiffX = mPosX - mFrameX;
        float rotation = mBaseRotateDegree * 2.f * objectDiffX / mParentWidth;
        if (mTouchPosition == TOUCH_BELOW) {
          rotation = -rotation;
        }

        //in this area would be code for doing something with the view as the mFrame moves.
        mFrame.setX(mPosX);
        mFrame.setY(mPosY);
        mFrame.setRotation(rotation);
        onFlingTopView(getFlingOffset());
        break;

      case MotionEvent.ACTION_CANCEL:
        mActivePointerId = INVALID_POINTER_ID;
        view.getParent().requestDisallowInterceptTouchEvent(false);
        break;
      default:
        break;
    }

    return true;
  }

  private boolean onTouchUpAction() {
    if (movedBeyondLeftBorder()) {
      // Left Swipe
      swipeTopView(true, getExitPoint(-mFrameWidth), SELECT_ITEM_DURATION).start();
    } else if (movedBeyondRightBorder()) {
      // Right Swipe
      swipeTopView(false, getExitPoint(mParentWidth), SELECT_ITEM_DURATION).start();
    } else {
      float absMoveDistance = Math.abs(mPosX - mFrameX);
      mPosX = 0;
      mPosY = 0;
      mDownTouchX = 0;
      mDownTouchY = 0;
      mFrame.animate()
          .setDuration(SELECT_ITEM_DURATION)
          .setInterpolator(new OvershootInterpolator(1.5f))
          .x(mFrameX)
          .y(mFrameY)
          .rotation(0);
      onFlingTopView(0.0f);
      if (absMoveDistance < 4.0) {
        onClickTopView(mFrame);
      }
    }
    return false;
  }

  /**
   * @param offset
   */
  abstract void onFlingTopView(float offset);

  private float getFlingOffset() {
    float zeroToOneValue =
        (mFrame.getX() + mFrameHalfWidth - mParent.getLeft())
            / (mParent.getRight() - mParent.getLeft());
    if (zeroToOneValue < 0) {
      zeroToOneValue = 0.f;
    }
    if (zeroToOneValue > 1) {
      zeroToOneValue = 1.f;
    }
    return zeroToOneValue * 2f - 1f;
  }

  private boolean movedBeyondLeftBorder() {
    return mFrame.getX() + mFrameHalfWidth < leftBorder();
  }

  public ValueAnimator swipeTopView(final boolean isSwipeToLeft, final float exitY, long duration) {
    mIsAnimationRunning = true;
    if (mSwipeAnimator != null && mSwipeAnimator.isRunning()) {
      mSwipeAnimator.cancel();
    }

    final float exitX;
    if (isSwipeToLeft) {
      exitX = -mFrameWidth - getRotationWidthOffset();
    } else {
      exitX = mParentWidth + getRotationWidthOffset();
    }

    final float fromX = mFrame.getLeft() + mFrame.getTranslationX();
    final float fromY = mFrame.getTop() + mFrame.getTranslationY();
    final float fromFactor = getFlingOffset();    // -1.0f .. 1.0f

    mSwipeAnimator = ValueAnimator.ofFloat(Math.abs(fromFactor), 1.0f);
    mSwipeAnimator.setDuration(duration);
    mSwipeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float factor = (float) animation.getAnimatedValue();
        float animatedX = exitX * factor + fromX * (1.f - factor);
        float animatedY = exitY * factor + fromY * (1.f - factor);
        mFrame.setX(animatedX);
        mFrame.setY(animatedY);
        mFrame.setRotation(getExitRotation(isSwipeToLeft, factor));
        onFlingTopView(isSwipeToLeft ? -factor : factor);
      }
    });

    mSwipeAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        if (isSwipeToLeft) {
          onExited(mFrame, StackView.Direction.LEFT);
        } else {
          onExited(mFrame, StackView.Direction.RIGHT);
        }
        mIsAnimationRunning = false;
      }
    });

    return mSwipeAnimator;
  }

  private float getExitPoint(int exitXPoint) {
    float[] x = new float[2];
    x[0] = mFrameX;
    x[1] = mPosX;

    float[] y = new float[2];
    y[0] = mFrameY;
    y[1] = mPosY;

    LinearRegression regression = new LinearRegression(x, y);

    // Your typical y = ax+b linear regression
    return (float) regression.slope() * exitXPoint + (float) regression.intercept();
  }

  private boolean movedBeyondRightBorder() {
    return mFrame.getX() + mFrameHalfWidth > rightBorder();
  }

  /**
   * @param view
   */
  abstract void onClickTopView(View view);

  public float leftBorder() {
    return mParentWidth / 4.f;
  }

  /**
   * When the object rotates it's width becomes bigger.
   * The maximum width is at 45 degrees.
   * <p/>
   * The below method calculates the width offset of the rotation.
   */
  private float getRotationWidthOffset() {
    return mFrameWidth / MAX_COS - mFrameWidth;
  }

  private float getExitRotation(boolean isLeft, float factor) {
    float rotation = mBaseRotateDegree * 2.f * factor;
    if (mTouchPosition == TOUCH_BELOW) {
      rotation = -rotation;
    }
    if (isLeft) {
      rotation = -rotation;
    }
    return rotation;
  }

  /**
   * @param view
   */
//  abstract void onExitToLeft(View view);

  /**
   * @param view
   */
//  abstract void onExitToRight(View view);

  /**
   *
   */
  abstract void onExited(View view, @StackView.Direction.Type int direction);

  public float rightBorder() {
    return 3 * mParentWidth / 4.f;
  }

  /**
   * Starts a default left exit animation.
   */
  public ValueAnimator swipeToLeft() {
    if (!mIsAnimationRunning) {
      return swipeTopView(true, mFrameY, SELECT_ITEM_DURATION);
    }
    return null;
  }

  /**
   * Starts a default right exit animation.
   */
  public ValueAnimator swipeToRight() {
    if (!mIsAnimationRunning) {
      return swipeTopView(false, mFrameY, SELECT_ITEM_DURATION);
    }
    return null;
  }

  @SuppressWarnings("unused")
  @Deprecated
  private float getExitRotation(boolean isLeft) {
    float rotation = mBaseRotateDegree * 2.f * (mParentWidth - mFrameX) / mParentWidth;
    if (mTouchPosition == TOUCH_BELOW) {
      rotation = -rotation;
    }
    if (isLeft) {
      rotation = -rotation;
    }
    return rotation;
  }

  public void setRotationDegrees(float degrees) {
    this.mBaseRotateDegree = degrees;
  }

  public boolean isTouching() {
    return this.mActivePointerId != INVALID_POINTER_ID;
  }

  public PointF getLastPoint() {
    return new PointF(this.mPosX, this.mPosY);
  }

  /*************************************************************************
   * Compilation:  javac LinearRegression.java
   * Execution:    java  LinearRegression
   * <p/>
   * Compute least squares solution to y = beta * x + alpha.
   * Simple linear regression.
   *************************************************************************/

  /**
   * The <tt>LinearRegression</tt> class performs a simple linear regression
   * on an set of <em>N</em> data points (<em>y<sub>i</sub></em>, <em>x<sub>i</sub></em>).
   * That is, it fits a straight line <em>y</em> = &alpha; + &beta; <em>x</em>,
   * (where <em>y</em> is the response variable, <em>x</em> is the predictor variable,
   * &alpha; is the <em>y-intercept</em>, and &beta; is the <em>slope</em>)
   * that minimizes the sum of squared residuals of the linear regression model.
   * It also computes associated statistics, including the coefficient of
   * determination <em>R</em><sup>2</sup> and the standard deviation of the
   * estimates for the slope and <em>y</em>-intercept.
   *
   * @author Robert Sedgewick
   * @author Kevin Wayne
   */
  class LinearRegression {
    private final int N;
    private final double alpha;
    private final double beta;
    private final double R2;
    private final double svar;
    private final double svar0;
    private final double svar1;

    /**
     * Performs a linear regression on the data points <tt>(y[i], x[i])</tt>.
     *
     * @param x the values of the predictor variable
     * @param y the corresponding values of the response variable
     * @throws IllegalArgumentException if the lengths of the two arrays are not equal
     */
    public LinearRegression(float[] x, float[] y) {
      if (x.length != y.length) {
        throw new IllegalArgumentException("array lengths are not equal");
      }
      N = x.length;

      // first pass
      double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
      for (int i = 0; i < N; i++) sumx += x[i];
      for (int i = 0; i < N; i++) sumx2 += x[i] * x[i];
      for (int i = 0; i < N; i++) sumy += y[i];
      double xbar = sumx / N;
      double ybar = sumy / N;

      // second pass: compute summary statistics
      double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
      for (int i = 0; i < N; i++) {
        xxbar += (x[i] - xbar) * (x[i] - xbar);
        yybar += (y[i] - ybar) * (y[i] - ybar);
        xybar += (x[i] - xbar) * (y[i] - ybar);
      }
      beta = xybar / xxbar;
      alpha = ybar - beta * xbar;

      // more statistical analysis
      double rss = 0.0;      // residual sum of squares
      double ssr = 0.0;      // regression sum of squares
      for (int i = 0; i < N; i++) {
        double fit = beta * x[i] + alpha;
        rss += (fit - y[i]) * (fit - y[i]);
        ssr += (fit - ybar) * (fit - ybar);
      }

      int degreesOfFreedom = N - 2;
      R2 = ssr / yybar;
      svar = rss / degreesOfFreedom;
      svar1 = svar / xxbar;
      svar0 = svar / N + xbar * xbar * svar1;
    }

    /**
     * Returns the standard error of the estimate for the intercept.
     *
     * @return the standard error of the estimate for the intercept
     */
    public double interceptStdErr() {
      return Math.sqrt(svar0);
    }

    /**
     * Returns the standard error of the estimate for the slope.
     *
     * @return the standard error of the estimate for the slope
     */
    public double slopeStdErr() {
      return Math.sqrt(svar1);
    }

    /**
     * Returns the expected response <tt>y</tt> given the value of the predictor
     * variable <tt>x</tt>.
     *
     * @param x the value of the predictor variable
     * @return the expected response <tt>y</tt> given the value of the predictor
     * variable <tt>x</tt>
     */
    public double predict(double x) {
      return beta * x + alpha;
    }

    /**
     * Returns a string representation of the simple linear regression model.
     *
     * @return a string representation of the simple linear regression model,
     * including the best-fit line and the coefficient of determination <em>R</em><sup>2</sup>
     */
    public String toString() {
      String s = "";
      s += String.format("%.2f N + %.2f", slope(), intercept());
      return s + "  (R^2 = " + String.format("%.3f", R2()) + ")";
    }

    /**
     * Returns the slope &beta; of the best of the best-fit line <em>y</em> = &alpha; + &beta;
     * <em>x</em>.
     *
     * @return the slope &beta; of the best-fit line <em>y</em> = &alpha; + &beta; <em>x</em>
     */
    public double slope() {
      return beta;
    }

    /**
     * Returns the <em>y</em>-intercept &alpha; of the best of the best-fit line <em>y</em> =
     * &alpha; + &beta; <em>x</em>.
     *
     * @return the <em>y</em>-intercept &alpha; of the best-fit line <em>y = &alpha; + &beta; x</em>
     */
    public double intercept() {
      return alpha;
    }

    /**
     * Returns the coefficient of determination <em>R</em><sup>2</sup>.
     *
     * @return the coefficient of determination <em>R</em><sup>2</sup>, which is a real number
     * between 0 and 1
     */
    public double R2() {
      return R2;
    }
  }
}
