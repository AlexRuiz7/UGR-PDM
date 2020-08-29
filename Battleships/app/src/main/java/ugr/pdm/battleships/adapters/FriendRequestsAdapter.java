package ugr.pdm.battleships.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import ugr.pdm.battleships.R;
import ugr.pdm.battleships.models.Friend;


/**
 * Adaptor para la lista de Friendos
 */
public class FriendRequestsAdapter extends FriendsAdapter {

    /**
     * Constructor
     *
     * @param mFriendsData
     * @param mContext
     */
    public FriendRequestsAdapter(ArrayList<Friend> mFriendsData, Context mContext) {
        super(mFriendsData, mContext);
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
    public FriendRequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendRequestsAdapter.ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.friend_request_layout, parent, false));
    }

    class ViewHolder extends FriendsAdapter.ViewHolder {

        protected ImageButton mAcceptButton;

        /**
         * Constructor
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mDeleteButton = itemView.findViewById(R.id.friend_request_delete_btn);
            mAcceptButton = itemView.findViewById(R.id.friend_request_accept_btn);

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Friend f = mFriendsData.remove(getAdapterPosition());

                    // Eliminar de Firebase
                    if (mUser != null && f.getPersonId() != null) {
                        getUserFriendRequestReference(mUser.getUid(), f.getPersonId()).removeValue();
                    }

                    notifyDataSetChanged();
                }
            });

            mAcceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Friend f = mFriendsData.get(getAdapterPosition());

                    // Actualizar de Firebase
                    if (mUser != null && f.getPersonId() != null) {
                        getUserFriendReference(mUser.getUid(), f.getPersonId()).setValue(true);
                        getUserFriendReference(f.getPersonId(), mUser.getUid()).setValue(true);
                        mDeleteButton.callOnClick();
                    }
                }
            });

        }

    }
}
