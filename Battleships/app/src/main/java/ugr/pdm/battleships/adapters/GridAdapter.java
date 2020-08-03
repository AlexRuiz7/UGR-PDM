package ugr.pdm.battleships.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ugr.pdm.battleships.models.GridCell;
import ugr.pdm.battleships.R;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private ArrayList<GridCell> mGridData;
    private Context mContext;

    /**
     * Constructor
     *
     * @param mGridData
     * @param mContext
     */
    public GridAdapter(ArrayList<GridCell> mGridData, Context mContext) {
        this.mGridData = mGridData;
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
    public GridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GridAdapter.ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.grid_cell, parent, false));
    }

    /**
     * Método requerido para ligar los datos al viewholder
     *
     * @param holder El viewholder que contendrá los datos
     * @param position la posición del adaptador
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Coger el producto actual
        GridCell cell = mGridData.get(position);

        // Rellenar con los datos
        holder.bindTo(cell);
    }

    /**
     * Método requerido para determinar el tamaño del conjunto de datos
     *
     * @return tamaño del contenedor de datos
     */
    @Override
    public int getItemCount() {
        return mGridData.size();
    }


    /**
     * Clase ViweHolder, representa cada elemento del RecyclerView
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /** Atributos. Contenedores visuales en los que introducir los datos */
        private ImageView mImageView;
        private TextView mTextView;


        /**
         * Constructor
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

//            mImageView = itemView.findViewById(R.id.cellImageView);
            mTextView = itemView.findViewById(R.id.cellTextView);
            itemView.setOnClickListener(this);
        }


        /**
         *
         * @param cell
         */
        @SuppressLint("SetTextI18n")
        public void bindTo(GridCell cell) {
            if (cell != null) {
                mTextView.setText(cell.getX() + ", " + cell.getY());
            }
        }


        /**
         *
         * @param view
         */
        @Override
        public void onClick(View view) {
            GridCell cell = mGridData.get(getAdapterPosition());
            Toast.makeText(mContext, cell.getX() + ", " + cell.getY(), Toast.LENGTH_SHORT).show();
        }
    }

}
