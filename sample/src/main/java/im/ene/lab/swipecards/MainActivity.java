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

package im.ene.lab.swipecards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import im.ene.lab.decky.ArrayStackAdapter;
import im.ene.lab.decky.StackView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

  private static final int MAX = 10;

  @Bind(R.id.stackview) StackView mStackView;
  private ArrayList<String> mStrings;
  private ArrayStackAdapter<String> mAdapter;

  @OnClick(R.id.swipe_left) public void swipeLeft() {
    if (mStackView != null) {
      mStackView.swipeToLeft();
    }
  }

  @OnClick(R.id.swipe_right) public void swipeRight() {
    if (mStackView != null) {
      mStackView.swipeToRight();
    }
  }

  @OnClick(R.id.undo_swipe) public void undoSwipe() {
    if (mStackView != null) {
      mStackView.undo();
    }
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    mStrings = new ArrayList<>();
    for (int i = 0; i < MAX; i++) {
      mStrings.add("Pos: " + i);
    }

    mAdapter = new ArrayStackAdapter<>(this, R.layout.card_item, R.id.text_view, mStrings);
    mStackView.setAdapter(mAdapter);

    mStackView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

      }
    });

    mStackView.setOnSwipeListener(new StackView.OnCardSwipeListener() {
      @Override public void onExiting(View view, float offset) {
        Log.d("exiting", offset + "");
      }

      @Override public void onExited(View view) {

      }

      @Override public void onExitToLeft(View view) {

      }

      @Override public void onExitToRight(View view) {

      }

      @Override public void onAdapterAboutToEmpty(int count) {

      }
    });
  }

  @Override protected void onDestroy() {
    ButterKnife.unbind(this);
    super.onDestroy();
  }
}
