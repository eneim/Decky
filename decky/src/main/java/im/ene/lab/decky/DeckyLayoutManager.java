package im.ene.lab.decky;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class DeckyLayoutManager extends RecyclerView.LayoutManager {

  LinearLayoutManager ref;
  StaggeredGridLayoutManager ref2;

  /**
   * This layout's children should have same sizes
   */
  /* Consistent size applied to all child views */
  private int mDecoratedChildWidth;
  private int mDecoratedChildHeight;
  private int mExpectedChildCount = 4;

  /**
   * First layout call from framework
   *
   * @param recycler
   * @param state
   */
  @Override public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
    if (state.getItemCount() == 0) {
      removeAndRecycleAllViews(recycler);
      return;
    }

    if (getChildCount() == 0 && state.isPreLayout()) {
      // Nothing to do during pre-layout when empty
      return;
    }

    if (getChildCount() == 0) {
      // Scrap measure first child
      View scrap = recycler.getViewForPosition(0);
      addView(scrap);
      measureChildWithMargins(scrap, 0, 0);
      /*
       * We make some assumptions in this code based on every child
       * view being the same size (i.e. a uniform grid). This allows
       * us to compute the following values up front because they
       * won't change.
       */
      mDecoratedChildWidth = getDecoratedMeasuredWidth(scrap);
      mDecoratedChildHeight = getDecoratedMeasuredHeight(scrap);

      detachAndScrapView(scrap, recycler);
    }

    int leftOffset = (getWidth() - mDecoratedChildWidth -
        getPaddingLeft() - getPaddingRight()) / 2;
    int topOffset = (getHeight() - mDecoratedChildHeight -
        getPaddingTop() - getPaddingBottom()) / 2;

    // Layout this position
    Log.e("LAYOUT_MANAGER", getChildCount() + " | " + getItemCount());
    ArrayList<View> viewCache = new ArrayList<>(getChildCount());
    if (getChildCount() > 0) {
      // Cache all views by their existing position, before updating counts
      for (int i = 0; i < getChildCount(); i++) {
        int adapterPosition = adapterPositionOfViewIndex(i);
        final View child = getChildAt(i);
        viewCache.add(adapterPosition, child);
      }

      // Temporarily detach all views.
      // Views we still need will be added back at the proper index.
      while (viewCache.size() > 0) {
        detachView(viewCache.remove(0));
      }
    }

    mExpectedChildCount = getItemCount() > 4 ? 4 : getItemCount() - 1;

    View childView;
    for (int i = mExpectedChildCount; i >= 0; i--) {
      childView = recycler.getViewForPosition(adapterPositionOfViewIndex(i));
      ViewCompat.setRotation(childView, 0.f);
      addView(childView);
      measureChildWithMargins(childView, 0, 0);
      layoutDecorated(childView, leftOffset, topOffset,
          leftOffset + mDecoratedChildWidth,
          topOffset + mDecoratedChildHeight);
    }

  }

  private int adapterPositionOfViewIndex(int i) {
    return i;
  }

  @Override public RecyclerView.LayoutParams generateDefaultLayoutParams() {
    return new RecyclerView.LayoutParams(
        RecyclerView.LayoutParams.WRAP_CONTENT,
        RecyclerView.LayoutParams.WRAP_CONTENT);
  }

  // TODO override this
  @Override public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
    return super.generateLayoutParams(lp);
  }

  // TODO override this
  @Override public RecyclerView.LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
    return super.generateLayoutParams(c, attrs);
  }

  @Override public boolean canScrollHorizontally() {
    return false;
  }

  @Override public boolean canScrollVertically() {
    return false;
  }

  @Override
  public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
    removeAllViews();
  }

  @Override
  public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
    super.onItemsRemoved(recyclerView, positionStart, itemCount);
  }

  // TODO finish this, with addition layout param attributes
  public static class LayoutParams extends RecyclerView.LayoutParams {

    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);
    }

    public LayoutParams(int width, int height) {
      super(width, height);
    }

    public LayoutParams(ViewGroup.MarginLayoutParams source) {
      super(source);
    }

    public LayoutParams(ViewGroup.LayoutParams source) {
      super(source);
    }

    public LayoutParams(RecyclerView.LayoutParams source) {
      super(source);
    }
  }
}
