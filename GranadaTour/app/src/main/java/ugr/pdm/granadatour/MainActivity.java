package ugr.pdm.granadatour;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.mapbox.mapboxsdk.Mapbox;

import ugr.pdm.granadatour.utils.LoadingSpinner;
import ugr.pdm.granadatour.utils.State;

public class MainActivity extends AppCompatActivity implements LoadingSpinner.OnLoadListener {

    private final State state = State.getInstance();
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavigation();
        FirebaseApp.initializeApp(this);
        setupThemeListener();
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));

        // Spinner
        spinner = (ProgressBar) findViewById(R.id.loadingSpinner);
        LoadingSpinner.getInstance().setListener(this);
        LoadingSpinner.getInstance().setLoading(false);
    }

    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home_fragment,
                R.id.navigation_routes_fragment,
                R.id.navigation_places_fragment,
                R.id.navigation_account_fragment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }


    private void setupThemeListener() {
        state.setOnDarkModeChangeListener(new State.OnDarkModeChangeListener() {
            @Override
            public void onModeChanged() {
            int nightMode = AppCompatDelegate.getDefaultNightMode();

            if (nightMode == AppCompatDelegate.MODE_NIGHT_YES)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            recreate();
            }
        });
    }

    /**
     * Spinner
     */
    @Override
    public void onLoadStart() {
        spinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadEnd() {
        spinner.setVisibility(View.GONE);
    }
}