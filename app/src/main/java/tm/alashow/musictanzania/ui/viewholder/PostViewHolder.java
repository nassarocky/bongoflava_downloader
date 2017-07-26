package tm.alashow.musictanzania.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import tm.alashow.musictanzania.R;
import tm.alashow.musictanzania.model.Audio;


public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public TextView duration;

    public PostViewHolder(View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.name);
        duration = (TextView) itemView.findViewById(R.id.duration);
;
    }

    public void bindToAudio(Audio Audio, View.OnClickListener starClickListener) {
        name.setText(Audio.title);
duration.setText(Audio.duration);
      name.setOnClickListener(starClickListener);
    }
}
