package softwareengineering.assignment.sharify;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.support.v7.widget.AppCompatButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static softwareengineering.assignment.sharify.ViewProfileFragment.CLASS_NAME;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailInput = null;
    private EditText passwordInput = null;
    private EditText reEnterPasswordInput = null;
    private AppCompatButton createAccount = null;
    private TextView login = null;
    private FirebaseAuth mAuth;
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        emailInput = (EditText)findViewById(R.id.signup_email);
        passwordInput =(EditText)findViewById(R.id.signup_password);
        reEnterPasswordInput = (EditText)findViewById(R.id.reEnterPassword);
        createAccount = (AppCompatButton)findViewById(R.id.createAccount);
        login = (TextView)findViewById(R.id.login_link);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void signup()
    {

        Log.d(TAG, "Sign up!");

        if(!isUserInputValid())
        {
            onSignUpFail();
        }
        else
        {
            createAccount.setEnabled(false);

            final String email = emailInput.getText().toString().trim();
            final String password = passwordInput.getText().toString();

            final ProgressDialog progDialog = new ProgressDialog(SignUpActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progDialog.setIndeterminate(true);
            progDialog.setMessage("Creating Account");
            progDialog.show();

            Runnable signUpTask = new Runnable() {
                @Override
                public void run() {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful())
                            {
                                onSignUpFail();
                                progDialog.dismiss();
                            }
                            else if (task.isSuccessful())
                            {
                                onSignUpSuccessful();
                                progDialog.dismiss();
                            }

                        }
                    });
                }
            };
            new Thread(signUpTask).start();
        }
    }

    public boolean isUserInputValid()
    {
        boolean isValid = true;

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String reEnterPassword = reEnterPasswordInput.getText().toString();

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailInput.setError("Please enter a valid email address!");
            isValid = false;
        }
        else
        {
            emailInput.setError(null);
        }
        if(!(password.equals(reEnterPassword)))
        {
            reEnterPasswordInput.setError("Passwords do not match. Please re-enter.");
            isValid = false;
        }
        else if (password.isEmpty())
        {
            passwordInput.setError("Please enter a password");
            isValid = false;
        }
        else {
            passwordInput.setError(null);
            reEnterPasswordInput.setError(null);
        }

        return isValid;
    }

    public void onSignUpFail()
    {
        Toast.makeText(getApplicationContext(), "Unable to create account, please try again!", Toast.LENGTH_LONG).show();
        createAccount.setEnabled(true);
    }
    public void onSignUpSuccessful() {

        createAccount.setEnabled(true);
        //setResult(RESULT_OK, null);

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();


        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    onSignInSuccessful();
                }
                else
                {
                    onFailedLogin();
                }
            }
        });

    }

    public void onSignInSuccessful()
    {
        Toast.makeText(getApplicationContext(), "Signed in", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SignUpActivity.this, EditProfileActivity.class);
        intent.putExtra(CLASS_NAME, "SignUpActivity");
        startActivity(intent);
        finish();
    }

    public void onFailedLogin()
    {
        Toast.makeText(getApplicationContext(),"Login failed. Please try again.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
