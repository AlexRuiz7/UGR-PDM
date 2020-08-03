package ugr.pdm.granadatour.ui.routes;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import ugr.pdm.granadatour.MapActivity;
import ugr.pdm.granadatour.R;
import ugr.pdm.granadatour.models.Item;
import ugr.pdm.granadatour.models.Route;

public class RoutesDetailedViewFragment extends Fragment {

    private Route mRoute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_routes, container, false);

        TextView title = root.findViewById(R.id.itemTitleDetail);
        TextView subtitle = root.findViewById(R.id.itemSubtitleDetail);
        TextView info = root.findViewById(R.id.itemDescriptionDetail);
        ImageView imageView = root.findViewById(R.id.itemImageDetail);

        if (getArguments() != null) {
            mRoute = (Route) getArguments().getSerializable(Item.SERIALZABLE_KEY);

            title.setText(mRoute.getTitle());
            subtitle.setText(mRoute.getSubtitle());
            info.setText(mRoute.getInfo());
            if (mRoute.isValidURL()) {
                StorageReference gsReference = mRoute.getImageReference();
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

        // Mostrar lista de lugares que componen la ruta
        root.findViewById(R.id.open_list_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle argsBundle = new Bundle();
                argsBundle.putStringArray(Item.SERIALZABLE_KEY, mRoute.getRoutePoints().split(","));
                Navigation.findNavController(view).navigate(R.id.navigation_places_fragment, argsBundle);
            }
        });


        // Abrir mapa. Se mandan los IDs de los lugares de la ruta a la actividad del mapa
        root.findViewById(R.id.open_map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapBoxIntent = new Intent(getActivity(), MapActivity.class);
                mapBoxIntent.putExtra(Item.SERIALZABLE_KEY, mRoute.getRoutePoints());
                startActivity(mapBoxIntent);
            }
        });


        // Botones para cambiar el tamaño del texto
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
