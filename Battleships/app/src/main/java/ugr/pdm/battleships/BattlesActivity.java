package ugr.pdm.battleships;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

import ugr.pdm.battleships.adapters.BattleInvitesAdapter;
import ugr.pdm.battleships.adapters.BattlesAdapter;
import ugr.pdm.battleships.models.Battle;
import ugr.pdm.battleships.utils.CustomListeners;

public class BattlesActivity extends AppCompatActivity
        implements CustomListeners.OnDataChangeListener, CustomListeners.OnBattleAcceptedListener {

    public static final String TAG = "BATTLES_ACTIVITY";

    private RecyclerView mBattlesRecyclerView;
    private RecyclerView mBattleInvitesRecyclerView;
    private RecyclerView mBattlesPlayedRecyclerView;
    private ArrayList<Battle> mBattles;
    private ArrayList<Battle> mBattlesPlayed;
    private ArrayList<Battle> mBattleInvites;
    private BattlesAdapter mBattlesAdapter;
    private BattlesAdapter mBattlesPlayedAdapter;
    private BattleInvitesAdapter mBattleInvitesAdapter;

    private DatabaseReference mDatabaseUsersRef;
    private DatabaseReference mDatabaseBattlesRef;
    private FirebaseUser mUser;

    /**
     * Método onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battles);

        mDatabaseUsersRef = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseBattlesRef = FirebaseDatabase.getInstance().getReference("battles");
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        setupViews();
        setupRecyclerView();
        loadBattles();
        loadBattleInvites();

        CustomListeners.getInstance().setOnDataChangeListener(this);
        CustomListeners.getInstance().setBattleAcceptedListener(this);
    }


    /**
     * Inicializa y configura los contenedores de la vista
     */
    private void setupViews() {
        mBattlesRecyclerView = findViewById(R.id.current_battles_recyclerView);
        mBattleInvitesRecyclerView = findViewById(R.id.battle_invites_recyclerView);
        mBattlesPlayedRecyclerView = findViewById(R.id.played_battles_recyclerView);
    }

    /**
     * Inicializa y configura los recycler views
     */
    private void setupRecyclerView() {
        // Batallas actuales
        LinearLayoutManager layout = new LinearLayoutManager(BattlesActivity.this);
        layout.setOrientation(RecyclerView.VERTICAL);
        mBattlesRecyclerView.setLayoutManager(layout);
        mBattles = new ArrayList<>();
        mBattlesAdapter = new BattlesAdapter(mBattles, BattlesActivity.this);
        mBattlesRecyclerView.setAdapter(mBattlesAdapter);

        // Batallas jugadas
        LinearLayoutManager layout2 = new LinearLayoutManager(BattlesActivity.this);
        layout2.setOrientation(RecyclerView.VERTICAL);
        mBattlesPlayedRecyclerView.setLayoutManager(layout2);
        mBattlesPlayed = new ArrayList<>();
        mBattlesPlayedAdapter = new BattlesAdapter(mBattlesPlayed, BattlesActivity.this);
        mBattlesPlayedRecyclerView.setAdapter(mBattlesPlayedAdapter);

        // Invitaciones a batallas
        LinearLayoutManager layout3 = new LinearLayoutManager(BattlesActivity.this);
        layout3.setOrientation(RecyclerView.VERTICAL);
        mBattleInvitesRecyclerView.setLayoutManager(layout3);
        mBattleInvites = new ArrayList<>();
        mBattleInvitesAdapter = new BattleInvitesAdapter(mBattleInvites, BattlesActivity.this);
        mBattleInvitesRecyclerView.setAdapter(mBattleInvitesAdapter);
    }


    /**
     * Carga las batallas del usuario desde Firebase
     */
    private void loadBattles() {
        mDatabaseUsersRef
                .child(mUser.getUid())
                .child("battles")
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                mBattles.clear();
                                mBattlesPlayed.clear();
                                for (DataSnapshot obj : snapshot.getChildren()) {
                                    loadBattle2(obj);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        }
                );
    }


    private void loadBattle2(DataSnapshot snapshot) {
        mDatabaseBattlesRef
                .child(Objects.requireNonNull(snapshot.getKey()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Battle b = snapshot.getValue(Battle.class);
                            b.setBattleID(snapshot.getKey());
                            // Si ya existe, actualizar
                            //  - Actualizar objeto
                            //  - Actualizar ViewHolder
                            //  - Si ha cambiado de sección: eliminar y añadir
                            // En otro caso, añadir a batallas (nueva)
                            int index = mBattles.indexOf(b);
                            if (index != -1 && mBattles.get(index).hasFinished() == b.hasFinished()) {
                                mBattles.set(index, b);
                            } else {
                                if (b.hasFinished()) {
                                    remove(snapshot.getKey(), mBattles, mBattlesAdapter);
                                    mBattlesPlayed.add(b);
                                } else {
                                    remove(snapshot.getKey(), mBattlesPlayed, mBattlesPlayedAdapter);
                                    mBattles.add(b);
                                }
                            }
                            mBattlesAdapter.notifyDataSetChanged();
                            mBattlesPlayedAdapter.notifyDataSetChanged();
                            updateUI();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    /**
     * Carga las invitaciones de batalla del usuario desde Firebase
     */
    private void loadBattleInvites() {
        mDatabaseUsersRef
                .child(mUser.getUid())
                .child("battleInvites")
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot obj : snapshot.getChildren()) {
                                    if (Objects.equals(obj.getValue(), true)) {
                                        loadBattle(obj, mBattles, mBattlesAdapter);
                                        remove(obj.getKey(), mBattleInvites, mBattleInvitesAdapter);
                                    } else {
                                        loadBattle(obj, mBattleInvites, mBattleInvitesAdapter);
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
     * Carga los datos de una batalla desde Firebase, dado el registro snapshot que lo contiene.
     * Representa la batalla en un objeto de tipo Battle y lo almacena en el vector que le
     * corresponde (array). Actualiza la interfaz.
     * <p>
     * TODO sacar actualización de interfaz y adapter
     *
     * @param snapshot registro de FirebaseDatabase
     * @param array    vector en el que almacenar la batalla
     * @param adapter  adaptador enlzada a array. Se llama a su método de actualización
     */
    private void loadBattle(DataSnapshot snapshot, final ArrayList<Battle> array, final BattlesAdapter adapter) {
        mDatabaseBattlesRef
                .child(Objects.requireNonNull(snapshot.getKey()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Log.e("Load battle", snapshot.getKey());
                            Battle b = snapshot.getValue(Battle.class);
                            b.setBattleID(snapshot.getKey());
                            array.add(b);
                            adapter.notifyDataSetChanged();
                            updateUI();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    /**
     * Realiza la actualización de la interfaz cuando una invitación a partida ha sido aceptada.
     * <p>
     * Comprueba si la partida identificada por battleID se encuentra en el vector array,
     * y en tal caso, lo elimina
     *
     * @param battleID ID de la batalla a buscar
     * @param array    vector en el que almacenar la batalla
     * @param adapter  adaptador enlzada a array. Se llama a su método de actualización
     */
    private void remove(String battleID, final ArrayList<Battle> array, final BattlesAdapter adapter) {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getBattleID().equals(battleID)) {
                array.remove(i);
                adapter.notifyDataSetChanged();
                updateUI();
            }
        }
    }


    /**
     * Actualiza los campos de textos estáticos de la interfaz, mostrando un mensaje indicando que
     * la sección no contiene datos.
     */
    private void updateUI() {
        TextView emptyBattlesTextView = findViewById(R.id.current_battles_empty_label);
        TextView emptyPlayedBattlesTextView = findViewById(R.id.played_battles_empty_label);
        TextView emptyBattleInvitesTextView = findViewById(R.id.battle_invites_empty_label);

        if (mBattles.isEmpty()) {
            emptyBattlesTextView.setVisibility(View.VISIBLE);
        } else {
            emptyBattlesTextView.setVisibility(View.GONE);
        }

        if (mBattlesPlayed.isEmpty()) {
            emptyPlayedBattlesTextView.setVisibility(View.VISIBLE);
        } else {
            emptyPlayedBattlesTextView.setVisibility(View.GONE);
        }

        if (mBattleInvites.isEmpty()) {
            emptyBattleInvitesTextView.setVisibility(View.VISIBLE);
        } else {
            emptyBattleInvitesTextView.setVisibility(View.GONE);
        }
    }


    /**
     * Implementación de la interfaz.
     */
    @Override
    public void onDataChanged() {
        updateUI();
    }


    /**
     * Cuando el jugador 2 acepta la invitación a partida, se le manda a colocar su flota
     *
     * @param b batalla aceptada
     */
    @Override
    public void onBattleAccepted(Battle b) {
        Log.e(TAG, b.getBattleID());
        Intent intent = new Intent(BattlesActivity.this, GameSetupActivity.class);
        intent.putExtra(TAG, b);
        startActivity(intent);
    }
}