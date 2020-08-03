package ugr.pdm.droidshop.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ugr.pdm.droidshop.R;
import ugr.pdm.droidshop.models.Product;

/**
 * Adaptor para la lista de productos
 */
public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    // Member variables.
    private ArrayList<Product> mProductsData;
    private Context mContext;


    /**
     * Constructor
     *
     * @param mProductsData
     * @param mContext
     */
    public WishlistAdapter(ArrayList<Product> mProductsData, Context mContext) {
        this.mProductsData = mProductsData;
        this.mContext = mContext;
    }


    /**
     * Método requerido para crear objetos viewholders
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public WishlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WishlistAdapter.ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.wishlist_item, parent, false));
    }


    /**
     * Método requerido para ligar los datos al viewholder
     *
     * @param holder El viewholder que contendrá los datos
     * @param position la posición del adaptador
     */
    @Override
    public void onBindViewHolder(@NonNull WishlistAdapter.ViewHolder holder, int position) {
        // Coger el producto actual
        Product product = mProductsData.get(position);

        // Rellenar con los datos
        holder.bindTo(product);
    }


    /**
     * Método requerido para determinar el tamaño del conjunto de datos
     *
     * @return tamaño del contenedor de datos
     */
    @Override
    public int getItemCount() {
        return mProductsData.size();
    }


    /**
     * Clase ViweHolder, representa cada elemento del RecyclerView
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /** Atributos. Contenedores visuales en los que introducir los datos */
        private TextView mTitleText;
        private TextView mPriceText;
        private Button mDeleteButton;


        /**
         * Constructor
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializar las vistas
            mTitleText = itemView.findViewById(R.id.wishlistItemProductName);
            mPriceText = itemView.findViewById(R.id.wishlistItemPriceName);
            mDeleteButton= itemView.findViewById(R.id.wishlistItemDelete);

            // Listeners
            itemView.setOnClickListener(this);
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Product p = mProductsData.remove(getAdapterPosition());
                    notifyDataSetChanged();

                    // Eliminar de Firebase
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("users");
                    dbref.child(user.getUid()).child("wishlist").child(p.getTitle()).removeValue();
                }
            });
        }



        /**
         *
         * @param product
         */
        public void bindTo(Product product) {
            if (product != null) {
                // Rellenar las vistas con datos
                mTitleText.setText(product.getTitle());
                mPriceText.setText(product.getPriceCurrency());
            }
        }


        /**
         *
         * @param view
         */
        @Override
        public void onClick(View view) {
            Product product = mProductsData.get(getAdapterPosition());

            // Navegar a vista del producto
            Bundle argsBundle = new Bundle();
            argsBundle.putSerializable(Product.SERIALIZABLE_KEY, product);
            Navigation.findNavController(view)
                    .navigate(R.id.nav_product, argsBundle);
        }

    }
}
