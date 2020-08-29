package ugr.pdm.battleships.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ugr.pdm.battleships.R;
import ugr.pdm.battleships.models.Friend;


/**
 * Adaptor para la lista de Friendos
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    // Member variables.
    protected ArrayList<Friend> mFriendsData;
    protected Context mContext;


    /**
     * Constructor
     *
     * @param mFriendsData
     * @param mContext
     */
    public FriendsAdapter(ArrayList<Friend> mFriendsData, Context mContext) {
        this.mFriendsData = mFriendsData;
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
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendsAdapter.ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.friend_layout, parent, false));
    }


    /**
     * Método requerido para ligar los datos al viewholder
     *
     * @param holder   El viewholder que contendrá los datos
     * @param position la posición del adaptador
     */
    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, int position) {
        // Coger el Friendo actual
        Friend Friend = mFriendsData.get(position);

        // Rellenar con los datos
        holder.bindTo(Friend);
    }


    /**
     * Método requerido para determinar el tamaño del conjunto de datos
     *
     * @return tamaño del contenedor de datos
     */
    @Override
    public int getItemCount() {
        return mFriendsData.size();
    }


    /**
     * Clase ViweHolder, representa cada elemento del RecyclerView
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * Atributos. Contenedores visuales en los que introducir los datos
         */
        protected ImageView mImageView;
        protected TextView mUserDisplayName;
        protected ImageButton mDeleteButton;

        protected FirebaseUser mUser;
        protected DatabaseReference dbRef;

        /**
         * Constructor
         *
         * @param itemView elemento de vista a procesar
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializar las vistas
            mImageView = itemView.findViewById(R.id.friend_photo);
            mUserDisplayName = itemView.findViewById(R.id.friend_displayName);
            mDeleteButton = itemView.findViewById(R.id.friend_delete_btn);

            mUser = FirebaseAuth.getInstance().getCurrentUser();
            dbRef = FirebaseDatabase.getInstance().getReference("users");

            // Listeners
            if (mDeleteButton != null)
                mDeleteButton.setOnClickListener(this);
        }


        /**
         * Enlaza la vista a los datos
         *
         * @param friend objeto de tipo Friend que contiene los datos
         */
        public void bindTo(Friend friend) {
            if (friend != null) {
                // Rellenar las vistas con datos
                mUserDisplayName.setText(friend.getPersonName());

                if (friend.getPersonPhoto() != null) {
                    Glide.with(mContext)
                            .load(friend.getPersonPhoto())
                            .fitCenter()
                            .into(mImageView);
                }
            }
        }


        /**
         * Devuelve una referencia de tipo DatabaseReference de Firebase apuntando al registro
         * que representa la relación de amistad entre el usuario identificado por userId al
         * usuario identificado por friendId
         *
         * @param userId el Id del usuario actual
         * @param friendId elId del amigo
         * @return DatabaseReference apuntando al registro requerido
         */
        protected DatabaseReference getUserFriendReference(final String userId, final String friendId) {
            return dbRef
                    .child(userId)
                    .child("friends")
                    .child(friendId);
        }

        /**
         * Devuelve una referencia de tipo DatabaseReference de Firebase apuntando al registro
         * que representa la solicitud de amistad enviada por el usuario identificado por userId al
         * usuario identificado por friendId
         *
         * @param userId el Id del usuario actual
         * @param friendId elId del amigo
         * @return DatabaseReference apuntando al registro requerido
         */
        protected DatabaseReference getUserFriendRequestReference(final String userId, final String friendId) {
            return dbRef
                    .child(userId)
                    .child("friendRequests")
                    .child(friendId);
        }


        /**
         * Escuchador de eventos del botón "Borrar amigo"
         * @param view
         */
        @Override
        public void onClick(View view) {
            Friend f = mFriendsData.remove(getAdapterPosition());

            // Eliminar de Firebase
            if (mUser != null && f.getPersonId() != null) {
                getUserFriendReference(mUser.getUid(), f.getPersonId()).removeValue();
                getUserFriendReference(f.getPersonId(), mUser.getUid()).removeValue();
                getUserFriendRequestReference(f.getPersonId(), mUser.getUid()).removeValue();
            }

            notifyDataSetChanged();
        }

    }
}
