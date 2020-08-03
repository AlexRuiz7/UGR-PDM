package ugr.pdm.droidshop.ui.products;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ugr.pdm.droidshop.R;
import ugr.pdm.droidshop.adapters.ReviewAdapter;
import ugr.pdm.droidshop.models.CartItem;
import ugr.pdm.droidshop.models.Product;
import ugr.pdm.droidshop.models.Review;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductFragment} factory method to
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment {

    private ImageView mImageView;
    private TextView mTitleText;
    private TextView mSubtitleText;
    private TextView mInfoText;
    private TextView mPriceText;
    private TextView mItemsCounter;
    private TextView mReviewText;
    private Button mWriteReviewButton;
    private ImageButton mCartButton;

    private Product mProduct;
    private int mItemsCount;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private RecyclerView mRecycleView;
    private ArrayList<Review> mReviewsData;
    private ReviewAdapter mAdapter;

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_product, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Inicializar las vistas
        initializeViews(root);
        setupRecyclerView();
        initializeData();
        createButtonHandlers(root);

        return root;
    }


    /**
     * Recupera las vistas
     *
     * @param root vista actual
     */
    private void initializeViews(View root) {
        mImageView = root.findViewById(R.id.productImage);
        mTitleText = root.findViewById(R.id.productName);
        mSubtitleText = root.findViewById(R.id.productSubtitle);
        mInfoText = root.findViewById(R.id.productInfo);
        mPriceText = root.findViewById(R.id.productPrice);
        mItemsCounter = root.findViewById(R.id.quantityCounter);
        mWriteReviewButton = root.findViewById(R.id.reviewInput);
        mRecycleView = root.findViewById(R.id.commentsRecyclerView);
        mCartButton = root.findViewById(R.id.cartAddButton);
        mReviewText = root.findViewById(R.id.reviewInputText);

        if (mAuth.getCurrentUser() == null) {
            mReviewText.setEnabled(false);
        }
    }


    /**
     *
     */
    private void setupRecyclerView() {
        // Añadir Layout
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        layout.setOrientation(RecyclerView.VERTICAL);
        mRecycleView.setLayoutManager(layout);

        // Inicializar ArrayList
        mReviewsData = new ArrayList<>();

        // Inicializar adaptador y enlazarlo al RecyclerView
        mAdapter = new ReviewAdapter(mReviewsData, getContext());
        mRecycleView.setAdapter(mAdapter);
    }


    /**
     * Rellena las vistas con los datos del producto
     */
    private void initializeData() {

        if (getArguments() != null) {
            mProduct = (Product) getArguments().getSerializable(Product.SERIALIZABLE_KEY);
            mTitleText.setText(mProduct.getTitle());
            mSubtitleText.setText(mProduct.getSubtitle());
            mInfoText.setText(mProduct.getInfo());
            mPriceText.setText(mProduct.getPriceCurrency());

            Log.e("PRODUCT", mProduct.toString());

            // Cargar imagen
            if (mProduct.isValidURL()) {
                Glide.with(getContext())
                        .load(mProduct.getImageReference())
                        .fitCenter()
                        .into(mImageView);
            }
        }

        // Cargar comentarios y añadir listener
        mDatabase
                .child("products")
                .child(mProduct.getKey())
                .child("reviews")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mReviewsData.clear();
                        for (DataSnapshot obj : snapshot.getChildren()) {
                            Review r = obj.getValue(Review.class);
                            r.setProductID(mProduct.getKey());
                            r.setReviewID(obj.getKey());
                            mReviewsData.add(r);
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("PRODUCT_REVIEW", error.getMessage());
                    }
                });
    }


    /**
     * Crea los manejadores de eventos de los botones
     *
     * @param root vista actual
     */
    private void createButtonHandlers(View root) {
        // Aumentar cantidad de productos
        root.findViewById(R.id.quantityAddButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int number = Integer.parseInt(mItemsCounter.getText().toString());
                        mItemsCounter.setText(String.valueOf(number + 1));

                        // Habilitamos el botón de añadir al carrito
                        if (number == 0)
                            mCartButton.setClickable(true);
                    }
                });

        // Reducir cantidad de productos
        root.findViewById(R.id.quantityRemoveButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int number = Integer.parseInt(mItemsCounter.getText().toString());

                        // Evitamos valores negativos
                        if (number - 1 >= 0) {
                            mItemsCounter.setText(String.valueOf(number - 1));
                        }
                        // Deshabilitamos el botón de añadir al carrito si la cantidad será cero
                        if (number == 1) {
                            mCartButton.setClickable(false);
                        }
                    }
                });

        // Añadir cantidad al carrito
        mCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();

                if (user == null) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Authentication required")
                            .setMessage("Create an account or login to use the cart")
                            .setPositiveButton("Close", null)
                            .create()
                            .show();
                } else {
                    mItemsCount = Integer.parseInt(mItemsCounter.getText().toString());
                    CartItem items = new CartItem(mProduct, mItemsCount);

                    mDatabase
                            .child("users")
                            .child(user.getUid())
                            .child("cart")
                            .child(mProduct.getTitle())
                            .setValue(items)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    makeToast(mItemsCount + " items added to cart");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("ADD TO CART", e.getMessage());
                                }
                            });
                }
            }
        });

        // Añadir elemento a la lista de deseos
        root.findViewById(R.id.wishlistAddButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user == null) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Authentication required")
                                    .setMessage("Create an account or login to have a wishlist")
                                    .setPositiveButton("Close", null)
                                    .create()
                                    .show();
                        } else {
                            mDatabase
                                    .child("users")
                                    .child(user.getUid())
                                    .child("wishlist")
                                    .child(mProduct.getTitle())
                                    .setValue(mProduct)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            makeToast("Added to wishlist");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("ADD TO WISHLIST", e.getMessage());
                                        }
                                    });
                        }
                    }
                });

        // Enviar comentario
        mWriteReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseUser user = mAuth.getCurrentUser();

                if (user == null) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Authentication required")
                            .setMessage("Create an account or login to write a review")
                            .setPositiveButton("Close", null)
                            .create()
                            .show();
                } else {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy - HH:mm:ss");

                    String text = mReviewText.getText().toString();
                    if (text.equals(""))
                        mReviewText.setError(getString(R.string.required_field));
                    else {
                        String UUID = user.getUid();
                        String name = user.getDisplayName();
                        String date = formatter.format(calendar.getTime());

                        final Review r = new Review(UUID, name, text, date);

                        mDatabase
                                .child("products")
                                .child(mProduct.getKey())
                                .child("reviews")
                                .push()
                                .setValue(r)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        makeToast("Review sent");

                                        // Limpiar el campo de texto
                                        mReviewText.setText("");
                                        mReviewText.clearFocus();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("REVIEW", e.getMessage());
                                    }
                                });
                    }
                }
            }
        });
    }


    /*********************/
    /** UTILIDADES       */
    /*********************/
    private void makeToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * @param n
     */
//    private void fakeInit(int n) {
//        mReviewsData.clear();
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy - HH:mm:ss");
//
//
//        String UUID = java.util.UUID.randomUUID().toString();
//        String username = "Fake Username";
//        String text = getString(R.string.review_text_placeholder);
//        String date = formatter.format(calendar.getTime());
//        Review r;
//        for (int i = 0; i < n; i++) {
//            r = new Review(UUID, username + i, i + 1 + "", text, date);
//            mReviewsData.add(r);
//        }
//    }
}