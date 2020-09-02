package ugr.pdm.battleships;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class MainMenuActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    /**
     * Método onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        initFirebase();
        if (mAuth.getCurrentUser() == null) {
            Intent loginIntent = new Intent(MainMenuActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        } else {
            setContentView(R.layout.activity_mainmenu);
            initViews();
            initListeners();
        }

    }


    /**
     * Inicializa las vistas
     */
    private void initViews() {
        ImageView mBackground = findViewById(R.id.background);

        StorageReference imgRef = FirebaseStorage.getInstance().getReference(getString(R.string.background));
        Glide.with(this)
                .load(imgRef)
                .into(mBackground);
    }


    /**
     * Inicializa los atributos relativos a Firebase
     */
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("es");
    }


    /**
     * Crea y añade escuchadores de eventos a los botones de la IU
     */
    private void initListeners() {
        Button newGameButton = findViewById(R.id.new_game_button);
        Button playerBattles = findViewById(R.id.battles_button);
        Button friendsButton = findViewById(R.id.friends_button);
        Button logoutButton = findViewById(R.id.logout_button);
        Button exitButton = findViewById(R.id.exit_button);

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, GameSetupActivity.class);
                startActivity(intent);
            }
        });

        playerBattles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MainMenuActivity.this, BattlesActivity.class);
                startActivity(intent);
            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, FriendsActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Firebase sign out
                mAuth.signOut();
                // Google sign out
                mGoogleSignInClient.signOut();

                recreate();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

}