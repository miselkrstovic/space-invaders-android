package com.github.spaceinvaders;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.spaceinvaders.engine.Engine;
import com.github.spaceinvaders.engine.EngineListener;
import com.github.spaceinvaders.engine.ScoreKeeper;
import com.github.spaceinvaders.engine.ScoreKeeperListener;
import com.jmedeisis.bugstick.Joystick;
import com.jmedeisis.bugstick.JoystickListener;

public class MainActivity extends AppCompatActivity implements EngineListener, ScoreKeeperListener {

    private TextView lblDummy1;
    private TextView lblDummy2;
    private TextView lblDummy3;
    private TextView lblHighScore;

    private View jvGradient1;

    private TextView VpLEDLabel1;
    private TextView VpLEDLabel2;

    private LinearLayout layLivesBar;
    private TextView lblPlayerLives;
    private ImageView imgPlayerLive1;
    private ImageView imgPlayerLive2;
    private ImageView imgPlayerLive3;
    private ImageView imgPlayerLive4;
    private ImageView imgPlayerLive5;

    private ConstraintLayout layStartScreen;

    private RelativeLayout layGameScreen;

    private Joystick joystick;
    private Engine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Binding views
        lblDummy1 = findViewById(R.id.lblDummy1);
        lblDummy2 = findViewById(R.id.lblDummy2);
        lblDummy3 = findViewById(R.id.lblDummy3);
        lblHighScore = findViewById(R.id.lblHighScore);

        VpLEDLabel1 = findViewById(R.id.vpLEDLabel1);
        VpLEDLabel2 = findViewById(R.id.vpLEDLabel2);

        layLivesBar = findViewById(R.id.pnlLivesBar);
        lblPlayerLives = findViewById(R.id.lblPlayerLives);
        imgPlayerLive1 = findViewById(R.id.imgPlayerLive1);
        imgPlayerLive2 = findViewById(R.id.imgPlayerLive2);
        imgPlayerLive3 = findViewById(R.id.imgPlayerLive3);
        imgPlayerLive4 = findViewById(R.id.imgPlayerLive4);
        imgPlayerLive5 = findViewById(R.id.imgPlayerLive5);

        layStartScreen = findViewById(R.id.pnlStartScreen);
        layGameScreen = findViewById(R.id.pnlGameScreen);

        joystick = findViewById(R.id.joystick);
        joystick.setJoystickListener(joystickHandler);

        engine = findViewById(R.id.pnlGameEngine);
        engine.setGameListener(this);
        engine.resetGame();

        // Force a configuration change
        Configuration fakeConfig = getResources().getConfiguration();
        onConfigurationChanged(fakeConfig);
    }

    @Override
    protected void onPause() {
        engine.pauseGame();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        engine.cleanUp();

        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        engine.processEnvironmentChange(
            newConfig.orientation,
            newConfig.screenHeightDp,
            newConfig.screenWidthDp
        );
    }

    @Override
    public void onStartGame(Engine sender) {
        layStartScreen.setVisibility(View.INVISIBLE);
        layGameScreen.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPauseGame(Engine sender) {
        layStartScreen.setVisibility(View.INVISIBLE);
        layGameScreen.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStopGame(Engine sender) {
        layStartScreen.setVisibility(View.VISIBLE);
        layGameScreen.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onShutdown(Engine sender) {
        finish();
    }

    @Override
    public void onUpdateScores(ScoreKeeper sender) {
        VpLEDLabel1.setText(engine.getScoreKeeper().formatScore(engine.getScoreKeeper().getPlayerScore1()));
        VpLEDLabel2.setText(engine.getScoreKeeper().formatScore(engine.getScoreKeeper().getPlayerScore2()));
        lblHighScore.setText(engine.getScoreKeeper().formatScore(engine.getScoreKeeper().getHighScore()));
    }

    @Override
    public void onUpdateLives(ScoreKeeper sender) {
        lblPlayerLives.setText(String.valueOf(engine.getPlayerLives()));
        imgPlayerLive1.setVisibility((engine.getPlayerLives() >= 1) ? View.VISIBLE : View.INVISIBLE);
        imgPlayerLive2.setVisibility((engine.getPlayerLives() >= 2) ? View.VISIBLE : View.INVISIBLE);
        imgPlayerLive3.setVisibility((engine.getPlayerLives() >= 3) ? View.VISIBLE : View.INVISIBLE);
        imgPlayerLive4.setVisibility((engine.getPlayerLives() >= 4) ? View.VISIBLE : View.INVISIBLE);
        imgPlayerLive5.setVisibility((engine.getPlayerLives() >= 5) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onShowMessage(String message) {
        if (message!=null && !TextUtils.isEmpty(message)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
            builder.setMessage(message.trim());
            builder.show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return engine.processKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return engine.processKeyUp(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
         return super.onTouchEvent(event);
    }

    JoystickListener joystickHandler = new JoystickListener() {
        @Override
        public void onDown() {
// NOFIX:
//                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE);
//                engine.processKeyDown(KeyEvent.ACTION_DOWN, keyEvent);
        }

        @Override
        public void onDrag(float degrees, float offset) {
            float movementThreshold = 0.2f;

            if (offset >= movementThreshold) {
                if (Math.abs(degrees) == 0) {
                    KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT);
                    engine.processKeyUp(KeyEvent.ACTION_DOWN, keyEvent, offset);
                } else if (Math.abs(degrees) == 180) {
                    KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT);
                    engine.processKeyUp(KeyEvent.ACTION_DOWN, keyEvent, offset);
                }
            }
        }

        @Override
        public void onUp() {
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SPACE);
            engine.processKeyUp(KeyEvent.ACTION_UP, keyEvent);
        }
    };

}
