package ugr.pdm.droidshop;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ugr.pdm.droidshop.utils.State;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private String email;
    private String password;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        setupUI(root);
        createButtonHandlers(root);

        return root;
    }

    /**
     *
     * @param view
     */
    private void setupUI(View view) {
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
    }

    /**
     * Crea y añade los handlers para los botones
     * @param root
     */
    private void createButtonHandlers(View root) {

        // Botón de login
        root.findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if (notValidData())
                    setError();
                else
                    firebaseLogin(email, password);
            }
        });

        // Botón de registro
        root.findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if (notValidData())
                    setError();
                else
                    firebaseRegister(email, password);
            }
        });
    }


    /**
     * Inicio de sesión firebase
     * @param email
     * @param password
     */
    private void firebaseLogin(String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        emailEditText.clearFocus();
                        passwordEditText.clearFocus();
                        closeKeyboard();
                        State.getInstance().setLogged(true);
                        getActivity().onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeAlert(e.getMessage());
                    }
                });
    }

    /**
     * Registro Firebase
     * @param email
     * @param password
     */
    private void firebaseRegister(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        makeToast(getString(R.string.auth_success));
                        mAuth.getCurrentUser().sendEmailVerification()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        makeToast("Verification email sent");
                                    }
                                });
//                        firebaseLogin(email, password);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeAlert(e.getMessage());
                    }
                });
    }


    /*********************/
    /** UTILIDADES       */
    /*********************/

    /**
     *
     */
    private void setError() {
        if (email.equals(""))
            emailEditText.setError(getString(R.string.required_field));
        if (password.equals(""))
            passwordEditText.setError(getString(R.string.required_field));
    }

    /**
     * Comprueba que los campos de texto no están vacíos
     *
     * @return true si no hay datos
     */
    private boolean notValidData() {
        return (email.equals("") || password.equals(""));
    }

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


    /**
     * Fuerza el cierre del teclado. Útil en los cambios de vistas (especialmente en el login)
     *
     * https://stackoverflow.com/questions/1109022/close-hide-android-soft-keyboard/35200179#35200179
     */
    private void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

}