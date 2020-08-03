package ugr.pdm.granadatour;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ugr.pdm.granadatour.models.Item;
import ugr.pdm.granadatour.models.Place;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private static final String rootReference = "places";
    private MapView mapView;
    private List<Feature> mPlacesList = new ArrayList<>();

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_map_view);

        // Recuperar IDs desde el intent y consultar coordenadas en Firebase
        String args = getIntent().getStringExtra(Item.SERIALZABLE_KEY);
        initializeData(Arrays.asList(args.split(",")));

        // Esperar a recibir las coordenadas de los lugares
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }


    /**
     * Consulta las coordenadas de lus lugares de la ruta en Firebase y las añade la lista de puntos
     * usada por MapBox
     *
     * @param idLugares
     */
    private void initializeData(List<String> idLugares) {
        DatabaseReference DBref = FirebaseDatabase.getInstance().getReference(rootReference);

        mPlacesList.clear();

        for (String id : idLugares) {
            DatabaseReference placeRef = DBref.child(id.trim());

            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Place place = snapshot.getValue(Place.class);
                    String placeCoords = place.getCoords();

                    double latitude = Double.parseDouble(placeCoords.split(",")[0]);
                    double longitude = Double.parseDouble(placeCoords.split(",")[1]);

                    mPlacesList.add(Feature.fromGeometry(Point.fromLngLat(longitude, latitude)));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ERROR",  error.getMessage());
                }
            };

            placeRef.addListenerForSingleValueEvent(eventListener);
        }

    }


    /**
     *
     * @param mapboxMap
     */
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")

                // Añadir le estilo del marcador
                .withImage(ICON_ID, BitmapFactory.decodeResource(
                        MapActivity.this.getResources(), com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default)
                )
                // Información de los marcadores en formato GeoJson
                .withSource(new GeoJsonSource(SOURCE_ID,
                        FeatureCollection.fromFeatures(mPlacesList))
                )
                // Dibujar el marcador
                .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(PropertyFactory.iconImage(ICON_ID),
                                iconAllowOverlap(true),
                                iconOffset(new Float[] {0f, -9f}))
                )
        );
    }

    /** Override de métodos necesario por parte de MapBox **/

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}



//                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
//                    @Override
//                    public void onStyleLoaded(@NonNull Style style) {
//                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
//
//                    }
//                });