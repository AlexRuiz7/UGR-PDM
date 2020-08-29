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
         * @param itemView
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

        protected DatabaseReference getUserFriendReference(final Friend f) {
            return dbRef
                    .child(mUser.getUid())
                    .child("friends")
                    .child(f.getPersonId());
        }

        ;

        protected DatabaseReference getUserFriendRequestReference(final Friend f) {
            return dbRef
                    .child(mUser.getUid())
                    .child("friendRequests")
                    .child(f.getPersonId());
        }

        ;

        protected DatabaseReference getOtherUserFriendRequestReference(final Friend f) {
            return dbRef
                    .child(f.getPersonId())
                    .child("friendRequests")
                    .child(mUser.getUid());
        }

        ;

        protected DatabaseReference getOtherUserFriendReference(final Friend f) {
            return dbRef
                    .child(f.getPersonId())
                    .child("friends")
                    .child(mUser.getUid());
        }

        ;


        /**
         * @param friend
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
         * @param view
         */
        @Override
        public void onClick(View view) {
            Friend f = mFriendsData.remove(getAdapterPosition());

            // Eliminar de Firebase
//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("users");
            if (mUser != null && f.getPersonId() != null) {
                getUserFriendReference(f).removeValue();
//                dbref
//                    .child(user.getUid())
//                    .child("friends")
//                    .child(f.getPersonId())
//                    .removeValue();
                getOtherUserFriendReference(f).removeValue();
//                dbref
//                    .child(f.getPersonId())
//                    .child("friends")
//                    .child(user.getUid())
//                    .removeValue();

                getOtherUserFriendRequestReference(f).removeValue();
//                dbref
//                    .child(f.getPersonId())
//                    .child("friendRequests")
//                    .child(user.getUid())
//                    .removeValue();
            }

            notifyDataSetChanged();
        }
    }
}
