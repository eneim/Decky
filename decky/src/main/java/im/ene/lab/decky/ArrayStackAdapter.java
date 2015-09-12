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
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by eneim on 9/10/15.
 */
public class ArrayStackAdapter<T> extends Adapter<T> {

  private ArrayAdapter<T> mAdapter;

  public ArrayStackAdapter(Context context,
                           @LayoutRes int resource,
                           @IdRes int textViewResourceId,
                           ArrayList<T> items) {
    super(context, items);
    mAdapter = new ArrayAdapter<>(context, resource, textViewResourceId, mMainStack);
  }

  @Override public long getItemId(int position) {
    return mAdapter.getItemViewType(position);
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    return mAdapter.getView(position, convertView, parent);
  }
}
