/*
 * Copyright 2015. Alashov Berkeli
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

package tm.alashow.musictanzania.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import tm.alashow.musictanzania.R;
import tm.alashow.musictanzania.interfaces.OnItemClickListener;
import tm.alashow.musictanzania.model.Audio;
import tm.alashow.musictanzania.ui.activity.MainActivity;

/**
 * Created by alashov on 08/12/14.
 */
public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.ViewHolder> {

    private ArrayList<Audio> audioList;

    private ArrayList<Audio> filteredList;
    private OnItemClickListener onItemClickListener;
    private Context context;

    /**
     * @param audioList list of array
     */
    public AudioListAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        if (context != null) {
            this.context = context;
        }
    }

    public void setList(ArrayList<Audio> audioList){

        this.audioList = audioList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }



    @Override
    public AudioListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_audio, parent, false);
        final ViewHolder mViewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, mViewHolder.getLayoutPosition());
            }
        });
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Audio audio = audioList.get(position);

        holder.name.setText(String.format("%s - %s", audio.getArtist(), audio.getTitle()));
        holder.duration.setText(audio.getDuration());

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             ((MainActivity) context).bottomsheet();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.name) TextView name;
        @Bind(R.id.play) View playButton;
        @Bind(R.id.duration) TextView duration;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * @param seconds seconds to format
     * @return converted to mm:ss duration
     */
    private String secondsToString(int seconds) {
        return String.format(Locale.US, "%02d:%02d", seconds / 60, seconds % 60);
    }
    public void setFilter(ArrayList<Audio> countryModels) {
        this.audioList = new ArrayList<>();
        this.audioList.addAll(countryModels);
        notifyDataSetChanged();
    }

}
