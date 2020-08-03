package ugr.pdm.granadatour.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import ugr.pdm.granadatour.R;
import ugr.pdm.granadatour.utils.State;

public class AccountsFragment extends Fragment {
    private Button loginButton;
    private Button registerButton;
    private Button logoutAccountButton;
    private Button deleteAccountButton;
    private Switch darkModeSwitch;
    private TextView emailTextView;
    private TextView nameTextView;
    private TextView passwordTextView;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        setupControlButtons(root);
        setupTextViews(root);
        restoreSavedInstance();
        setupThemeSwitch();
        createButtonOnClickHandlers();

        return root;
    }

    /**
     *
     * @param root
     */
    private void setupControlButtons(View root) {
        // Referenciar componentes de control
        loginButton = root.findViewById(R.id.btn_account_login);
        registerButton = root.findViewById(R.id.btn_account_register);
        logoutAccountButton = root.findViewById(R.id.btn_account_logout);
        deleteAccountButton = root.findViewById(R.id.btn_account_delete);
        darkModeSwitch = root.findViewById(R.id.darkModeSwitch);
    }

    /**
     *
     * @param root
     */
    private void setupTextViews(View root) {
        // Referenciar campos de textp
        emailTextView = root.findViewById(R.id.editTextTextEmailAddress);
        nameTextView = root.findViewById(R.id.editTextTextPersonName);
        passwordTextView = root.findViewById(R.id.editTextTextPassword);
    }

    /**
     * Reconstruye la vista desde el estado de la aplicación
     */
    private void restoreSavedInstance() {
        darkModeSwitch.setChecked(State.getInstance().isDarkMode());
        setIsLoggedView(State.getInstance().isLogged());
    }

    /**
     *
     */
    private void setupThemeSwitch() {
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                State.getInstance().setDarkMode(b);
            }
        });
    }

    /**
     *
     */
    private void createButtonOnClickHandlers() {
        // Botón de inicio de sesión
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (emailTextView.getText().equals(""))       emailTextView.setError("Required");
                if (nameTextView.getText().equals(""))        nameTextView.setError("Required");
                if (passwordTextView.getText().equals(""))    passwordTextView.setError("Required");

                // TODO: firebase login

                setIsLoggedView(true);
            }
        });

        //
        logoutAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: firebase logout

                setIsLoggedView(false);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Account created", Toast.LENGTH_SHORT).show();
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Account deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     * @param enabled
     */
    private void setIsLoggedView(boolean enabled) {
        int goInvisible = (enabled) ? View.INVISIBLE : View.VISIBLE;
        int goVisible = (enabled) ? View.VISIBLE : View.INVISIBLE;

        emailTextView.setEnabled(!enabled);
        nameTextView.setEnabled(!enabled);
        passwordTextView.setEnabled(!enabled);

        registerButton.setEnabled(!enabled);
        registerButton.setVisibility(goInvisible);

        loginButton.setEnabled(!enabled);
        registerButton.setVisibility(goInvisible);

        logoutAccountButton.setEnabled(enabled);
        logoutAccountButton.setVisibility(goVisible);

        deleteAccountButton.setEnabled(enabled);
        deleteAccountButton.setVisibility(goVisible);

        State.getInstance().setLogged(enabled);
    }

}