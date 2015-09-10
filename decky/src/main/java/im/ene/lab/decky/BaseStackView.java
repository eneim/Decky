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

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by eneim on 9/10/15.
 */
abstract class BaseStackView extends AdapterView<BaseStackAdapter> {
  protected BaseStackAdapter mAdapter;
  protected int mHeightMeasureSpec;
  protected int mWidthMeasureSpec;

  private DataSetObserver mDataSetObserver;

  public BaseStackView(Context context) {
    super(context);
  }

  public BaseStackView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public BaseStackView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public BaseStackView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override public void setAdapter(BaseStackAdapter adapter) {
    if (adapter == null) {
      throw new IllegalArgumentException("Adapter must not be null");
    }

    if (mAdapter != null && mDataSetObserver != null) {
      mAdapter.unregisterDataSetObserver(mDataSetObserver);
      mDataSetObserver = null;
    }

    mAdapter = adapter;

    if (mDataSetObserver == null) {
      mDataSetObserver = new AdapterDataSetObserver();
      mAdapter.registerDataSetObserver(mDataSetObserver);
    }
  }

  @Override public View getSelectedView() {
    return getTopView();
  }

  @Override public void setSelection(int position) {
    swipeToCard(position);
  }

  public abstract void swipeToCard(int position);

  public abstract View getTopView();

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    this.mWidthMeasureSpec = widthMeasureSpec;
    this.mHeightMeasureSpec = heightMeasureSpec;
  }

  private class AdapterDataSetObserver extends DataSetObserver {
    @Override
    public void onChanged() {
      requestLayout();
    }

    @Override
    public void onInvalidated() {
      requestLayout();
    }
  }
}
