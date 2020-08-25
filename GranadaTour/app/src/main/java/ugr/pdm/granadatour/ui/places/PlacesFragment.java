package ugr.pdm.granadatour.ui.places;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ugr.pdm.granadatour.R;
import ugr.pdm.granadatour.adapters.PlaceAdapter;
import ugr.pdm.granadatour.models.Item;
import ugr.pdm.granadatour.models.Place;
import ugr.pdm.granadatour.utils.LoadingSpinner;

public class PlacesFragment extends Fragment {

    private ArrayList<Place> mPlacesData;
    private PlaceAdapter mAdapter;
    private final String rootReference = "places";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_places, container, false);
        setupRecyclerView(root);

        // Carga todos los lugares (pantalla Lugares)
        if (getArguments() == null) {
            initializeData();
        }
        // Carga los lugares específicos de una ruta, dados como argumentos a este fragment
        else {
            List<String> IDLugares = Arrays.asList(getArguments().getStringArray(Item.SERIALZABLE_KEY));
            initializeData(IDLugares);
        }

        return root;
    }

    /**
     * Inicializa el Recycler View
     *
     * @param root
     */
    private void setupRecyclerView(View root) {
        // Initialize the RecyclerView.
        RecyclerView mRecyclerView = root.findViewById(R.id.placesRecyclerView);
        // Set the Layout Manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Initialize the ArrayList that will contain the data.
        mPlacesData = new ArrayList<>();
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new PlaceAdapter(this.getContext(), mPlacesData);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Carga todos los lugares existentes en la base de datos de Firebase
     */
    private void initializeData() {
        LoadingSpinner.getInstance().setLoading(true);
        FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();
        DatabaseReference DBref = firebaseDB.getReference();
        DatabaseReference placesRef = DBref.child(rootReference);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPlacesData.clear();

                for (DataSnapshot placeSnapshot : snapshot.getChildren()) {
                    Place place = placeSnapshot.getValue(Place.class);
                    mPlacesData.add(place);
                }

                // Notify the adapter of the change.
                mAdapter.notifyDataSetChanged();
                LoadingSpinner.getInstance().setLoading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };

        placesRef.addListenerForSingleValueEvent(eventListener);
    }

    /**
     * Carga la información de los lugares recibidos como argumentos a este metodo, representados por
     * su ID en la base de datsode Firebase.
     *
     * Este método se utiliza cuando se quiere cargar solo ciertos lugares, y no todos, como los
     * lugares pertenecientes a una ruta concreta,
     *
     * @param idLugares IDs de los lugares a cargar
     */
    private void initializeData(List<String> idLugares) {
        FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();
        DatabaseReference DBref = firebaseDB.getReference(rootReference);

        mPlacesData.clear();

        for (String id : idLugares) {
            DatabaseReference placeRef = DBref.child(id.trim());
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    LoadingSpinner.getInstance().setLoading(true);
                    Place place = snapshot.getValue(Place.class);
                    mPlacesData.add(place);
                    mAdapter.notifyDataSetChanged();
                    LoadingSpinner.getInstance().setLoading(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ERROR",  error.getMessage());
                }
            };

            placeRef.addValueEventListener(eventListener);
        }
    }

}