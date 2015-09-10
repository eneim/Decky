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
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by eneim on 9/10/15.
 */
public class StackView extends BaseStackView {

  /**
   * Number of cards on hand ...
   */
  private int mOnHandStackCount = 3;

  /**
   * Number of cards left (on the deck and hand)
   */
  private int mAdapterThreshold = 10;

  /**
   * Rotation animation degree
   */
  private float mRotateDegree = 15.f;

  /**
   * A index tracker for cards on hand
   */
  private int mStackBottomIndex = 0;

  private boolean mIsInLayout = false;

  /**
   * Listener for fling action
   */
  private OnCardSwipeListener mOnCardSwipeListener;

  /**
   * Card on top of the stack
   */
  private View mTopView = null;
  private OnItemClickListener mOnItemClickListener;
  private OnTopViewTouchListener mOnTopViewTouchListener;
  private PointF mLastTouchPoint;

  public StackView(Context context) {
    this(context, null);
  }

  public StackView(Context context, AttributeSet attrs) {
    this(context, attrs, R.attr.swipeStyle);
  }

  /**
   * @param context
   * @param attrs
   * @param defStyle
   */
  public StackView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StackView,
        defStyle, 0);
    mOnHandStackCount = typedArray.getInt(R.styleable.StackView_max_visible_children,
        mOnHandStackCount);
    mAdapterThreshold = typedArray.getInt(R.styleable.StackView_min_adapter_stack,
        mAdapterThreshold);
    mRotateDegree = typedArray.getFloat(R.styleable.StackView_rotation_degrees, 15.f);
    String layoutStyle = typedArray.getString(R.styleable.StackView_layout_style);
    typedArray.recycle();

    // TODO implement Custom Style
  }

  public void setOnHandStackCount(int onHandStackCount) {
    this.mOnHandStackCount = onHandStackCount;
    requestLayout();
  }

  @Override
  public void requestLayout() {
    if (!mIsInLayout) {
      super.requestLayout();
    }
  }

  public void setAdapterThreshold(int adapterThreshold) {
    this.mAdapterThreshold = adapterThreshold;
    requestLayout();
  }

  public void setOnSwipeListener(OnCardSwipeListener onCardSwipeListener) {
    this.mOnCardSwipeListener = onCardSwipeListener;
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.mOnItemClickListener = onItemClickListener;
  }

  @Override public BaseStackAdapter getAdapter() {
    return mAdapter;
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    // if we don't have an adapter, we don't need to do anything
    if (mAdapter == null) {
      return;
    }

    mIsInLayout = true;
    final int adapterCount = mAdapter.getCount();

    if (adapterCount == 0) {
      removeAllViewsInLayout();
    } else {
      View topCard = getChildAt(mStackBottomIndex);
      if (mTopView != null && topCard != null && topCard == mTopView) {
        if (mOnTopViewTouchListener.isTouching()) {
          PointF lastPoint = mOnTopViewTouchListener.getLastPoint();
          if (mLastTouchPoint == null || !mLastTouchPoint.equals(lastPoint)) {
            mLastTouchPoint = lastPoint;
            removeViewsInLayout(0, mStackBottomIndex);
            layoutChildren(1, adapterCount);
          }
        }
      } else {
        // Reset the UI and set top view listener
        removeAllViewsInLayout();
        layoutChildren(0, adapterCount);
        setupTopView();
      }
    }

    mIsInLayout = false;

    if (adapterCount <= mAdapterThreshold) {
      if (mOnCardSwipeListener != null) {
        mOnCardSwipeListener.onAdapterAboutToEmpty(adapterCount);
      }
    }
  }

  private void layoutChildren(int startingIndex, int adapterCount) {
    int maxViewIndex = Math.min(adapterCount, mOnHandStackCount) - 1;
    while (startingIndex <= maxViewIndex) {
      View newUnderChild = mAdapter.getView(startingIndex, null, this);
      if (newUnderChild.getVisibility() != GONE) {
        makeAndAddView(newUnderChild);
        mStackBottomIndex = startingIndex;
      }
      startingIndex++;
    }
  }

  /**
   * Set the top view and add the fling listener
   */
  private void setupTopView() {
    if (getChildCount() > 0) {
      mTopView = getChildAt(mStackBottomIndex);
      if (mTopView != null) {
        mOnTopViewTouchListener = new OnTopViewTouchListener(this, mTopView, mRotateDegree) {
          @Override void onFlingTopView(float offset) {
            if (mOnCardSwipeListener != null) {
              mOnCardSwipeListener.onExiting(mTopView, offset);
            }
          }

          @Override void onClickTopView(View view) {
            if (mOnItemClickListener != null) {
              mOnItemClickListener.onItemClick(StackView.this, view, 0,
                  getItemIdAtPosition(0));
            }
          }

          @Override void onExitToLeft(View view) {
            if (mOnCardSwipeListener != null) {
              mOnCardSwipeListener.onExitToLeft(view);
            }

            mAdapter.swipeToLeft();
          }

          @Override void onExitToRight(View view) {
            if (mOnCardSwipeListener != null) {
              mOnCardSwipeListener.onExitToRight(view);
            }

            mAdapter.swipeToRight();
          }

          @Override void onExited(View view) {
            if (mOnCardSwipeListener != null) {
              mOnCardSwipeListener.onExited(mTopView);
            }
            mTopView = null;
          }
        };

        mTopView.setOnTouchListener(mOnTopViewTouchListener);
      }
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  private void makeAndAddView(View child) {
    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
    addViewInLayout(child, 0, lp, true);

    final boolean needToMeasure = child.isLayoutRequested();
    if (needToMeasure) {
      int childWidthSpec = getChildMeasureSpec(mWidthMeasureSpec,
          getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
          lp.width);
      int childHeightSpec = getChildMeasureSpec(mHeightMeasureSpec,
          getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin,
          lp.height);
      child.measure(childWidthSpec, childHeightSpec);
    } else {
      cleanupLayoutState(child);
    }

    int childMeasuredWidth = child.getMeasuredWidth();
    int childMeasuredHeight = child.getMeasuredHeight();

    int gravity = lp.gravity;
    if (gravity == -1) {
      gravity = Gravity.TOP | Gravity.START;
    }

    int layoutDirection = getLayoutDirection();
    final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
    final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

    int childLeft;
    int childTop;
    switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
      case Gravity.CENTER_HORIZONTAL:
        childLeft = (getWidth() + getPaddingLeft() - getPaddingRight() - childMeasuredWidth) / 2
            + lp.leftMargin - lp.rightMargin;
        break;
      case Gravity.END:
        childLeft = getWidth() + getPaddingRight() - childMeasuredWidth - lp.rightMargin;
        break;
      case Gravity.START:
      default:
        childLeft = getPaddingLeft() + lp.leftMargin;
        break;
    }
    switch (verticalGravity) {
      case Gravity.CENTER_VERTICAL:
        childTop = (getHeight() + getPaddingTop() - getPaddingBottom() - childMeasuredHeight) / 2
            + lp.topMargin - lp.bottomMargin;
        break;
      case Gravity.BOTTOM:
        childTop = getHeight() - getPaddingBottom() - childMeasuredHeight - lp.bottomMargin;
        break;
      case Gravity.TOP:
      default:
        childTop = getPaddingTop() + lp.topMargin;
        break;
    }

    child.layout(childLeft, childTop,
        childLeft + childMeasuredWidth, childTop + childMeasuredHeight);
  }

  @Override public void swipeToCard(final int position) {
    // swipeToPosition(0) doable IFF mLeftStack.size() + mRightStack.size() = 0;
    // swipeToPosition(index) doable IFF mLeftStack.size() + mRightStack.size() <= n;
    if (position >= mAdapter.getCount()) {
      throw new IndexOutOfBoundsException("Required position is out of Adapter's index bound");
    }

    int swipedCardCount = mAdapter.getTotalCount() - mAdapter.getCount();
    if (swipedCardCount > position) { // The selected card has already been swiped
      return;
    }

    if (swipedCardCount == position) { // You are at the required card already
      return;
    }

    int offset;
    if ((offset = position - mAdapter.getTotalCount() + mAdapter.getCount()) > 0) {
      final boolean isEven = offset % 2 == 0;
      postDelayed(new Runnable() {
        @Override public void run() {
          ValueAnimator listenerAdapter;
          if (isEven) {
            listenerAdapter = swipeToLeftInternal();
          } else {
            listenerAdapter = swipeToRightInternal();
          }

          if (listenerAdapter != null) {
            listenerAdapter.addListener(new AnimatorListenerAdapter() {
              @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                swipeToCard(position);
              }
            });
            listenerAdapter.start();
          }
        }
      }, 100);
    }
  }

  @Override public View getTopView() {
    return mTopView;
  }

  public void swipeToLeft() {
    if (mOnTopViewTouchListener != null) {
      try {
        mOnTopViewTouchListener.swipeToLeft().start();
      } catch (NullPointerException er) {
        er.printStackTrace();
      }
    }
  }

  private ValueAnimator swipeToLeftInternal() {
    if (mOnTopViewTouchListener != null) {
      return mOnTopViewTouchListener.swipeToLeft();
    }
    return null;
  }

  private ValueAnimator swipeToRightInternal() {
    if (mOnTopViewTouchListener != null) {
      return mOnTopViewTouchListener.swipeToRight();
    }
    return null;
  }

  public void swipeToRight() {
    if (mOnTopViewTouchListener != null) {
      try {
        mOnTopViewTouchListener.swipeToRight().start();
      } catch (NullPointerException er) {
        er.printStackTrace();
      }
    }
  }

  public void undo() {
    mStackBottomIndex--;
    mAdapter.undoLastSwipe();
  }

  @Override
  public LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new FrameLayout.LayoutParams(getContext(), attrs);
  }

  public interface OnCardSwipeListener {

    void onExiting(View view, float offset);

    void onExited(View view);

    void onExitToLeft(View view);

    void onExitToRight(View view);

    void onAdapterAboutToEmpty(int count);
  }

}
