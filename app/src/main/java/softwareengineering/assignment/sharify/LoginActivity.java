package softwareengineering.assignment.sharify;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText email = null;
    private EditText password = null ;
    private AppCompatButton login_btn = null;
    private TextView signup_btn = null;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!= null)
                {
                    Log.d(TAG, "Signed in" + user.getUid());
                }
                else
                {
                    Log.d(TAG, "Signed out");
                }
            }
        };
        email = (EditText)findViewById(R.id.email);
        password =(EditText)findViewById(R.id.password);
        login_btn = (AppCompatButton)findViewById(R.id.login);
        signup_btn = (TextView)findViewById(R.id.signup);

        login_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                login();
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    public void login()
    {
        Log.d(TAG, "Login!");

        if(!isEmailValid())
        {
            onFailedLogin();
        }
        else{

            login_btn.setEnabled(false);

            final ProgressDialog progDialog = new ProgressDialog(LoginActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progDialog.setIndeterminate(true);
            progDialog.setMessage("Signing in");
            progDialog.show();

            final String emailInput = email.getText().toString();
            final String passwordInput = password.getText().toString();

            Runnable loginTask = new Runnable() {
                @Override
                public void run() {
                    mAuth.signInWithEmailAndPassword(emailInput,passwordInput).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                onSuccessfulLogin();
                                progDialog.dismiss();
                            }
                            else
                            {
                                onFailedLogin();
                                progDialog.dismiss();
                            }
                        }
                    });
                }
            };

            new Thread(loginTask).start();

        }

    }

    public boolean isEmailValid(){

        boolean isEmail = false;

        String input = email.getText().toString();

        if(input.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(input).matches())
        {
            email.setError("Please enter a valid email address!");
        }
        else {
            email.setError(null);
            isEmail = true;
        }

        return isEmail;
    }

    public void onFailedLogin()
    {
        Toast.makeText(getApplicationContext(),"Login failed. Please try again.", Toast.LENGTH_LONG).show();
        login_btn.setEnabled(true);
    }

    public void onSuccessfulLogin()
    {
        Toast.makeText(getApplicationContext(), "Signed in", Toast.LENGTH_LONG).show();
        login_btn.setEnabled(true);
        finish();
    }


}
