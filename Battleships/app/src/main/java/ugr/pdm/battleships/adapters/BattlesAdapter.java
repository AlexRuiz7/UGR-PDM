package ugr.pdm.battleships.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ugr.pdm.battleships.R;
import ugr.pdm.battleships.models.Battle;
import ugr.pdm.battleships.models.Friend;


/**
 * Adaptador para la lista de batallas
 */
public class BattlesAdapter extends RecyclerView.Adapter<BattlesAdapter.ViewHolder> {

    // Member variables.
    protected ArrayList<Battle> mBattlesData;
    protected Context mContext;


    /**
     * Constructor
     *
     * @param mBattlesData vector contenedor de datos
     * @param mContext     contexto
     */
    public BattlesAdapter(ArrayList<Battle> mBattlesData, Context mContext) {
        this.mBattlesData = mBattlesData;
        this.mContext = mContext;
    }


    /**
     * Método requerido para crear objetos viewholders
     *
     * @param parent   vista padre (ViewGroup)
     * @param viewType modo
     * @return viewholder
     */
    @NonNull
    @Override
    public BattlesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BattlesAdapter.ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.battle_layout, parent, false));
    }


    /**
     * Método requerido para ligar los datos al viewholder
     *
     * @param holder   El viewholder que contendrá los datos
     * @param position la posición del adaptador
     */
    @Override
    public void onBindViewHolder(@NonNull BattlesAdapter.ViewHolder holder, int position) {
        // Coger el dato actual
        Battle battle = mBattlesData.get(position);

        // Rellenar con los datos
        holder.bindTo(battle);
    }


    /**
     * Método requerido para determinar el tamaño del conjunto de datos
     *
     * @return tamaño del contenedor de datos
     */
    @Override
    public int getItemCount() {
        return mBattlesData.size();
    }


    /**
     * Clase ViweHolder, representa cada elemento del RecyclerView
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Atributos. Contenedores visuales en los que introducir los datos
         */
        protected ImageView mPlayerOneImageView;
        protected TextView mPlayerOneDisplayName;
        private ImageView mPlayerTwoImageView;
        private TextView mPlayerTwoDisplayName;

        protected FirebaseUser mUser;
        protected DatabaseReference usersRef;

        private Friend mPlayerOne, mPlayerTwo;

        /**
         * Constructor
         *
         * @param itemView elemento de vista a procesar
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializar las vistas
            mPlayerOneImageView = itemView.findViewById(R.id.player_one_photo);
            mPlayerOneDisplayName = itemView.findViewById(R.id.player_one_displayName);
            mPlayerTwoImageView = itemView.findViewById(R.id.player_two_photo);
            mPlayerTwoDisplayName = itemView.findViewById(R.id.player_two_displayName);

            mUser = FirebaseAuth.getInstance().getCurrentUser();
            usersRef = FirebaseDatabase.getInstance().getReference("users");
        }


        /**
         * Enlaza la vista a los datos
         *
         * @param b objeto de tipo Battle que contiene los datos
         */
        public void bindTo(Battle b) {
            if (b != null) {
                loadPlayerOne(b.getPlayerOneID());
                loadPlayerTwo(b.getPlayerTwoID());
            }
        }

        /**
         * Carga los datos del jugador 1 desde Firebase
         *
         * @param userId id del jugador 1 de la partida
         */
        protected void loadPlayerOne(final String userId) {
            usersRef
                    .child(userId)
                    .addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    mPlayerOne = Friend.buildFromSnapshot(snapshot);
                                    mPlayerOneDisplayName.setText(mPlayerOne.getPersonName());
                                    loadImage(mPlayerOne, mPlayerOneImageView);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            }
                    );
        }

        /**
         * Carga los datos del jugador 2 desde Firebase
         *
         * @param userId id del jugador 2 de la partida
         */
        protected void loadPlayerTwo(final String userId) {
            usersRef
                    .child(userId)
                    .addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    mPlayerTwo = Friend.buildFromSnapshot(snapshot);
                                    mPlayerTwoDisplayName.setText(mPlayerTwo.getPersonName());
                                    loadImage(mPlayerTwo, mPlayerTwoImageView);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            }
                    );
        }


        /**
         * Carga la imagen del juagdor
         *
         * @param player    jugador
         * @param container contenedor de la imagen
         */
        private void loadImage(final Friend player, final ImageView container) {
            if (player.getPersonPhoto() != null) {
                Glide.with(mContext)
                        .load(player.getPersonPhoto())
                        .fitCenter()
                        .into(container);
            }
        }
    }
}
