/*
 * Copyright 2014. Alashov Berkeli
 *
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package tm.alashow.musictanzania.interfaces;

import android.view.View;

/**
 * Created by alashov on 23/09/2016.
 */

public interface OnItemClickListener {
    /**
     * On item click.
     *
     * @param view     the view
     * @param position the position
     */
    void onItemClick(View view, int position);
}
