package ch.bbcag.cineboi.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ch.bbcag.cineboi.R;
import ch.bbcag.cineboi.model.Film;

public class WatchListRecyclerAdapter extends RecyclerView.Adapter<WatchListRecyclerAdapter.ViewHolder> {

    private List<Film> localDataSet;
    private Context context;

    public WatchListRecyclerAdapter(List<Film> dataSet, Context context) {
        localDataSet = dataSet;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Film film = localDataSet.get(position);
        viewHolder.getFilmTitle().setText(film.getName());
        viewHolder.getFilmDescription().setText(film.getOverview());
        Glide.with(context).load(film.getPoster_Path()).centerCrop().into((ImageView) viewHolder.getFilmPoster());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView filmTitle;
        private final TextView filmDescription;
        private final ImageView filmPoster;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            filmTitle = (TextView) view.findViewById(R.id.watchlist_title);
            filmDescription = (TextView) view.findViewById(R.id.watchlist_overview);
            filmPoster = (ImageView) view.findViewById(R.id.watchlist_poster);
        }

        public TextView getFilmTitle() {
            return filmTitle;
        }

        public TextView getFilmDescription() {
            return filmDescription;
        }

        public ImageView getFilmPoster() {
            return filmPoster;
        }
    }
}
