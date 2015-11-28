package im.ene.lab.decky;

import android.support.v7.widget.RecyclerView;

/**
 * Created by eneim on 11/28/15.
 */
public abstract class DeckyAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> {

  /**
   * This Adapter is able to remove specific item
   *
   * @param position of item to be removed
   */
  public abstract void remove(int position);
}
