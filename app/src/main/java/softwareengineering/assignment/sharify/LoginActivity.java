package softwareengineering.assignment.sharify;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.UserRecoverableException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static softwareengineering.assignment.sharify.ViewProfileFragment.CLASS_NAME;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText email = null;
    private EditText password = null ;
    private AppCompatButton login_btn = null;
    private TextView signup_btn = null;
    private TextView reset_btn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean isConnected = false;
    private DatabaseReference mUserRef;
    private FirebaseDatabase mDatabase;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Runnable checkInternet = new Runnable() {
            @Override
            public void run() {
                onCheckInternetConnection(LoginActivity.this);
            }
        };
        new Thread(checkInternet).start();

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

        mDatabase = FirebaseDatabase.getInstance();
        mUserRef = mDatabase.getReference();


//        if(mAuth.getCurrentUser() != null)
//        {
//            startNewActivity();
//        }


        email = (EditText)findViewById(R.id.email);
        password =(EditText)findViewById(R.id.password);
        login_btn = (AppCompatButton)findViewById(R.id.login);
        signup_btn = (TextView)findViewById(R.id.signup);
        reset_btn = (TextView)findViewById(R.id.resetpassword);

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
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                intent.putExtra(CLASS_NAME,"LoginActivity");
                startActivity(intent);
                finish();
            }
        });
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
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
        if(isPasswordNull() || !isEmailValid() || !isConnected)
        {
            if(isPasswordNull() || !isEmailValid())
            {
                onFailedLogin();
            }
            if(!isConnected)
            {
                notifyNoConnection();
            }
        }
        else{

            login_btn.setEnabled(false);

            final ProgressDialog progDialog = new ProgressDialog(LoginActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progDialog.setIndeterminate(true);
            progDialog.setMessage("Signing in");
            progDialog.show();

            final String emailInput = email.getText().toString().trim();
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

    private boolean isEmailValid(){

        boolean isEmail = false;

        String input = email.getText().toString().trim();

        if(input.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(input).matches())
        {
            email.setError("Please enter a valid email address.");
        }
        else {
            email.setError(null);
            isEmail = true;
        }

        return isEmail;
    }

    private boolean isPasswordNull()
    {
        boolean isPasswordNull = true;

        String pass = password.getText().toString().trim();

        if(pass.isEmpty()) {
            password.setError("Please enter your password.");
        }
        else
        {
            password.setError(null);
            isPasswordNull = false;
        }
        return isPasswordNull;
    }

    public void onFailedLogin()
    {
        Toast.makeText(LoginActivity.this,"Login failed. Please try again.", Toast.LENGTH_LONG).show();
        login_btn.setEnabled(true);
    }
    public void notifyNoConnection()
    {
        Toast.makeText(LoginActivity.this,"No internet connection.", Toast.LENGTH_LONG).show();
        login_btn.setEnabled(true);
    }

    public void onSuccessfulLogin()
    {
        Toast.makeText(LoginActivity.this, "Signed in.", Toast.LENGTH_LONG).show();
        login_btn.setEnabled(true);

        //Should start a different activity after login
        startNewActivity();

    }

    public void onCheckInternetConnection(Context context)
    {
        isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if((networkInfo != null) && networkInfo.isConnected())
        {
            isConnected = true;
        }
        if(isConnected)
        {
            try{
                HttpURLConnection urlConnection = (HttpURLConnection)(new URL("http://clients3.google.com/generate_204").openConnection());
                urlConnection.setRequestProperty("User-Agent", "Android");
                urlConnection.setRequestProperty("Connection", "close");
                urlConnection.setConnectTimeout(1500);
                urlConnection.connect();
                if(urlConnection.getResponseCode() == 204 && urlConnection.getContentLength() == 0)
                {
                    isConnected = true;
                }
            }catch (IOException e)
            {
                isConnected = false;
            }
        }
    }

    public void startNewActivity()
    {

        mUserRef = mUserRef.child("Users Information").child(mAuth.getCurrentUser().getUid());
        final ProgressDialog progDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progDialog.setIndeterminate(true);
        progDialog.setMessage("Loading user data...");
        progDialog.show();
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfo = dataSnapshot.getValue(UserInfo.class);
                progDialog.dismiss();
                startViewPager();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mUserRef.addValueEventListener(userListener);






    }
    private void startViewPager()
    {
        if(userInfo!= null)
        {
            String userType = userInfo.getOrganizationType();
            if(userType.equals("Supermarket"))
            {
                Intent intent = new Intent(LoginActivity.this, SMViewPagerActivity.class);
                startActivity(intent);
                finish();
            }
            else if(userType.equals("Non-governmental Organization")){
                Intent intent = new Intent(LoginActivity.this, NGOViewPagerActivity.class);
                startActivity(intent);
                finish();
            }
        }
        else if(userInfo == null)
        {
            Intent intent = new Intent(LoginActivity.this, EditProfileActivity.class);
            intent.putExtra(CLASS_NAME,"LoginActivity");
            startActivity(intent);
            finish();
        }
    }


}
