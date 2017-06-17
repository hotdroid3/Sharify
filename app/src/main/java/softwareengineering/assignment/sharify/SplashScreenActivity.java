package softwareengineering.assignment.sharify;

import android.app.Activity;
import android.os.Bundle;
import java.util.TimerTask;
import java.util.Timer;
import android.content.Intent;

public class SplashScreenActivity extends Activity {
    private int timerDelay=3000;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        timerTask = new TimerTask(){
            @Override
            public void run(){
                startActivity(new Intent(SplashScreenActivity.this,LoginActivity.class));
                finish();
            }
        };
        new Timer().schedule(timerTask,timerDelay);
    }


}
