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

import android.content.Context;
import android.util.Log;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by eneim on 9/10/15.
 *
 * @param <T>
 */
public abstract class Adapter<T> extends BaseAdapter {

  static final Integer LAST_SWIPE_UNDEFINED = 0;
  static final Integer LAST_SWIPE_LEFT = 1;
  static final Integer LAST_SWIPE_RIGHT = 2;

  /**
   * Stack to stores objects which are swiped to left
   */
  private final Stack<T> mLeftStack = new Stack<>();

  /**
   * Stack to stores objects which are swiped to right
   */
  private final Stack<T> mRightStack = new Stack<>();

  /**
   * Stack to stores swipe direction, defined by constants above
   */
  private final Stack<Integer> mActions = new Stack<>();

  /**
   * Main object storage
   */
  protected ArrayList<T> mMainStack = new ArrayList<>();

  public Adapter(Context context, ArrayList<T> items) {
    this(context);
    mMainStack = items;
  }

  /**
   * @param context
   */
  public Adapter(Context context) {
    super();
    mActions.push(LAST_SWIPE_UNDEFINED);
  }

  protected final synchronized void swipeToLeft() {
    if (!mMainStack.isEmpty()) {
      mLeftStack.push(mMainStack.remove(0));
      notifyDataSetChanged();
      mActions.push(LAST_SWIPE_LEFT);
      onSwipeToLeft();
      verify();
    }
  }

  /**
   * Override this for custom behavior on swiping the top card to left
   */
  public void onSwipeToLeft() {

  }

  /**
   * @return
   */
  @Override public final int getCount() {
    return mMainStack.size();
  }

  /**
   * @param position
   * @return
   */
  @Override public final T getItem(int position) {
    if (position < 0 || position > mMainStack.size()) {
      return null;
    }
    return mMainStack.get(position);
  }

  protected final synchronized void swipeToRight() {
    if (!mMainStack.isEmpty()) {
      mRightStack.push(mMainStack.remove(0));
      notifyDataSetChanged();
      mActions.push(LAST_SWIPE_RIGHT);
      onSwipeToRight();
      verify();
    }
  }

  /**
   * Override this for custom behavior on swiping the top card to right
   */
  public void onSwipeToRight() {

  }

  protected synchronized void undoLastSwipe() {
    int lastAction = mActions.peek(); // fetch the last action;
    if (lastAction != LAST_SWIPE_UNDEFINED) {
      if (lastAction == LAST_SWIPE_LEFT) {
        mActions.pop();
        mMainStack.add(0, mLeftStack.pop());
        notifyDataSetChanged();
      } else if (lastAction == LAST_SWIPE_RIGHT) {
        mActions.pop();
        mMainStack.add(0, mRightStack.pop());
        notifyDataSetChanged();
      }

      onUndo();
    } else {
      // You are at the bottom of the stack;
      // i.e main stack is empty
    }

    verify();
  }

  // optional
  public void onUndo() {
  }

  /**
   * method for debugging
   */
  private void verify() {
    // the following value should be 1 always;
    int diff = mActions.size() + mMainStack.size() - getTotalCount();
    if (diff == 1) {
      Log.e("StackAdapter", "stacks are stable");
    } else {
      Log.e("StackAdapter", "stacks are not stable");
    }
  }

  public final int getTotalCount() {
    return mMainStack.size() + mLeftStack.size() + mRightStack.size();
  }

  /**
   * Programmatically swipe to a position. First swipe will be a left swipe and the right comes
   * next and so on.
   * <p/>
   * Note that 2 stacks store the number of swiped cards, we base on that number to make the
   * programmatically swiping
   *
   * @param position
   */
  public void swipeToPosition(int position) {
    // swipeToPosition(0) doable IFF mLeftStack.size() + mRightStack.size() = 0;
    // swipeToPosition(index) doable IFF mLeftStack.size() + mRightStack.size() <= n;
    if (position >= getCount()) {
      throw new IndexOutOfBoundsException("Required position is out of Adapter's index bound");
    }

    int swipedCardCount = mLeftStack.size() + mRightStack.size();
    if (swipedCardCount > position) {
      throw new IllegalStateException("The selected card has already been swiped");
    }

    if (swipedCardCount == position) { // You are at the required card already
      return;
    }

    int offset;
    while ((offset = position - mLeftStack.size() - mRightStack.size()) > 0) {
      if (offset % 2 == 0) {
        swipeToLeft();
      } else {
        swipeToRight();
      }
    }
  }

}
