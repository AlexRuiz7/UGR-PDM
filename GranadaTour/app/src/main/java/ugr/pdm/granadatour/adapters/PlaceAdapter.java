package ugr.pdm.granadatour.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import ugr.pdm.granadatour.R;
import ugr.pdm.granadatour.models.Item;
import ugr.pdm.granadatour.models.Place;

/***
 * The adapter class for the RecyclerView, contains the places data.
 */
public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    // Member variables.
    private ArrayList<Place> mPlacesData;
    private Context mContext;

    /**
     * Constructor that passes in the Routes data and the context.
     *
     * @param PlacesData ArrayList containing the places data.
     * @param context    Context of the application.
     */
    public PlaceAdapter(Context context, ArrayList<Place> PlacesData) {
        this.mPlacesData = PlacesData;
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
    @Override
    public PlaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(PlaceAdapter.ViewHolder holder, int position) {
        // Get current place.
        Place currentPlace = mPlacesData.get(position);

        // Populate the textviews with data.
        holder.bindTo(currentPlace);
    }

    /**
     * Required method for determining the size of the data set.
     *
     * @return Size of the data set.
     */
    @Override
    public int getItemCount() {
        return mPlacesData.size();
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
         * @param placeView The rootview of the list_item.xml layout file.
         */
        ViewHolder(View placeView) {
            super(placeView);

            // Initialize the views.
            mTitleText = placeView.findViewById(R.id.itemTitle);
            mSubtitleText = placeView.findViewById(R.id.itemSubtitle);
            mRoutesImage = placeView.findViewById(R.id.itemImage);

            placeView.setOnClickListener(this);
        }

        void bindTo(Place currentPlace) {
             if (currentPlace != null) {
                // Populate the views with data.
                mTitleText.setText(currentPlace.getTitle());
                mSubtitleText.setText((currentPlace.getSubtitle()));
             }

            // Create a reference to a file from a Google Cloud Storage URI
            if (currentPlace.isValidURL()) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference gsReference = storage.getReferenceFromUrl(currentPlace.getImage());
                Glide.with(mContext)
                        .load(gsReference)
                        .centerCrop()
                        .into(mRoutesImage);
            }
        }

        @Override
        public void onClick(View view) {
            Place place = mPlacesData.get(getAdapterPosition());

            Bundle argsBundle = new Bundle();
            argsBundle.putSerializable(Item.SERIALZABLE_KEY, place);
            Navigation.findNavController(view).navigate(R.id.navigation_place_detail_fragment, argsBundle);
        }
    }
}
