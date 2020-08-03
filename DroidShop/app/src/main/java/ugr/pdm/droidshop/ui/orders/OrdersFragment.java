package ugr.pdm.droidshop.ui.orders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ugr.pdm.droidshop.R;
import ugr.pdm.droidshop.adapters.OrderAdapter;
import ugr.pdm.droidshop.adapters.WishlistAdapter;
import ugr.pdm.droidshop.models.CartItem;
import ugr.pdm.droidshop.models.Product;
import ugr.pdm.droidshop.utils.State;

public class OrdersFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<CartItem> mProductsData;
    private OrderAdapter mAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private TextView emptyMessageTextView;
    private LinearLayout mHeaders;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean userIsLogged = State.getInstance().isLogged();
        if (!userIsLogged) {
            Navigation
                    .findNavController(getActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.nav_login);

            return getView();
        }
        else {
            View root = inflater.inflate(R.layout.fragment_orders, container, false);

            initViews(root);
            setupRecyclerView();
            initFirebase();
            initializeData();

            return root;
        }
    }


    /**
     *
     * @param root
     */
    private void initViews(View root) {
        emptyMessageTextView = root.findViewById(R.id.ordersEmpty);
        emptyMessageTextView.setVisibility(View.VISIBLE);
        mRecyclerView = root.findViewById(R.id.ordersRecyclerView);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mHeaders = root.findViewById(R.id.include);
        mHeaders.setVisibility(View.INVISIBLE);
    }


    /**
     *
     */
    private void setupRecyclerView() {
        // Añadir Layout
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        layout.setOrientation(RecyclerView.VERTICAL);
        layout.findLastCompletelyVisibleItemPosition();
        mRecyclerView.setLayoutManager(layout);

        // Inicializar ArrayList
        mProductsData = new ArrayList<>();

        // Inicializar adaptador y enlazarlo al RecyclerView
        mAdapter = new OrderAdapter(mProductsData, getContext());
        mRecyclerView.setAdapter(mAdapter);
    }


    /**
     *
     */
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    /**
     *
     */
    private void initializeData() {
        FirebaseUser user = mAuth.getCurrentUser();

        mDatabase
                .child("users")
                .child(user.getUid())
                .child("orders")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.getChildrenCount() != 0) {
                            mProductsData.clear();

                            for (DataSnapshot order : snapshot.getChildren()) {
                                for (DataSnapshot item : order.getChildren()) {
                                    CartItem p = item.getValue(CartItem.class);
                                    mProductsData.add(p);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            emptyMessageTextView.setVisibility(View.GONE);
                            mHeaders.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("CART", error.getMessage());
                    }
                });
    }

}