package ugr.pdm.droidshop.ui.account;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import ugr.pdm.droidshop.R;
import ugr.pdm.droidshop.utils.State;

public class AccountFragment extends Fragment implements OnFailureListener {
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText nameEditText;
    private TextView welcomeText;
    private String email, oldEmail;
    private String displayName, oldDisplayName;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Si el usuario no ha iniciado sesión, se le redirecciona
        boolean userIsLogged = State.getInstance().isLogged();
        if (!userIsLogged) {
            Navigation
                    .findNavController(getActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.nav_login);
        }

        View root = inflater.inflate(R.layout.fragment_account, container, false);
        mAuth = FirebaseAuth.getInstance();
        setupUI(root);
        createButtonHandlers(root);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    /**
     *
     * @param view
     */
    private void setupUI(View view) {
        emailEditText = view.findViewById(R.id.emailChangeEditText);
        passwordEditText = view.findViewById(R.id.passwordChangeEditText);
        nameEditText = view.findViewById(R.id.nameChangeEditText);
        welcomeText = view.findViewById(R.id.welcomeTextView);

        updateUI();
    }

    /**
     *
     */
    @SuppressLint("SetTextI18n")
    private void updateUI() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            email = mAuth.getCurrentUser().getEmail();;
            displayName = mAuth.getCurrentUser().getDisplayName();

            emailEditText.setText(email);
            nameEditText.setText(displayName);

            if (displayName == null)
                welcomeText.setText(getString(R.string.welcome_label) + " " + email);
            else
                welcomeText.setText(getString(R.string.welcome_label) + " " + displayName);
        }
    }

    /**
     *
     * @param root
     */
    private void createButtonHandlers(View root) {

        final Button editButton = root.findViewById(R.id.editButton);
        final Button logoutButton = root.findViewById(R.id.logoutButton);
        final Button deleteButton = root .findViewById(R.id.deleteAccountButton);
        final LinearLayout buttonsLayout = root.findViewById(R.id.linearLayoutButtons);

        // Botón de edición
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean wasEnabled = emailEditText.isEnabled();
                emailEditText.setEnabled(!wasEnabled);
                nameEditText.setEnabled(!wasEnabled);
                passwordEditText.setEnabled(!wasEnabled);

                String text = wasEnabled ? getString(R.string.edit_button) : getString(R.string.save_button);
                editButton.setText(text);

                int mode = wasEnabled ? View.VISIBLE : View.GONE;
                welcomeText.setVisibility(mode);
                buttonsLayout.setVisibility(mode);

                // Leer campos de texto
                email = emailEditText.getText().toString();
                displayName = nameEditText.getText().toString();

                // Comprobar si han sido modificados
                if (wasEnabled) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (!oldEmail.equals(email)) {
                        user.updateEmail(email)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        makeToast(getString(R.string.email_updated));
                                        updateUI();
                                    }
                                })
                                .addOnFailureListener(AccountFragment.this);

                    } else if (!oldDisplayName.equals(displayName)) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        makeToast(getString(R.string.display_name_updated));
                                        updateUI();
                                    }
                                })
                                .addOnFailureListener(AccountFragment.this);
                    }
                }

                // Actualizar
                oldEmail = email;
                oldDisplayName = displayName;
            }
        });


        // Logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseLogout();
            }
        });

        // Delete account button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.alert_delete_account_title)
                        .setMessage(R.string.alert_delete_account_msg)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAuth.getCurrentUser().delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(), "Account removed", Toast.LENGTH_SHORT).show();
                                                firebaseLogout();
                                            }
                                        })
                                        .addOnFailureListener(AccountFragment.this);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .create()
                        .show();
            }
        });
    }


    /**
     *
     */
    private void firebaseLogout() {
        mAuth.signOut();
        State.getInstance().setLogged(false);

        Navigation
                .findNavController(getActivity(), R.id.nav_host_fragment)
                .navigate(R.id.nav_home);
    }


    /*********************/
    /** UTILIDADES       */
    /*********************/

    /**
     *
     * @param msg
     */
    private void makeToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }


    /**
     *
     * @param msg
     */
    private void makeAlert(String msg) {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.auth_failed))
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        makeAlert(e.getMessage());
    }

}