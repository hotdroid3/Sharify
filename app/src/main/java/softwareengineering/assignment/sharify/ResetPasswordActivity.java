package softwareengineering.assignment.sharify;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText inputEmail=null;
    private Button reset_btn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        inputEmail=(EditText)findViewById(R.id.email);
        reset_btn=(Button)findViewById(R.id.reset);

        mAuth=FirebaseAuth.getInstance();

        reset_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                reset_btn.setEnabled(false);
                String email = inputEmail.getText().toString().trim();

                if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    inputEmail.setError("Please enter a valid email address.");
                    Toast.makeText(getApplication(), "Enter your registered email.", Toast.LENGTH_SHORT).show();
                    reset_btn.setEnabled(true);
                    return;
                }

                final ProgressDialog progDialog = new ProgressDialog(ResetPasswordActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progDialog.setIndeterminate(true);
                progDialog.setMessage("Sending email...");
                progDialog.show();

                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity.this, "We have sent you instructions to reset your password :)", Toast.LENGTH_SHORT).show();
                                    progDialog.dismiss();
                                    finish();
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email.", Toast.LENGTH_SHORT).show();
                                    progDialog.dismiss();
                                    reset_btn.setEnabled(true);
                                }

                            }
                        });
            }
        });
    }
}
