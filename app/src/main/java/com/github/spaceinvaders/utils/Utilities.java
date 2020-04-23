package com.github.spaceinvaders.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import androidx.annotation.ColorInt;

import com.github.spaceinvaders.MainApplication;
import com.github.spaceinvaders.compatibility.Point32;
import com.github.spaceinvaders.compatibility.Rect32;
import com.github.spaceinvaders.enums.SoundFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;

public class Utilities {

    private static final String dirDelimiter = "/";

    public static void beep() {
        try {
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
        } catch (Exception e) {
            // Gotcha!
        }
    }

    public static void playSoundW(Context context, String filename, boolean loop) throws IOException {
        AssetManager am = context.getAssets();
        AssetFileDescriptor afd = am.openFd(filename);
        MediaPlayer player = new MediaPlayer();
        player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                afd.getLength());
        player.prepareAsync();
        player.setLooping(loop);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }

        });
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                player.start();
            }
        });
    }

    public static void playWave(SoundFile soundName) {
        playWave(soundName, false);
    }

    public static void playWave(SoundFile soundName, boolean looping) {
        try {
            Context context = MainApplication.getContext();
            String filename = soundName.toString() + ".wav";
            if (looping) {
                playSoundW(context, filename, true);
            } else {
                playSoundW(context, filename, false);
            }
        } catch (Exception ex) {
            beep(); // fallback
        }
    }


    public static int spriteBottomAlign(int srcHeight, int destHeight, int offset) {
        return offset + (srcHeight - destHeight);
    }

    public static int spriteTopAlign(int SrcHeight, int DestHeight, int Offset) {
        int result = Offset - (SrcHeight - DestHeight);
        return result;
    }

    public static int spriteCenter(int SourceWidth, int DestWidth, int Offset) {
        int result = Offset + (SourceWidth - DestWidth) / 2;
        return result;
    }

    public static int spriteMiddleAlign(int SrcHeight, int DestHeight, int Offset) {
        int result = Offset + (SrcHeight - DestHeight) / 2;
        return result;
    }

    public static boolean ptInRect(Rect32 rect, Point32 point) {
        // Rectangle normalization
        rect.setTop(Math.min(rect.getTop(), rect.getBottom()));
        rect.setBottom(Math.max(rect.getTop(), rect.getBottom()));
        rect.setLeft(Math.min(rect.getLeft(), rect.getRight()));
        rect.setRight(Math.max(rect.getLeft(), rect.getRight()));

        boolean result = ((point.getX() >= rect.getLeft()) && (point.getX() <= rect.getRight())) &&
                ((point.getY() >= rect.getTop()) && (point.getY() <= rect.getBottom()));
        return result;
    }

    public static boolean pointInRect(Rect32 rect, Point32 p) {
        boolean result = (p.getX() >= rect.getLeft()) && (p.getX() <= rect.getRight()) && (p.getY() >= rect.getTop())
                && (p.getY() <= rect.getBottom());
        return result;
    }

    public static boolean overlaps(Rect32 u, Rect32 r) {
        boolean result =
                pointInRect(r, new Point32(u.getLeft(), u.getTop())) ||
                        pointInRect(r, new Point32(u.getRight(), u.getTop())) ||
                        pointInRect(r, new Point32(u.getLeft(), u.getBottom())) ||
                        pointInRect(r, new Point32(u.getRight(), u.getBottom()));

        if (!result) {
            result =
                    pointInRect(u, new Point32(r.getLeft(), r.getTop())) ||
                            pointInRect(u, new Point32(r.getRight(), r.getTop())) ||
                            pointInRect(u, new Point32(r.getLeft(), r.getBottom())) ||
                            pointInRect(u, new Point32(r.getRight(), r.getBottom()));
        }
        return result;
    }

    public static String stringOfChar(char c, int length) {
        StringBuffer outputBuffer = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            outputBuffer.append(c);
        }
        return outputBuffer.toString();
    }

    private static SecureRandom rand = new SecureRandom();

    static {
        rand.setSeed(new Date().getTime());
    }

    private static int generateRandomInteger(int min, int max) {
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static int random(int max) {
        return generateRandomInteger(0, max - 1);
    }

    public static int floorDiv(int x, int y) {
        return (int) (x / y);
    }

    public static int setAlpha(@ColorInt int col, int alpha) {
        return Color.argb(
                alpha,
                Color.red(col),
                Color.green(col),
                Color.blue(col)
        );
    }

    public static int pxToDp(float px) {
        return (int) (px * getDisplayDensity());
    }

    public static int pxToDp(int px) {
        return (int) (px * getDisplayDensity());
    }

    public static int dpToPx(int dp) {
        return (int) (dp / getDisplayDensity());
    }

    private static float getDisplayDensity() {
        float density = MainApplication.getContext().getResources().getDisplayMetrics().density;
        return density;
    }

}