package ugr.pdm.battleships;

import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import ugr.pdm.battleships.adapters.FriendRequestsAdapter;
import ugr.pdm.battleships.adapters.FriendsAdapter;
import ugr.pdm.battleships.models.Friend;

public class FriendsActivity extends AppCompatActivity {
    public static final String TAG = "FRIENDS_ACTIVITY";

    private RecyclerView mFriendsRecyclerView;
    private RecyclerView mPendingFriendsRecyclerView;
    private RecyclerView mFriendRequestsRecyclerView;
    private ArrayList<Friend> mFriends;
    private ArrayList<Friend> mPendingFriends;
    private ArrayList<Friend> mFriendRequests;
    private FriendsAdapter mFriendsAdapter;
    private FriendsAdapter mPendingFriendsAdapter;
    private FriendsAdapter mFriendRequestsAdapter;

    private DatabaseReference mDatabaseUsersRef;
    private FirebaseUser mUser;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mDatabaseUsersRef = FirebaseDatabase.getInstance().getReference("users");
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        setupViews();
        setupRecyclerView();
        loadFriends();
        loadFriendRequests();
    }

    /**
     * Inicializa y configura los contenedores de la vista
     */
    private void setupViews() {
        SearchView mSearchView = findViewById(R.id.friend_searchView);
        mFriendsRecyclerView = findViewById(R.id.friends_recyclerView);
        mPendingFriendsRecyclerView = findViewById(R.id.friends_pending_recyclerView);
        mFriendRequestsRecyclerView = findViewById(R.id.friends_requests_recyclerView);

        mSearchView.clearFocus();
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                doFirebaseSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }


    /**
     * Inicializa y configura los recycler views
     */
    private void setupRecyclerView() {
        // Amigos
        LinearLayoutManager layout = new LinearLayoutManager(FriendsActivity.this);
        layout.setOrientation(RecyclerView.VERTICAL);
        mFriendsRecyclerView.setLayoutManager(layout);
        mFriends = new ArrayList<>();
        mFriendsAdapter = new FriendsAdapter(mFriends, FriendsActivity.this);
        mFriendsRecyclerView.setAdapter(mFriendsAdapter);

        // Solicitudes de amistad pendientes
        LinearLayoutManager layout2 = new LinearLayoutManager(FriendsActivity.this);
        layout2.setOrientation(RecyclerView.VERTICAL);
        mPendingFriendsRecyclerView.setLayoutManager(layout2);
        mPendingFriends = new ArrayList<>();
        mPendingFriendsAdapter = new FriendsAdapter(mPendingFriends, FriendsActivity.this);
        mPendingFriendsRecyclerView.setAdapter(mPendingFriendsAdapter);

        // Solicitudes de amistad recibidas
        LinearLayoutManager layout3 = new LinearLayoutManager(FriendsActivity.this);
        layout3.setOrientation(RecyclerView.VERTICAL);
        mFriendRequestsRecyclerView.setLayoutManager(layout3);
        mFriendRequests = new ArrayList<>();
        mFriendRequestsAdapter = new FriendRequestsAdapter(mFriendRequests, FriendsActivity.this);
        mFriendRequestsRecyclerView.setAdapter(mFriendRequestsAdapter);
    }


    /**
     * Carga los amigos del usuario desde Firebase
     */
    private void loadFriends() {
        mDatabaseUsersRef
                .child(mUser.getUid())
                .child("friends")
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot obj : snapshot.getChildren()) {
                                    Log.e(TAG, obj.getKey() + ": " + obj.getValue());
                                    if (Objects.equals(obj.getValue(), true)) {
                                        loadFriend(obj, mFriends, mFriendsAdapter);
                                        updatePendingRequests(obj.getKey(), mPendingFriends, mPendingFriendsAdapter);
                                    } else {
                                        loadFriend(obj, mPendingFriends, mPendingFriendsAdapter);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        }
                );
    }

    private void loadFriendRequests() {
        mDatabaseUsersRef
                .child(mUser.getUid())
                .child("friendRequests")
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot obj : snapshot.getChildren()) {
                                    Log.e(TAG, obj.getKey() + ": " + obj.getValue());
                                    if (Objects.equals(obj.getValue(), true)) {
                                        Log.e(TAG, obj.getKey() + " pasa a true");
                                        loadFriend(obj, mFriends, mFriendsAdapter);
                                        updatePendingRequests(obj.getKey(), mFriendRequests, mFriendRequestsAdapter);
                                    } else {
                                        loadFriend(obj, mFriendRequests, mFriendRequestsAdapter);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        }
                );
    }


    /**
     * Carga los datos de un usuario desde Firebase, dado el registro snapshot que lo contiene.
     * Representa al usuario en un objeto de tipo Friend  y lo almacena en el vector que le
     * corresponde (array). Actualiza la interfaz.
     *
     * @param snapshot registro de FirebaseDatabase
     * @param array    vector en el que almacenar el usuario
     * @param adapter  adaptador enlzada a array. Se llama a su método de actualización
     */
    private void loadFriend(DataSnapshot snapshot, final ArrayList<Friend> array, final FriendsAdapter adapter) {
        mDatabaseUsersRef
                .child(Objects.requireNonNull(snapshot.getKey()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        array.add(Friend.buildFromSnapshot(snapshot));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    /**
     * Realiza la actualización de la interfaz cuando una petición de amistad ha sido aceptada.
     * <p>
     * Comprueba si el usuario identificado por pendingFriendID se encuentra en el vector
     * mPendingFriends, y en tal caso, lo elimina de dicho vector
     *
     * @param pendingFriendID ID del usuario a buscar
     */
    private void updatePendingRequests(String pendingFriendID, final ArrayList<Friend> array, final FriendsAdapter adapter) {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getPersonId().equals(pendingFriendID)) {
                array.remove(i);
                adapter.notifyDataSetChanged();
            }
        }
    }


    /**
     * Busca un usuario en Firebase
     *
     * @param query correo del usuario a buscar
     */
    private void doFirebaseSearch(String query) {
        mDatabaseUsersRef.orderByChild("personEmail").equalTo(query)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Friend f = null;

                        for (DataSnapshot obj : snapshot.getChildren()) {
                            f = Friend.buildFromSnapshot(obj);
                        }

                        handleFriendSearchResult(f);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    /**
     * Maneja el resultado de la búsqueda de usuario
     *
     * @param f usuario resultado de la búsqueda o null
     *          <p>
     *          TODO reemplazar hardcoded string por resources
     */
    private void handleFriendSearchResult(final Friend f) {
//        Log.e(TAG, f.toString());
        if (f == null) {
            String info_msg = "Usuario no encontrado";
            Toast.makeText(this, info_msg, Toast.LENGTH_SHORT).show();
        } else {
            // TODO enviar mensaje a f

            // Referencia a lista de  amigos de usuario
            final DatabaseReference friendRequestsRef = mDatabaseUsersRef
                    .child(mUser.getUid())
                    .child("friends")
                    .child(f.getPersonId());

            // Referencia a solcitiudes de amistad recibidas de f
            final DatabaseReference requestedFriendRef = mDatabaseUsersRef
                    .child(f.getPersonId())
                    .child("friendRequests")
                    .child(mUser.getUid());

            // Comprobar si existe en la lista de amigos
            friendRequestsRef.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String info_msg = "";

                            if (snapshot.exists()) {
                                info_msg = f.getPersonName() + " ya está en tu lista de amigos";
                            } else {
                                friendRequestsRef.setValue(false);
                                requestedFriendRef.setValue(false);
                                info_msg = "Petición de amistad enviada a " + f.getPersonName();
                            }
                            Toast.makeText(FriendsActivity.this, info_msg, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    }
            );
        }
    }


    /**
     * UTILIDAD
     * <p>
     * Inicializa un array de tipo amigos con valores de prueba.
     *
     * @param array vector a rellenar
     * @param n     número de elementos a generar
     */
    private void fakeInit(ArrayList<Friend> array, int n) {
        array.clear();

        String email = "fake_email";
        String username = "Fake Username";
        Friend f;
        for (int i = 0; i < n; i++) {
            f = new Friend(username + " " + i, email + "_" + i + "@domain.com", null, null);
            array.add(f);
        }
    }
}