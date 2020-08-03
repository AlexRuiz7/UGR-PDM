package ugr.pdm.battleships;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainMenuActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;

    private ImageView mBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFirebase();
        if (mAuth.getCurrentUser() == null) {
            Intent registerIntent =  new Intent(MainMenuActivity.this, LoginActivity.class);
            startActivity(registerIntent);
        }
        else {
            setContentView(R.layout.activity_mainmenu);
            initViews();
            initListeners();
        }

    }


    /**
     *
     */
    private void initViews() {
        mBackground = findViewById(R.id.background);

        StorageReference imgRef = FirebaseStorage.getInstance().getReference(getString(R.string.background));
        Glide.with(this)
                .load(imgRef)
                .into(mBackground);
    }


    /**
     *
     */
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("es");
    }


    /**
     *
     */
    private void initListeners() {
        Button logoutButton = findViewById(R.id.logout_button);
        Button newGameButton = findViewById(R.id.new_game_button);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                recreate();
            }
        });

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}