/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.lab.attiq.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by eneim on 1/21/16.
 */
public interface OnItemClickListener {

  /**
   * Interact with RecyclerView's item on click event
   *
   * @param adapter         who holds data
   * @param viewHolder      the #ViewHolder who receives click event
   * @param view            the view under the click event
   * @param adapterPosition position of clicked item in adapter
   * @param itemId          retrieve from
   *                        {@link RecyclerView.Adapter#getItemId(int)}
   */
  void onItemClick(BaseAdapter adapter, BaseAdapter.ViewHolder viewHolder, View view,
                   int adapterPosition, long itemId);
}
