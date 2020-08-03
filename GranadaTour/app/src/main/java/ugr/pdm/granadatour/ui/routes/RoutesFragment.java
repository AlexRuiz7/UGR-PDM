package ugr.pdm.granadatour.ui.routes;

import android.os.Bundle;
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

import ugr.pdm.granadatour.R;
import ugr.pdm.granadatour.adapters.RouteAdapter;
import ugr.pdm.granadatour.models.Route;

public class RoutesFragment extends Fragment {

    private ArrayList<Route> mRoutesData;
    private RouteAdapter mAdapter;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_routes, container, false);
        setupRecyclerView(root);
        initializeData();

        return root;
    }

    /**
     *
     * @param root
     */
    private void setupRecyclerView(View root) {
        // Initialize the RecyclerView.
        RecyclerView mRecyclerView = root.findViewById(R.id.routesRecyclerView);
        // Set the Layout Manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Initialize the ArrayList that will contain the data.
        mRoutesData = new ArrayList<>();
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new RouteAdapter(this.getContext(), mRoutesData);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     *
     */
    private void initializeData() {
        FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();
        DatabaseReference DBref = firebaseDB.getReference();
        DatabaseReference routesRef = DBref.child("routes");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mRoutesData.clear();

                for (DataSnapshot routeSnapshot : snapshot.getChildren()) {
                    Route route = routeSnapshot.getValue(Route.class);
                    mRoutesData.add(route);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };

        routesRef.addListenerForSingleValueEvent(eventListener);

        // Notify the adapter of the change.
        mAdapter.notifyDataSetChanged();
    }

}