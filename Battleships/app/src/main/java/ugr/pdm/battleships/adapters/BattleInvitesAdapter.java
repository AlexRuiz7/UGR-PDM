package ugr.pdm.battleships.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import ugr.pdm.battleships.R;
import ugr.pdm.battleships.models.Battle;
import ugr.pdm.battleships.utils.CustomListeners;


/**
 * Adaptador para la lista de batallas
 */
public class BattleInvitesAdapter extends BattlesAdapter {

    /**
     * Constructor
     *
     * @param mBattlesData vector contenedor de datos
     * @param mContext     contexto
     */
    public BattleInvitesAdapter(ArrayList<Battle> mBattlesData, Context mContext) {
        super(mBattlesData, mContext);
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
    public BattleInvitesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BattleInvitesAdapter.ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.friend_request_layout, parent, false));
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
    public class ViewHolder extends BattlesAdapter.ViewHolder {

        /**
         * Atributos. Contenedores visuales en los que introducir los datos
         */
        private ImageButton mAcceptButton, mDeleteButton;


        /**
         * Constructor
         *
         * @param itemView elemento de vista a procesar
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializar las vistas
            mPlayerOneImageView = itemView.findViewById(R.id.friend_photo);
            mPlayerOneDisplayName = itemView.findViewById(R.id.friend_displayName);
            mAcceptButton = itemView.findViewById(R.id.friend_request_accept_btn);
            mDeleteButton = itemView.findViewById(R.id.friend_request_delete_btn);

            initListeners();
        }


        /**
         * Inicializa los event listeners asociados a esta vista
         */
        private void initListeners() {
            // Descartar invitación a partida
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Battle b = mBattlesData.remove(getAdapterPosition());

                    // Eliminar de Firebase
                    if (mUser != null && b.getBattleID() != null) {
                        getBattleInviteReference(mUser.getUid(), b.getBattleID()).removeValue();
                        getUserReference(b.getPlayerOneID(), b.getBattleID()).removeValue();
                        getBattleReference(b.getBattleID()).removeValue();
                    }

                    notifyDataSetChanged();
                    CustomListeners.getInstance().notifyDataSetChanged();
                }
            });

            // Aceotar invitación a partida
            mAcceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Battle b = mBattlesData.remove(getAdapterPosition());

                    // Actualizar de Firebase
                    if (mUser != null && b.getBattleID() != null) {
                        getBattleInviteReference(mUser.getUid(), b.getBattleID()).removeValue();
                        getUserReference(mUser.getUid(), b.getBattleID()).setValue(true);

                        CustomListeners.getInstance().onBattleAccepted(b);
                    }

                    notifyDataSetChanged();
                    CustomListeners.getInstance().notifyDataSetChanged();
                }
            });
        }


        /**
         * Genera y devuelve una referencia a la invitacíón a partida
         *
         * @param uid      id del usuario que recibió la invitación
         * @param battleID id de la batalla
         * @return referencia a la invitación
         */
        private DatabaseReference getBattleInviteReference(String uid, String battleID) {
            return usersRef.child(uid).child("battleInvites").child(battleID);
        }


        /**
         * Genera y devuelve una referencia al registro de partida que contiene el usuario
         *
         * @param uid      id del usuario
         * @param battleID id de la batalla
         * @return referencia al registro
         */
        private DatabaseReference getUserReference(String uid, String battleID) {
            return usersRef.child(uid).child("battles").child(battleID);
        }


        /**
         * Genera y devuelve una referencia a la partida
         *
         * @param battleID id de la partida
         * @return referencia de la partida
         */
        private DatabaseReference getBattleReference(String battleID) {
            return usersRef.getDatabase().getReference().child("battles").child(battleID);
        }


        /**
         * Enlaza la vista a los datos
         *
         * @param b objeto de tipo Battle que contiene los datos
         */
        public void bindTo(Battle b) {
            if (b != null) {
                loadPlayerOne(b.getPlayerOneID());
            }
        }

    }
}
