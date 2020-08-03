package ugr.pdm.droidshop;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ugr.pdm.droidshop.utils.State;

public class MainActivity extends AppCompatActivity implements State.OnAuthEventListener {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private NavigationView navigationView;
    private NavController navController;


    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_orders, R.id.nav_wishlist, R.id.nav_account, R.id.nav_login)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        mAuth = FirebaseAuth.getInstance();
        State.getInstance().setOnAuthChangeListener(this);
        FirebaseUser user = mAuth.getCurrentUser();
        State.getInstance().setLogged(user != null);
    }

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.shopping_cart:
//                Toast.makeText(this, "Shopping cart", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.nav_cart);
            break;

            case R.id.action_search:

            break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     *
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    /**
     *
     */
    public void onIsLoggedChange() {
        FirebaseUser user = mAuth.getCurrentUser();
        Log.e("MAIN_AUTH", "Auth state changed: " + State.getInstance().isLogged());

        TextView textView = navigationView.getHeaderView(0).findViewById(R.id.side_nav_bar_header);
        if (user != null) {
            if (textView != null)
                textView.setText(user.getEmail());
        }
        else {
            textView.setText(getString(R.string.nav_header_title));
        }
    }

}