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
import ugr.pdm.droidshop.models.CartItem;
import ugr.pdm.droidshop.models.Product;

/**
 * Adaptor para la lista de productos
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    // Member variables.
    private ArrayList<CartItem> mProductsData;
    private Context mContext;


    /**
     * Constructor
     *
     * @param mProductsData
     * @param mContext
     */
    public CartAdapter(ArrayList<CartItem> mProductsData, Context mContext) {
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
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartAdapter.ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.product_on_cart_item, parent, false));
    }


    /**
     * Método requerido para ligar los datos al viewholder
     *
     * @param holder El viewholder que contendrá los datos
     * @param position la posición del adaptador
     */
    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        // Coger el producto actual
        CartItem item = mProductsData.get(position);

        // Rellenar con los datos
        holder.bindTo(item);
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
        private TextView mQuantityText;
        private TextView mSubtotalText;
        private Button mDeleteButton;


        /**
         * Constructor
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializar las vistas
            mTitleText = itemView.findViewById(R.id.cartItemName);
            mPriceText = itemView.findViewById(R.id.cartItemPrice);
            mQuantityText = itemView.findViewById(R.id.cartItemQuantity);
            mSubtotalText = itemView.findViewById(R.id.cartItemSubtotal);
            mDeleteButton= itemView.findViewById(R.id.cartItemDelete);

            // Listeners
            itemView.setOnClickListener(this);
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CartItem p = mProductsData.remove(getAdapterPosition());
                    notifyDataSetChanged();

                    // Eliminar de Firebase
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseDatabase
                            .getInstance()
                            .getReference("users")
                            .child(user.getUid())
                            .child("cart")
                            .child(p.getProduct().getTitle())
                            .removeValue();
                }
            });
        }


        /**
         *Rellenar las vistas con datos
         * @param item
         */
        public void bindTo(CartItem item) {
            if (item != null) {
                mTitleText.setText(item.getProduct().getTitle());
                mPriceText.setText(item.getProduct().getPriceCurrency());
                mQuantityText.setText(String.valueOf(item.getQuantity()));
                mSubtotalText.setText(item.getSubtotalCurrency());
            }
        }


        /**
         *
         * @param view
         */
        @Override
        public void onClick(View view) {
            CartItem item = mProductsData.get(getAdapterPosition());

            // Navegar a vista del producto
            Bundle argsBundle = new Bundle();
            argsBundle.putSerializable(Product.SERIALIZABLE_KEY, item.getProduct());
            Navigation.findNavController(view)
                    .navigate(R.id.nav_product, argsBundle);
        }
    }
}
