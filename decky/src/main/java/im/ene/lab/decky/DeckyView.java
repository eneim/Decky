package im.ene.lab.decky;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;

/**
 * Decky View
 */
public class DeckyView extends RecyclerView {

  public DeckyView(Context context) {
    this(context, null);
  }

  public DeckyView(Context context, AttributeSet attrs) {
    super(context, attrs);
    LayoutManager layoutManager = new DeckyLayoutManager();
    setLayoutManager(layoutManager);
  }

  @Override public void setLayoutManager(LayoutManager layout) {
    if (!(layout instanceof DeckyLayoutManager)) {
      throw new IllegalArgumentException("This view only accepts DeckyLayoutManager");
    }

    super.setLayoutManager(layout);
  }

  @Override public void setAdapter(Adapter adapter) {
    if (adapter instanceof DeckyAdapter) {
      super.setAdapter(adapter);
      initTouchBehavior((DeckyAdapter) adapter);
    } else {
      throw new IllegalArgumentException("This view only accepts DeckyAdapter");
    }
  }

  private void initTouchBehavior(final DeckyAdapter adapter) {
    final ItemTouchHelper.Callback touchCallback =
        new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP
                | ItemTouchHelper.DOWN
                | ItemTouchHelper.LEFT
                | ItemTouchHelper.RIGHT,
            ItemTouchHelper.UP
                | ItemTouchHelper.DOWN
                | ItemTouchHelper.LEFT
                | ItemTouchHelper.RIGHT) {
          @Override
          public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                RecyclerView.ViewHolder target) {
            return false;
          }

          @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int adapterPosition = viewHolder.getAdapterPosition();
            adapter.remove(adapterPosition);
            adapter.notifyItemRemoved(adapterPosition);
          }

          @Override
          public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                  int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            int degree = (int) (dX * 45 / 540.f);
            ViewCompat.setRotation(viewHolder.itemView, degree);

            // calculate swiping offset

          }
        };
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);
    itemTouchHelper.attachToRecyclerView(this);
  }

  private OnSwipeOffsetListener mOffsetListener;

  public interface OnSwipeOffsetListener {

    void onSwipeOffset(float offset);
  }
}
