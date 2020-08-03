package ugr.pdm.granadatour.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import ugr.pdm.granadatour.R;
import ugr.pdm.granadatour.models.Item;
import ugr.pdm.granadatour.models.Route;

/***
 * The adapter class for the RecyclerView, contains the routes data.
 */
public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder> {

    // Member variables.
    private ArrayList<Route> mRoutesData;
    private Context mContext;

    /**
     * Constructor that passes in the Routes data and the context.
     *
     * @param RoutesData ArrayList containing the routes data.
     * @param context    Context of the application.
     */
    public RouteAdapter(Context context, ArrayList<Route> RoutesData) {
        this.mRoutesData = RoutesData;
        this.mContext = context;
    }


    /**
     * Required method for creating the viewholder objects.
     *
     * @param parent   The ViewGroup into which the new View will be added
     *                 after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return The newly created ViewHolder.
     */
    @NonNull
    @Override
    public RouteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.list_item, parent, false));
    }

    /**
     * Required method that binds the data to the viewholder.
     *
     * @param holder   The viewholder into which the data should be put.
     * @param position The adapter position.
     */
    @Override
    public void onBindViewHolder(RouteAdapter.ViewHolder holder, int position) {
        // Get current route.
        Route currentRoute = mRoutesData.get(position);

        // Populate the textviews with data.
        holder.bindTo(currentRoute);
    }

    /**
     * Required method for determining the size of the data set.
     *
     * @return Size of the data set.
     */
    @Override
    public int getItemCount() {
        return mRoutesData.size();
    }


    /**
     * ViewHolder class that represents each row of data in the RecyclerView.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Member Variables for the TextViews
        private TextView mTitleText;
        private TextView mSubtitleText;
        private ImageView mRoutesImage;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param routeView The rootview of the list_item.xml layout file.
         */
        ViewHolder(View routeView) {
            super(routeView);

            // Initialize the views.
            mTitleText = routeView.findViewById(R.id.itemTitle);
            mSubtitleText = routeView.findViewById(R.id.itemSubtitle);
            mRoutesImage = routeView.findViewById(R.id.itemImage);

            routeView.setOnClickListener(this);
        }

        void bindTo(Route currentRoute) {
            // Populate the views with data.
            mTitleText.setText(currentRoute.getTitle());
            mSubtitleText.setText(currentRoute.getSubtitle());

            // Create a reference to a file from a Google Cloud Storage URI
            if (currentRoute.isValidURL()) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference gsReference = storage.getReferenceFromUrl(currentRoute.getImage());
                Glide.with(mContext)
                        .load(gsReference)
                        .centerCrop()
                        .into(mRoutesImage);
            }
        }

        @Override
        public void onClick(View view) {
            Route clickedRoute = mRoutesData.get(getAdapterPosition());

            Bundle argsBundle = new Bundle();
            argsBundle.putSerializable(Item.SERIALZABLE_KEY, clickedRoute);
            Navigation.findNavController(view).navigate(R.id.navigation_route_detail_fragment, argsBundle);
        }
    }
}
