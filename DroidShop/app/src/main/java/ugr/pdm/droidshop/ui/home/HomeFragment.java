package ugr.pdm.droidshop.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ugr.pdm.droidshop.R;
import ugr.pdm.droidshop.adapters.ProductAdapter;
import ugr.pdm.droidshop.models.Product;

public class HomeFragment extends Fragment {

    private RecyclerView mRecycleView;
    private ArrayList<Product> mProductsData;
    private DatabaseReference mDatabase;
    private ProductAdapter mAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference("products");

        setupRecyclerView(root);
        initializeData();

        return root;
    }


    /**
     *
     * @param root
     */
    private void setupRecyclerView(View root) {
        // Enlazar RecyclerView
        mRecycleView = root.findViewById(R.id.productRecyclerView);
        // Añadir Layout
        mRecycleView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        // Inicializar ArrayList
        mProductsData = new ArrayList<>();
        // Inicializar adaptador y enlazarlo al RecyclerView
        mAdapter = new ProductAdapter(mProductsData, getContext());
        mRecycleView.setAdapter(mAdapter);
    }


    /**
     * Carga los datos desde Firebase
     */
    private void initializeData() {

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mProductsData.clear();
                for (DataSnapshot obj : snapshot.getChildren()) {
                    Product p = obj.getValue(Product.class);
                    p.setKey(obj.getKey());
                    mProductsData.add(p);
                }
                mAdapter.notifyDataSetChanged();
                mAdapter.copyList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HOME", error.getMessage());
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Inflar el menú
        inflater.inflate(R.menu.main_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });

    }

    /**
     * Rellena el recycler view con datos de prueba
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