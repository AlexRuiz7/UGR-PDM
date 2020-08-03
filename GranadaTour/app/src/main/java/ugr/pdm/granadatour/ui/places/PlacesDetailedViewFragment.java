package ugr.pdm.granadatour.ui.places;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import ugr.pdm.granadatour.R;
import ugr.pdm.granadatour.models.Item;
import ugr.pdm.granadatour.models.Place;

public class PlacesDetailedViewFragment extends Fragment {

    private Place mPlace;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_places, container, false);

        TextView title = root.findViewById(R.id.itemTitleDetail);
        TextView subtitle = root.findViewById(R.id.itemSubtitleDetail);
        TextView info = root.findViewById(R.id.itemDescriptionDetail);
        ImageView imageView = root.findViewById(R.id.itemImageDetail);

        if (getArguments() != null) {
            mPlace = (Place) getArguments().getSerializable(Item.SERIALZABLE_KEY);

            title.setText(mPlace.getTitle());
            subtitle.setText(mPlace.getSubtitle());
            info.setText(mPlace.getInfo());
            if (mPlace.isValidURL()) {
                StorageReference gsReference = mPlace.getImageReference();
                Glide.with(this).load(gsReference).into(imageView);
            }

            createButtonOnClickHandlers(root);
        }

        return root;
    }


    /**
     * Crea y añade onClickEventListeners a los botones de acciones en la vista detallada
     *
     * @param root vista raíz
     */
    private void createButtonOnClickHandlers(View root) {

        final String coordsString = "geo:" + mPlace.getCoords() + "?q=";

        // Ver lugar en Google Maps
        root.findViewById(R.id.open_map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Button", "Location button");

                Uri addressUri = Uri.parse(coordsString + Uri.encode(mPlace.getTitle()));
                Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);
                intent.setPackage("com.google.android.apps.maps");

                // Find an activity to handle the intent, and start that activity.
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.e("ImplicitIntent", "Can't handle this intent!");
                }
            }
        });

        // Ver restaurantes cerca en Google Maps
        root.findViewById(R.id.open_restaurants_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Button", "Restaurants button");

                Uri addressUri = Uri.parse(coordsString + Uri.encode("restaurants"));
                Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);
                intent.setPackage("com.google.android.apps.maps");

                // Find an activity to handle the intent, and start that activity.
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.e("ImplicitIntents", "Can't handle this intent!");
                }
            }
        });

        // Ver hoteles cerca en Google Maps
        root.findViewById(R.id.open_hotels_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Button", "Hotels button");

                Uri addressUri = Uri.parse(coordsString + Uri.encode("hotels"));
                Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);
                intent.setPackage("com.google.android.apps.maps");

                // Find an activity to handle the intent, and start that activity.
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.e("ImplicitIntents", "Can't handle this intent!");
                }
            }
        });

        // Ver parkings cerca en Google Maps
        root.findViewById(R.id.open_parkings_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Location button", "Parkings button");

                Uri addressUri = Uri.parse(coordsString + Uri.encode("parkings"));
                Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);
                intent.setPackage("com.google.android.apps.maps");

                // Find an activity to handle the intent, and start that activity.
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.e("ImplicitIntents", "Can't handle this intent!");
                }
            }
        });


        final TextView info = root.findViewById(R.id.itemDescriptionDetail);

        root.findViewById(R.id.zoomIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float currentTextSize = info.getTextSize();

                if (currentTextSize < 65.0)
                    info.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize+2);
            }
        });

        root.findViewById(R.id.zoomOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float currentTextSize = info.getTextSize();

                if (currentTextSize > 41.0)
                    info.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize-2);
            }
        });

    }


}
