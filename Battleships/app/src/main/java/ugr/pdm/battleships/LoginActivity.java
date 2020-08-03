package ugr.pdm.battleships;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private boolean mVerificationInProgress = false;
    private String mVerificationId;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private ImageView mBackground;
    private EditText mEditText;
    private TextView mWindowTitle;
    private TextView mWindowText;
    private TextView mWindowInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initListeners();
        initFirebase();

        if (mAuth.getCurrentUser() != null) {
            startGame();
        }
    }


    /**
     *
     */
    private void initViews() {
        mEditText = findViewById(R.id.registerInput);
        mWindowTitle = findViewById(R.id.registerTitle);
        mWindowText = findViewById(R.id.registerText);
        mWindowInfo = findViewById(R.id.registerInfo);
        mBackground = findViewById(R.id.backgroundLogin);


        StorageReference imgRef = FirebaseStorage.getInstance().getReference(getString(R.string.background));
        Glide.with(this)
                .load(imgRef)
                .into(mBackground);
    }

    /**
     *
     */
    private void initListeners() {

        mEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (mEditText.getInputType() == InputType.TYPE_CLASS_PHONE) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String number = mEditText.getText().toString();
                        if (validate())
                            startPhoneNumberVerification(number);
                    }
                }
                else if (mEditText.getInputType() == InputType.TYPE_CLASS_NUMBER) {
                    verifyPhoneNumberWithCode(mVerificationId, mEditText.getText().toString());
                }

                return false;
            }
        });
    }

    /**
     *
     */
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("es");

        // Inicialización de callbacks
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.e(TAG, "onVerificationCompleted -> " + phoneAuthCredential);
                mVerificationInProgress = false;
                mAuth.signInWithCredential(phoneAuthCredential);
                startGame();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e(TAG, "onVerificationFailed -> " + e.getMessage());
                mVerificationInProgress = false;
//                finish();
            }

            @Override
            public void onCodeSent(@NonNull String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Log.e(TAG, "onCodeSent -> " + verificationID);
                mVerificationInProgress = true;
                mVerificationId = verificationID;
                updateUI();
            }
        };
    }


    /**
     *
     * @return
     */
    private boolean validate() {
        String number = mEditText.getText().toString();

        if (!number.contains("+34")) {
            mEditText.setError("Falta el prefijo +34");
            return false;
        }
        if ( number.substring(3).trim().length() != 9 ) {
            mEditText.setError("El número debe contener 9 dígitos");
            return false;
        }

        return true;
    }

    /**
     *
     */
    private void updateUI() {
        mWindowTitle.setText(getString(R.string.verification_title));
        mWindowText.setText(getString(R.string.verification_label));
        mEditText.setHint(getString(R.string.verification_hint));
        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mEditText.setText("");
        mWindowInfo.setVisibility(View.GONE);
    }

    /**
     *
     */
    private void startGame() {
        Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
        startActivity(intent);
    }

    /**
     *
     * @param number
     */
    private void startPhoneNumberVerification(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,             // Número a verificar
                60,              // Timeout
                TimeUnit.SECONDS,   // Unidad del tiemout
                this,       // Activity
                mCallbacks);        // OnVerificationStateChangedCallbacks
        mVerificationInProgress = true;
        Toast.makeText(getApplicationContext(), number, Toast.LENGTH_SHORT).show();
    }


    /**
     *
     * @param verificationId
     * @param code
     */
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    /**
     *
     * @param credential
     */
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            startGame();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                mEditText.setError("Invalid code");
                            }
                        }
                    }
                });
    }

}