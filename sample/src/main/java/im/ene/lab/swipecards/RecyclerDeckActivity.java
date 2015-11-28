package im.ene.lab.swipecards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchUIUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import im.ene.lab.decky.DeckyAdapter;
import im.ene.lab.decky.DeckyView;

import java.util.ArrayList;
import java.util.List;


public class RecyclerDeckActivity extends AppCompatActivity {

  private static final String TAG = RecyclerDeckActivity.class.getSimpleName();

  @Bind(R.id.recycler_view) DeckyView mRecyclerView;

  private Adapter mAdapter;
  private ItemTouchHelper mItemTouchHelper;
  private ItemTouchUIUtil mItemTouchUtil;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_decky);
    ButterKnife.bind(this);

    mAdapter = new Adapter();
    mRecyclerView.setAdapter(mAdapter);
  }

  static class Adapter extends DeckyAdapter<ViewHolder> {

    public List<String> mItems;

    public Adapter() {
      mItems = new ArrayList<>();
      for (int i = 0; i < 20; i++) {
        mItems.add(i + "");
      }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.card_item, parent, false);
      return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
      holder.bind(mItems.get(position));
    }

    @Override public int getItemCount() {
      return mItems.size();
    }

    @Override public void remove(int position) {
      mItems.remove(position);
    }
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.text_view) TextView mTextView;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void bind(String position) {
      mTextView.setText("" + position);
    }
  }
}
