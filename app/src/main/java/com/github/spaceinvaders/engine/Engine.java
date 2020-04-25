package com.github.spaceinvaders.engine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.github.spaceinvaders.GameSettings;
import com.github.spaceinvaders.R;
import com.github.spaceinvaders.compatibility.Bitmap32;
import com.github.spaceinvaders.compatibility.Canvas32;
import com.github.spaceinvaders.compatibility.Point32;
import com.github.spaceinvaders.compatibility.Tuple;
import com.github.spaceinvaders.enums.ExplosionType;
import com.github.spaceinvaders.enums.GameState;
import com.github.spaceinvaders.enums.MissileDirection;
import com.github.spaceinvaders.enums.MissileType;
import com.github.spaceinvaders.enums.SoundFile;
import com.github.spaceinvaders.enums.TranslatedKey;
import com.github.spaceinvaders.models.Explosion;
import com.github.spaceinvaders.models.Ground;
import com.github.spaceinvaders.models.LaserCannon;
import com.github.spaceinvaders.models.Missile;
import com.github.spaceinvaders.models.Model;
import com.github.spaceinvaders.models.MysteryShip;
import com.github.spaceinvaders.orchestrators.AlienOrchestrator;
import com.github.spaceinvaders.orchestrators.BunkerOrchestrator;
import com.github.spaceinvaders.orchestrators.ExplosionOrchestrator;
import com.github.spaceinvaders.orchestrators.MissileOrchestrator;
import com.github.spaceinvaders.utils.CanvasHelper;
import com.github.spaceinvaders.utils.Utilities;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.KeyEvent.KEYCODE_BUTTON_SELECT;
import static android.view.KeyEvent.KEYCODE_BUTTON_START;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_SOFT_LEFT;
import static android.view.KeyEvent.KEYCODE_SOFT_RIGHT;
import static android.view.KeyEvent.KEYCODE_SPACE;
import static com.github.spaceinvaders.enums.GameState.PAUSED;
import static com.github.spaceinvaders.enums.GameState.RUNNING;
import static com.github.spaceinvaders.enums.GameState.STOPPED;

public class Engine extends SurfaceView implements HoleListener, ScoreKeeperListener {

    public final int ALIEN_CLEARANCE = 3;

    private GameState _state = STOPPED;
    private int _playerLives;
    private Timer _mainLoop;
    private Queue<Tuple<TranslatedKey, Float>> _keyQueue = new LinkedList<>();
    private Bitmap32 _backgroundImage;
    private boolean _flipFlop;
    private int _flipFlopCounter;

    private EngineListener listener;

    private static Canvas _canvas;

    private LaserCannon _laserCannon;
    private MysteryShip _mysteryShip;
    public Ground _ground;
    public ScoreKeeper scoreKeeper;

    public AlienOrchestrator alienOrchestrator;
    public MissileOrchestrator missileOrchestrator;
    public ExplosionOrchestrator explosionOrchestrator;
    public BunkerOrchestrator bunkerOrchestrator;

    private int sleepInterval = 33; // 1 second div 30 fps

    public Engine(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public Engine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Engine(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup();
    }

    public Engine(Context context) {
        super(context);
        setup();
    }

    public void setup() {
        // Make surface view is transparent
        setZOrderOnTop(true); // necessary
        SurfaceHolder sfhTrackHolder = getHolder();
        sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);

        // Start main loop
        if (GameSettings.FPS > 0) {
            sleepInterval = 1_000 / GameSettings.FPS;
        } else {
            sleepInterval = 33; // Default: 30fps (1000ms / 30fps)
        }

        _mainLoop = new Timer();
        _mainLoop.scheduleAtFixedRate(new PaintJob(), 0, sleepInterval);

        checkEngineInitialized();
    }

