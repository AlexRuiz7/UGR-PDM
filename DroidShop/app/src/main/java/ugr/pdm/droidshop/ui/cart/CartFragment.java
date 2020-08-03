package ugr.pdm.droidshop.ui.cart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ugr.pdm.droidshop.R;
import ugr.pdm.droidshop.adapters.CartAdapter;
import ugr.pdm.droidshop.models.CartItem;
import ugr.pdm.droidshop.utils.State;


public class CartFragment extends Fragment {

    private RecyclerView mRecycleView;
    private ArrayList<CartItem> mProductsData;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private CartAdapter mAdapter;

    private TextView emptyMessageTextView;
    private TextView totalTextView;
    private LinearLayout cartLayout;
    private Button buyButton;

    private double orderTotal;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean userIsLogged = State.getInstance().isLogged();
        if (!userIsLogged) {
            Navigation
                    .findNavController(getActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.nav_login);

            return getView();
        }
        else {
            // Inflate the layout for this fragment
            View root = inflater.inflate(R.layout.fragment_cart, container, false);

            initViews(root);
            setupRecyclerView(root);
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
        emptyMessageTextView = root.findViewById(R.id.cartEmpty);
        emptyMessageTextView.setVisibility(View.GONE);
        cartLayout = root.findViewById(R.id.cartLayout);
        totalTextView = root.findViewById(R.id.orderTotal);
        totalTextView.setText("-");
        buyButton = root.findViewById(R.id.buyButton);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                final DatabaseReference userRef = mDatabase.child("users").child(user.getUid());

                // Procesar compra
                userRef.child("orders").push().setValue(mProductsData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Order completed", Toast.LENGTH_SHORT).show();

                                // Vaciar carrito
                                mProductsData.clear();
                                mAdapter.notifyDataSetChanged();
                                userRef.child("cart").removeValue();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("COMPRA", e.getMessage());
                            }
                        });
            }
        });
    }


    /**
     *
     * @param root
     */
    private void setupRecyclerView(View root) {
        // Enlazar RecyclerView
        mRecycleView = root.findViewById(R.id.cartRecyclerView);

        // Añadir Layout
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        layout.setOrientation(RecyclerView.VERTICAL);
        layout.findLastCompletelyVisibleItemPosition();
        mRecycleView.setLayoutManager(layout);

        // Inicializar ArrayList
        mProductsData = new ArrayList<>();

        // Inicializar adaptador y enlazarlo al RecyclerView
        mAdapter = new CartAdapter(mProductsData, getContext());
        mRecycleView.setAdapter(mAdapter);
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
                .child("cart")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() == 0) {
                            emptyMessageTextView.setVisibility(View.VISIBLE);
                            cartLayout.setVisibility(View.INVISIBLE);
                        }
                        else {
                            mProductsData.clear();
                            cartLayout.setVisibility(View.VISIBLE);
                            orderTotal = 0;
                            for (DataSnapshot obj : snapshot.getChildren()) {
                                CartItem p = obj.getValue(CartItem.class);
                                mProductsData.add(p);
                                orderTotal += p.getSubtotal();
                            }
                            mAdapter.notifyDataSetChanged();
                            totalTextView.setText(orderTotal + " €");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("CART", error.getMessage());
                    }
                });
    }


    /**
     *
     */
//    private void fakeInit() {
//        String productName = "Product ";
//        String productInfo = "DEFAULT";
//        Product p;
//        for (int i=0; i<100; i++) {
//            p = new Product(productName + i, productName, productInfo, i+"€");
//            mProductsData.add(p);
//        }
//    }

}