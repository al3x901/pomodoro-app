package armaosfa.pomodorotimer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private Button timerButton;
    private Button projectButton;
    private RelativeLayout relativeLayoutMain;

    private Context mContext;

    private long ONE_SECOND = 1000;


    boolean isTimerRunning = false;
    boolean isBreakTimerRunning = false;
    private Map <String,?>  allprefs;
    private CountDownTimer workTimer;
    private CountDownTimer breakTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadPreferences();

        mContext = this;

        relativeLayoutMain = (RelativeLayout) findViewById(R.id.relativeLayoutMain);
        timerButton = (Button) findViewById(R.id.timerButton);
        projectButton = (Button) findViewById(R.id.projectButton);

        setUpTimers();
        setUpListeners();
    }
    @Override
    public void onResume(){
        super.onResume();
        loadPreferences();
    }

    public void setUpTimers(){

        final long workTime = Long.parseLong(allprefs.get("pref_work_length").toString());
        System.out.println(workTime);
        workTimer = new CountDownTimer(workTime, ONE_SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {

                int secs = (int) (millisUntilFinished/ONE_SECOND);
                int mins = secs / 60;
                secs = secs % 60;

                timerButton.setText("" + mins + ":" + String.format("%02d", secs));

                if(secs == 10){
                    makeNotification("Keep it moving!", 1);
                }

            }

            @Override
            public void onFinish() {
                //Handle finish
                isTimerRunning = false;
                isBreakTimerRunning = true;
                makeNotification("Break Time! ", 2);
                breakTimer.start();
            }
        };

        final long breakTime = Long.parseLong(allprefs.get("pref_break_length").toString());
        breakTimer = new CountDownTimer(breakTime, ONE_SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secs = (int) (millisUntilFinished/breakTime);
                int mins = secs / 60;
                secs = secs % 60;

                timerButton.setText("" + mins + ":" + String.format("%02d", secs));

            }

            @Override
            public void onFinish() {
                // Connect the two
                makeNotification("Time to work", 3);
                isBreakTimerRunning = false;
                timerButton.setText(R.string.timerVal);
            }
        };

    }

    public void setUpListeners() {

        relativeLayoutMain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }

            private GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    //stop timer
                    if(isTimerRunning){
                        workTimer.cancel();
                        timerButton.setText(getResources().getString(R.string.timerVal));
                        isTimerRunning = false;
                    } else if (isBreakTimerRunning){
                        breakTimer.cancel();
                        timerButton.setText(getResources().getString(R.string.timerVal));
                        isBreakTimerRunning = false;
                    }

                    return super.onDoubleTap(e);
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    //start timer
                    if (!isTimerRunning) {
                        System.out.println("SINGLE TAP CONFIRMED");
                        workTimer.start();
                        isTimerRunning = true;
                    }

                    return true;
                }



                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }
            });



        });

        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to pomodoro settings
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        projectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to pomodoro settings
                Intent intent = new Intent(MainActivity.this, ProjectManagementActivity.class);
                startActivity(intent);
            }
        });
    }

    // Set up all the settings correctly so you can read them, everytime you start a pomodoro , read in all the seetings and set everyhting up accordingly.
    //things that can vary are: the time which reminder sounds, the frequency of mid-session reminders

    public void makeNotification(String message, int id){

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(message)
                        .setContentText("Working")
                        .setSound(soundUri);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(id, mBuilder.build());

    }

    private void loadPreferences () {
        allprefs = PreferenceManager.getDefaultSharedPreferences(this).getAll();
        setUpTimers();
    }



}