    private boolean initialized = false;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setCanvas(canvas);
        _mainLoopTimer(canvas);
    }

    private void checkEngineInitialized() {
        if (!initialized) {
            // Initialize engine
            _laserCannon = LaserCannon.getInstance();
            _mysteryShip = MysteryShip.getInstance();
            missileOrchestrator = new MissileOrchestrator(this);
            explosionOrchestrator = new ExplosionOrchestrator();
            alienOrchestrator = new AlienOrchestrator(this);
            alienOrchestrator.setMissileOrchestrator(missileOrchestrator);
            alienOrchestrator.setExplosionOrchestrator(explosionOrchestrator);
            bunkerOrchestrator = new BunkerOrchestrator();
            bunkerOrchestrator.setExplosionOrchestrator(explosionOrchestrator);
            _ground = Ground.getInstance();
            scoreKeeper = ScoreKeeper.getInstance(this);

            _backgroundImage = new Bitmap32();
            _backgroundImage.loadFromFile(R.drawable.earth);

            initialized = true;
        }
    }

    private static void setCanvas(Canvas canvas) {
        _canvas = canvas;
    }

    public static Canvas getCanvas() {
        if (_canvas != null) {
            return _canvas;
        } else {
            return new Canvas32(); // TODO: Bad patch!!!
        }
    }

    public void cleanUp() {
        _mainLoop.cancel();

        _backgroundImage.free();
        _keyQueue.clear();
        _ground.free();
        scoreKeeper.free();
        explosionOrchestrator.free();
        missileOrchestrator.free();
        alienOrchestrator.free();
        bunkerOrchestrator.free();
        _mysteryShip.free();
        _laserCannon.free();
    }

    class PaintJob extends TimerTask {
        public void run() {
            mHandler.obtainMessage(1).sendToTarget();
        }
    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            invalidate();
        }
    };

    public int getPlayerLives() {
        return _playerLives;
    }

    public void _shootMissile() {
        Missile missile;

        if (missileOrchestrator.getPlayerMissileCount() < GameSettings.MAX_PLAYER_MISSILE_COUNT) {
            missile = new Missile(missileOrchestrator, explosionOrchestrator);
            missile.setLeft(_laserCannon.getLeft() + _laserCannon.getWidth() / 2);
            missile.setTop(_laserCannon.getTop());
            missileOrchestrator.addMissile(missile);
            Utilities.playWave(SoundFile.PLAYER_MISSILE_SHOOT);
        }
    }

    public void _updateFlipFlop() {
        _flipFlopCounter = _flipFlopCounter + 1;
        switch (_flipFlopCounter) {
            case 0:
            case 1:
            case 2:
            case 3:
                _flipFlop = true;
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                _flipFlop = false;
                break;
            default:
                _flipFlopCounter = 0;
        }
    }

    public void setGameListener(EngineListener listener) {
        this.listener = listener;
    }

    private void _setState(GameState state) {
        GameState prevState;
        prevState = _state;
        _state = state; // The new game state

        switch (prevState) {
            case STOPPED:
                switch (_state) {
                    case STOPPED:
                        // Do nothing
                        break;
                    case RUNNING:
                        while (_keyQueue.size() > 0) _keyQueue.poll();
                        _state = RUNNING;
                        _playerLives = GameSettings.INITIAL_PLAYER_LIVES;
                        _laserCannon.setLeft(8);
                        _ground.reset();

                        notifyStartGame();
                        notifyUpdateLives();
                        break;
                    case PAUSED:
                        // Do nothing
                        break;
                }
                break;
            case RUNNING:
                switch (_state) {
                    case STOPPED:
                        _state = STOPPED;
                        explosionOrchestrator.clear();
                        missileOrchestrator.clear();
                        scoreKeeper.clear();
                        showMessage("   Game Over   ");
                        //        _setState(gsStopped); // todo: Make this work
                        notifyStopGame();
                        break;
                    case RUNNING:
                        // Do nothing
                        break;
                    case PAUSED:
                        _state = PAUSED;
                        notifyPauseGame();
                        break;
                }
                break;
            case PAUSED:
                switch (_state) {
                    case STOPPED:
                        _state = STOPPED;
                        explosionOrchestrator.clear();
                        missileOrchestrator.clear();
                        scoreKeeper.clear();
                        notifyStopGame();
                        break;
                    case RUNNING:
                        while (_keyQueue.size() > 0) _keyQueue.poll();
                        _state = RUNNING;
                        notifyStartGame();
                        break;
                    case PAUSED:
                        // Do nothing
                        break;
                }
                break;
        }
    }

    private void notifyStartGame() {
        if (listener != null) {
            listener.onStartGame(this);
        }
    }

    private void notifyStopGame() {
        if (listener != null) {
            listener.onStopGame(this);
        }
    }

    private void notifyPauseGame() {
        if (listener != null) {
            listener.onPauseGame(this);
        }
    }

    private void notifyShutdown() {
        if (listener != null) {
            listener.onShutdown(this);
        }
    }

    private void notifyUpdateLives() {
        if (listener != null) {
            listener.onUpdateLives(scoreKeeper);
        }
    }

    private void notifyUpdateScores() {
        if (listener != null) {
            listener.onUpdateScores(scoreKeeper);
        }
    }

    private void showMessage(String msg) {
        if (listener != null) {
            listener.onShowMessage(msg);
        }
    }

    public void _processKey() {
        if (_keyQueue == null || _keyQueue.size() == 0) return;

        Tuple<TranslatedKey, Float> tuple = _keyQueue.poll();
        TranslatedKey key = tuple.x;
        float offset = tuple.y;

        switch (_state) {
            case STOPPED:
                switch (key) {
                    case LAUNCH_MISSILE:
                        startGame();
                        break;
                    case QUIT_GAME:
                        notifyShutdown();
                        break;
                }
                break;
            case RUNNING:
                if (_laserCannon.isShowing()) {
                    switch (key) {
                        case MOVE_TANK_LEFT:
                            _laserCannon.setLeft(_laserCannon.getLeft() - GameSettings.HORIZONTAL_PIXEL_SHIFT * 2 - (int) (offset * 10));
                            break;
                        case MOVE_TANK_RIGHT:
                            _laserCannon.setLeft(_laserCannon.getLeft() + GameSettings.HORIZONTAL_PIXEL_SHIFT * 2 + (int) (offset * 10));
                            break;
                        case LAUNCH_MISSILE:
                            _shootMissile();
                            break;
                        case PAUSE_GAME:
                            pauseGame();
                            break;
                        case QUIT_GAME:
                            stopGame();
                            break;
                    }

                    if (_laserCannon.getLeft() < 8) _laserCannon.setLeft(8);
                    if (_laserCannon.getLeft() > (CanvasHelper.getWidth() - 36)) {
                        _laserCannon.setLeft(CanvasHelper.getWidth() - 36);
                    }
                }
                break;
            case PAUSED:
                switch (key) {
                    case LAUNCH_MISSILE:
                    case PAUSE_GAME:
                        startGame(false);
                        break;
                    case QUIT_GAME:
                        stopGame();
                        break;
                }
                break;
        }
    }

    @Override
    public void updateHole(Object sender, int key, int shift) {
        _ground.append(key, true);
    }

    @Override
    public void onUpdateScores(ScoreKeeper sender) {
        notifyUpdateScores();
    }

    @Override
    public void onUpdateLives(ScoreKeeper sender) {
        _incPlayerLives();
    }

    private void _decPlayerLives() {
        _playerLives = _playerLives - 1;
        if (_playerLives <= 0) {
            _playerLives = 0; // Normalize stuff just in case
        }

        notifyUpdateLives();
    }

    private void _incPlayerLives() {
        _playerLives = _playerLives + 1;

        notifyUpdateLives();
    }

    private void _checkGameOver() {
        int alienCount;

        // Check if player lives is diminished
        if (_playerLives <= 0) {
            stopGame();
            return;
        }

        // Check if any aliens are available
        alienCount = 0;
        for (int x = 1; x <= GameSettings.ALIEN_MESH_WIDTH; x++) {
            for (int y = 1; y <= GameSettings.ALIEN_MESH_HEIGHT; y++) {
                if (alienOrchestrator.getMap2D(x, y) != null) {
                    if (alienOrchestrator.getMap2D(x, y).isVisible()) {
                        alienCount = alienCount + 1;
                    }
                }
            }
        }
        if (alienCount == 0) {
            startGame();
            return;
        }

        // Check if an alien reached the ground
        for (int y2 = GameSettings.ALIEN_MESH_HEIGHT; y2 >= 1; y2--) {
            for (int x2 = 1; x2 <= GameSettings.ALIEN_MESH_WIDTH; x2++) {
                if (alienOrchestrator.getMap2D(x2, y2) != null) {
                    if (alienOrchestrator.getMap2D(x2, y2).isVisible()) {
                        if (alienOrchestrator.getMap2D(x2, y2).getTop() + alienOrchestrator.getMap2D(x2, y2).getHeight() >= _laserCannon.getTop()) {
                            stopGame();
                            return;
                        }
                    }
                }
            }
        }
    }

    private void _mainLoopTimer(Canvas canvas) {
        _processKey();
        _updateFlipFlop();

        if ((_state == RUNNING) || (_state == PAUSED)) {
            if (_state == RUNNING) {
                _laserCannon.update();
                explosionOrchestrator.update();
                missileOrchestrator.update(); // UpdatePlayer
                if (_laserCannon.isShowing()) {
                    alienOrchestrator.update(); // UpdateAliens
                }
                bunkerOrchestrator.update();
                _mysteryShip.update();
            } else {
                if (_flipFlop) {
                    String str = getResources().getString(R.string.paused).toUpperCase();
                    int strWidth = CanvasHelper.getTextBounds(str).width();
                    int x = (CanvasHelper.getWidth() - strWidth) / 2;
                    int y = 0;
                    CanvasHelper.drawText(str, x, y);
                }
            }

            _laserCannon.paint();
            explosionOrchestrator.batchPaint();
            missileOrchestrator.batchPaint(); // UpdatePlayer
            alienOrchestrator.batchPaint(); // UpdateAliens
            bunkerOrchestrator.batchPaint();
            _ground.paint();
            _mysteryShip.paint();
            if (_state == RUNNING) {
                _checkCollisions();
                _checkGameOver();
            }
        }
    }

    public void _checkCollisions() {
        _checkCollisions_missile_alien();
        _checkCollisions_missile_cannon();
        _checkCollisions_missile_missile();
        _checkCollisions_missile_mysteryShip();
        _checkCollisions_missile_bunker();
        _checkCollisions_laser_missile_bunker();
    }

    private void _checkCollisions_missile_alien() {
        Explosion explosion;

        // Check missile/alien collision
        for (int x = 1; x <= GameSettings.ALIEN_MESH_WIDTH; x++) {
            for (int y = 1; y <= GameSettings.ALIEN_MESH_HEIGHT; y++) {
                for (int i = 0; i < missileOrchestrator.getPlayerObjects().size(); i++) {
                    if (missileOrchestrator.getPlayerObjects().get(i).getMissileType() == MissileType.PLAYER) {
                        if (alienOrchestrator.getMap2D(x, y) != null) {
                            if (alienOrchestrator.getMap2D(x, y).isVisible()) {
                                if (Utilities.ptInRect(new Model(
                                        alienOrchestrator.getMap2D(x, y).getLeft() + ALIEN_CLEARANCE,
                                        alienOrchestrator.getMap2D(x, y).getTop(),
                                        alienOrchestrator.getMap2D(x, y).getLeft() + alienOrchestrator.getMap2D(x, y).getWidth() - ALIEN_CLEARANCE, // The trick to allowing missiles to pass in between aliens
                                        alienOrchestrator.getMap2D(x, y).getTop() + alienOrchestrator.getMap2D(x, y).getHeight()
                                ), new Point32(missileOrchestrator.getPlayerObjects().get(i).getLeft(),
                                        missileOrchestrator.getPlayerObjects().get(i).getTop()
                                ))) {
                                    // Update score
                                    switch (alienOrchestrator.getMap2D(x, y).getSpecie()) {
                                        case ALPHA:
                                            scoreKeeper.setPlayerScore1(scoreKeeper.getPlayerScore1() + GameSettings.ALIEN_SCORE_ALPHA);
                                            alienOrchestrator.accelerate();
                                            break;
                                        case BETA:
                                            scoreKeeper.setPlayerScore1(scoreKeeper.getPlayerScore1() + GameSettings.ALIEN_SCORE_BETA);
                                            alienOrchestrator.accelerate();
                                            break;
                                        case GAMMA:
                                            scoreKeeper.setPlayerScore1(scoreKeeper.getPlayerScore1() + GameSettings.ALIEN_SCORE_GAMMA);
                                            alienOrchestrator.accelerate();
                                            break;
                                    }
                                    // Obliterate alien
                                    alienOrchestrator.getMap2D(x, y).die();
                                    explosion = new Explosion(ExplosionType.ALIEN, explosionOrchestrator);
                                    explosion.setLeft(Utilities.spriteCenter(
                                            alienOrchestrator.getMap2D(x, y).getWidth(),
                                            explosion.getWidth(),
                                            alienOrchestrator.getMap2D(x, y).getLeft()
                                    ));
                                    explosion.setTop(Utilities.spriteMiddleAlign(
                                            alienOrchestrator.getMap2D(x, y).getHeight(),
                                            explosion.getHeight(),
                                            alienOrchestrator.getMap2D(x, y).getTop())
                                    );
                                    explosionOrchestrator.addExplosion(explosion);

                                    // Recycle missile
                                    missileOrchestrator.getPlayerObjects().get(i).garbage();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void _checkCollisions_missile_cannon() {
        Explosion explosion;

        // Check missile/cannon collision
        if (_laserCannon.isShowing()) {
            for (int i = 0; i < missileOrchestrator.getAlienObjects().size(); i++) {
                if (missileOrchestrator.getAlienObjects().get(i).getMissileType() == MissileType.ALIEN) {
                    if (Utilities.ptInRect(new Model(
                            _laserCannon.getLeft(),
                            _laserCannon.getTop(),
                            _laserCannon.getLeft() + _laserCannon.getWidth(),
                            _laserCannon.getTop() + _laserCannon.getHeight()
                    ), new Point32(missileOrchestrator.getAlienObjects().get(i).getLeft(),
                            missileOrchestrator.getAlienObjects().get(i).getTop() +
                                    missileOrchestrator.getAlienObjects().get(i).getHeight()
                    ))) {
                        _laserCannon.die();

                        explosion = new Explosion(ExplosionType.PLAYER, explosionOrchestrator);
                        explosion.setLeft(Utilities.spriteCenter(
                                _laserCannon.getWidth(),
                                explosion.getWidth(),
                                _laserCannon.getLeft()
                        ));
                        explosion.setTop(Utilities.spriteBottomAlign(_laserCannon.getHeight(), explosion.getHeight(), _laserCannon.getTop()));
                        explosionOrchestrator.addExplosion(explosion);

                        _decPlayerLives();
                        // Recycle missile
                        missileOrchestrator.getAlienObjects().get(i).garbage();
                        Utilities.playWave(SoundFile.LASER_CANNON_EXPLOSION);
                    }
                }
            }
        }
    }

    private void _checkCollisions_missile_missile() {
        Explosion explosion;

        // Check missile/missile collision
        for (int i = missileOrchestrator.getAlienObjects().size() - 1; i >= 0; i--) {
            for (int j = missileOrchestrator.getPlayerObjects().size() - 1; j >= 0; j--) {
                if (Utilities.overlaps(
                        missileOrchestrator.getAlienObjects().get(i).getBoundsRect(),
                        missileOrchestrator.getPlayerObjects().get(j).getBoundsRect()
                )) {
                    explosion = new Explosion(ExplosionType.MISSILE, explosionOrchestrator);
                    explosion.setLeft(Utilities.spriteCenter(
                            missileOrchestrator.getAlienObjects().get(i).getWidth(),
                            explosion.getWidth(),
                            missileOrchestrator.getAlienObjects().get(i).getLeft()
                    ));

                    explosion.setTop(Utilities.spriteMiddleAlign(missileOrchestrator.getAlienObjects().get(i).getHeight(), explosion.getHeight(), missileOrchestrator.getAlienObjects().get(i).getTop()));
                    explosionOrchestrator.addExplosion(explosion);
                    missileOrchestrator.getAlienObjects().get(i).garbage();
                    missileOrchestrator.getPlayerObjects().get(j).garbage();
                    break;
                }
            }
        }
    }

    private void _checkCollisions_missile_mysteryShip() {
        Explosion explosion;

        // Check missile/mystery-ship collision
        if (_mysteryShip.isShowing()) {
            for (int i = missileOrchestrator.getPlayerObjects().size() - 1; i >= 0; i--) {
                if (Utilities.overlaps(
                        missileOrchestrator.getPlayerObjects().get(i).getBoundsRect(),
                        _mysteryShip.getBoundsRect()
                )) {
                    // Update score
                    scoreKeeper.setPlayerScore1(scoreKeeper.getPlayerScore1() + GameSettings.ALIEN_SCORE_MYSTERY_SHIP);

                    // Obliterate mystery ship
                    explosion = new Explosion(ExplosionType.MYSTERY_SHIP, explosionOrchestrator);
                    explosion.setLeft(Utilities.spriteCenter(
                            _mysteryShip.getWidth(),
                            explosion.getWidth(),
                            _mysteryShip.getLeft()
                    ));
                    explosion.setTop(Utilities.spriteMiddleAlign(_mysteryShip.getHeight(), explosion.getHeight(), _mysteryShip.getTop()));

                    _mysteryShip.die();
                    explosionOrchestrator.addExplosion(explosion);
                    break;
                }
            }
        }
    }

    private void _checkCollisions_missile_bunker() {
        Explosion explosion;

        // Check missile/bunker collision
        for (int i = 0; i < missileOrchestrator.getAlienObjects().size(); i++) {
            if (missileOrchestrator.getAlienObjects().get(i).getMissileType() == MissileType.ALIEN) {
                for (int j = 0; j < bunkerOrchestrator.getMap().size(); j++) {
                    if (Utilities.ptInRect(bunkerOrchestrator.getMapItem(j).getBoundsRect(), new Point32(missileOrchestrator.getAlienObjects().get(i).getLeft(),
                            missileOrchestrator.getAlienObjects().get(i).getTop() + missileOrchestrator.getAlienObjects().get(i).getHeight()
                    ))) {

                        if (!bunkerOrchestrator.getMapItem(j).isPassable(missileOrchestrator.getAlienObjects().get(i).getBoundsRect(), MissileDirection.MOVING_DOWN)) {
                            explosion = new Explosion(ExplosionType.GROUND, explosionOrchestrator);
                            explosion.setLeft(Utilities.spriteCenter(
                                    missileOrchestrator.getAlienObjects().get(i).getWidth(),
                                    explosion.getWidth(),
                                    missileOrchestrator.getAlienObjects().get(i).getLeft()
                            ));
                            explosion.setTop(CanvasHelper.getHeight() - explosion.getHeight());
                            explosionOrchestrator.addExplosion(explosion);

                            // Recycle missile
                            missileOrchestrator.getAlienObjects().get(i).garbage();

                            // NOTICE: Do not play any waves here
                        }
                    }
                }
            }
        }
    }

    private void _checkCollisions_laser_missile_bunker() {
        Explosion explosion;

        // Check laser-missile/bunker collision
        for (int i = missileOrchestrator.getPlayerObjects().size() - 1; i >= 0; i--) {
            for (int j = 1; j < bunkerOrchestrator.getMap().size(); j++) {
                if (Utilities.overlaps(
                        missileOrchestrator.getPlayerObjects().get(i).getBoundsRect(),
                        bunkerOrchestrator.getMapItem(j).getBoundsRect()
                )) {
                    if (!bunkerOrchestrator.getMapItem(j).isPassable(missileOrchestrator.getPlayerObjects().get(i).getBoundsRect(), MissileDirection.MOVING_UP)) {
                        explosion = new Explosion(ExplosionType.GROUND, explosionOrchestrator);
                        explosion.setLeft(Utilities.spriteCenter(
                                missileOrchestrator.getPlayerObjects().get(i).getWidth(),
                                explosion.getWidth(),
                                missileOrchestrator.getPlayerObjects().get(i).getLeft()
                        ));
                        explosion.setTop(bunkerOrchestrator.getMapItem(j).getTop() + bunkerOrchestrator.getMapItem(j).getHeight());
                        explosionOrchestrator.addExplosion(explosion);

                        // Recycle missile
                        missileOrchestrator.getPlayerObjects().get(i).garbage();

                        // NOTICE: Do not play any waves here
                    }
                }
            }
        }
    }

    //------------------------------------------------------------------------------------------------

    public void startGame() {
        startGame(true);
    }

    public void startGame(boolean init) {
        if (init) {
            alienOrchestrator.init();
            bunkerOrchestrator.init();
            _mysteryShip.init();
            _laserCannon.init();
            _ground.init();
        }

        _setState(RUNNING);
    }

    public void resetGame() {
        notifyStopGame();
    }

    public void stopGame() {
        _setState(STOPPED);
    }

    public void pauseGame() {
        _setState(PAUSED);
    }

    public void processEnvironmentChange(int orientation, int screenHeightDp, int screenWidthDp) {
        // TODO:
    }

    public boolean processKeyDown(int keyCode, KeyEvent event) {
// NOFIX:
//        if (_keyQueue.size() == 0) {
//            _keyQueue.add(translateKey(keyCode, event));
//            _processKey();
//        }
        return true;
    }

    public boolean processKeyUp(int keyCode, KeyEvent event) {
        return processKeyUp(keyCode, event, 0);
    }

    public boolean processKeyUp(int keyCode, KeyEvent event, float offset) {
        if (_keyQueue.size() > 0) {
            Tuple<TranslatedKey, Float> tuple = new Tuple<>(translateKey(keyCode, event), offset);

            // Checking to avoid adding a tuple that already enqueued
            if (!_keyQueue.peek().equals(tuple)) {
                _keyQueue.add(tuple);
            }
        } else {
            Tuple<TranslatedKey, Float> tuple = new Tuple<>(translateKey(keyCode, event), offset);
            _keyQueue.add(tuple);
        }

        if (_state != RUNNING) {
            _processKey();
        } else {
            // Allow keyQueue to be dequeued by main loop
        }

        return true;
    }

    private TranslatedKey translateKey(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KEYCODE_DPAD_LEFT:
            case KEYCODE_SOFT_LEFT:
                return TranslatedKey.MOVE_TANK_LEFT;

            case KEYCODE_DPAD_RIGHT:
            case KEYCODE_SOFT_RIGHT:
                return TranslatedKey.MOVE_TANK_RIGHT;

            case KEYCODE_BUTTON_SELECT:
                return TranslatedKey.PAUSE_GAME;

            case KEYCODE_BUTTON_START:
            case KEYCODE_SPACE:
                return TranslatedKey.LAUNCH_MISSILE;

            case KEYCODE_BACK:
                return TranslatedKey.QUIT_GAME;

            default:
                return TranslatedKey.UNKNOWN_KEY;
        }
    }

    public Point32 getLaserCannonPosition() {
        return new Point32(_laserCannon.getLeft(), _laserCannon.getTop());
    }

}

