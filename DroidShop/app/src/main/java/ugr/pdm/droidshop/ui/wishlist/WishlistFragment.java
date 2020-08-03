package ugr.pdm.droidshop.ui.wishlist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import ugr.pdm.droidshop.adapters.WishlistAdapter;
import ugr.pdm.droidshop.models.Product;
import ugr.pdm.droidshop.utils.State;

public class WishlistFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<Product> mProductsData;
    private DatabaseReference mUserRef;
    private WishlistAdapter mAdapter;
    private TextView emptyMessageTextView;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean userIsLogged = State.getInstance().isLogged();
        if (!userIsLogged) {
            Navigation
                    .findNavController(getActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.nav_login);

            return getView();
        }
        else {
            View root = inflater.inflate(R.layout.fragment_wishlist, container, false);
            emptyMessageTextView = root.findViewById(R.id.wishlistEmpty);
            emptyMessageTextView.setVisibility(View.GONE);

            setupFirebase();
            setupRecyclerView(root);
            initializeData();

            return root;
        }

    }


    /**
     * Inicializa las referenicias a Firebase
     */
    private void setupFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            mUserRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
    }


    /**
     *
     * @param root
     */
    private void setupRecyclerView(View root) {
        // Enlazar RecyclerView
        mRecyclerView = root.findViewById(R.id.wishlistRecyclerView);
        // Añadir Layout
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        layout.setOrientation(RecyclerView.VERTICAL);
        layout.findLastCompletelyVisibleItemPosition();
        mRecyclerView.setLayoutManager(layout);
        // Inicializar ArrayList
        mProductsData = new ArrayList<>();
        // Inicializar adaptador y enlazarlo al RecyclerView
        mAdapter = new WishlistAdapter(mProductsData, getContext());
        mRecyclerView.setAdapter(mAdapter);
    }


    /**
     *
     */
    private void initializeData() {

        mUserRef.child("wishlist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    emptyMessageTextView.setVisibility(View.VISIBLE);
                }
                else {
                    mProductsData.clear();
                    for (DataSnapshot obj : snapshot.getChildren()) {
                        Product p = obj.getValue(Product.class);
                        mProductsData.add(p);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("WISHLIST", error.getMessage());
            }
        });
    }


    /**
     *
     */
    private void fakeInit() {
        String productName = "Product ";
        String productInfo = "DEFAULT";
        Product p;
        for (int i=0; i<100; i++) {
            p = new Product(productName + i, productName, productInfo, i);
            mProductsData.add(p);
        }
    }

}