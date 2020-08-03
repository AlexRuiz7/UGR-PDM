package ugr.pdm.droidshop.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ugr.pdm.droidshop.R;
import ugr.pdm.droidshop.models.Review;

/**
 * Adaptor para la lista de reviewos
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    // Member variables.
    private ArrayList<Review> mReviewData;
    private Context mContext;


    /**
     * Constructor
     *
     * @param mReviewData
     * @param mContext
     */
    public ReviewAdapter(ArrayList<Review> mReviewData, Context mContext) {
        this.mReviewData = mReviewData;
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
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewAdapter.ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.product_review, parent, false));
    }


    /**
     * Método requerido para ligar los datos al viewholder
     *
     * @param holder El viewholder que contendrá los datos
     * @param position la posición del adaptador
     */
    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        // Coger el comentario actual
        Review review = mReviewData.get(position);

        // Rellenar con los datos
        holder.bindTo(review);
    }


    /**
     * Método requerido para determinar el tamaño del conjunto de datos
     *
     * @return tamaño del contenedor de datos
     */
    @Override
    public int getItemCount() {
        return mReviewData.size();
    }


    /**
     * Clase ViweHolder, representa cada elemento del RecyclerView
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /** Atributos. Contenedores visuales en los que introducir los datos */
        private TextView mUsernameText;
        private TextView mDateText;
        private TextView mReviewText;
        private TextView mUUIDText;
        private TextView mReviewIDText;
        private Button mRemoveButton;


        /**
         * Constructor
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializar las vistas
            mUsernameText = itemView.findViewById(R.id.reviewUsername);
            mDateText = itemView.findViewById(R.id.reviewPublicationDate);
            mReviewText = itemView.findViewById(R.id.reviewText);
            mUUIDText = itemView.findViewById(R.id.UUID);
            mReviewIDText = itemView.findViewById(R.id.reviewID);
            mRemoveButton = itemView.findViewById(R.id.deleteReviewButton);


        }


        /**
         *
         * @param review
         */
        public void bindTo(Review review) {
            if (review != null) {
                // Rellenar las vistas con datos
                mUsernameText.setText(review.getUsername());
                mDateText.setText(review.getTimestamp());
                mReviewText.setText(review.getText());
                mUUIDText.setText(review.getUUID());

                // Si el comentario pertenece al usuario actual, habilitamos el botón de eliminar
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    if (user.getUid().equals(review.getUUID()))
                        mRemoveButton.setVisibility(View.VISIBLE);
                }

                final String productID = review.getProductID();
                final String reviewID = review.getReviewID();
                //
                mRemoveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Review r = mReviewData.get(getAdapterPosition());
                        FirebaseDatabase.getInstance().getReference()
                                .child("products")
                                .child(productID)
                                .child("reviews")
                                .child(reviewID)
                                .removeValue();
                    }
                });
            }
        }


    }
}
