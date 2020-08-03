package ugr.pdm.droidshop.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ugr.pdm.droidshop.R;
import ugr.pdm.droidshop.models.Product;

/**
 * Adaptor para la lista de productos
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {

    // Member variables.
    private ArrayList<Product> mProductsData;
    private ArrayList<Product> mProductsDataFull;
    private Context mContext;


    /**
     * Constructor
     *
     * @param mProductsData
     * @param mContext
     */
    public ProductAdapter(ArrayList<Product> mProductsData, Context mContext) {
        this.mProductsData = mProductsData;
        this.mContext = mContext;
        mProductsDataFull = new ArrayList<>(mProductsData);
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
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductAdapter.ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.product_preview, parent, false));
    }


    /**
     * Método requerido para ligar los datos al viewholder
     *
     * @param holder El viewholder que contendrá los datos
     * @param position la posición del adaptador
     */
    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
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
     *
     * @return
     */
    @Override
    public Filter getFilter() {
        return productFilter;
    }

    private Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Product> filteredList = new ArrayList<>();
//            mProductsDataFull = new ArrayList<>(mProductsData);

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(mProductsDataFull);
                Log.e("FILTER", filteredList.size()+"");
            }
            else {
                String filterPatten = charSequence.toString().toLowerCase().trim();

                for (Product p : mProductsDataFull) {
                    if (p.getTitle().toLowerCase().contains(filterPatten) ) {
                        filteredList.add(p);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mProductsData.clear();
            mProductsData.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };


    /**
     * Se realiza la copia cuando se acaba de cargar los datos desde Firebase
     */
    public void copyList() {
        mProductsDataFull = new ArrayList<>(mProductsData);
    }

    /**
     * Clase ViweHolder, representa cada elemento del RecyclerView
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {

        /** Atributos. Contenedores visuales en los que introducir los datos */
        private ImageView mImageView;
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;


        /**
         * Constructor
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializar las vistas
            mImageView = itemView.findViewById(R.id.productPreview);
            mTitleText = itemView.findViewById(R.id.productPreviewName);
            mInfoText = itemView.findViewById(R.id.productPreviewInfo);
            mPriceText = itemView.findViewById(R.id.productPreviewPrice);

            itemView.setOnClickListener(this);
        }


        /**
         *
         * @param product
         */
        public void bindTo(Product product) {
            if (product != null) {
                // Rellenar las vistas con datos
                mTitleText.setText(product.getTitle());
                mInfoText.setText(product.getSubtitle());
                mPriceText.setText(product.getPriceCurrency());

                if (product.isValidURL()) {
                    Glide.with(mContext)
                            .load(product.getImageReference())
                            .fitCenter()
                            .into(mImageView);
                }
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
