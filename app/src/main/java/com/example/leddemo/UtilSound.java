package com.example.leddemo;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;

import java.util.HashMap;
import java.util.Map;


public class UtilSound {

    public static SoundPool sp;
    public static Map<Integer, Integer> suondMap;
    public static Context context;

    //init sound pool
    public static void initSoundPool(Context context) {
        UtilSound.context = context;
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
        suondMap = new HashMap<Integer, Integer>();
        suondMap.put(1, sp.load(context, R.raw.barcodebeep, 1));
        suondMap.put(2,sp.load(context,R.raw.beep,1));
        suondMap.put(3,sp.load(context,R.raw.beeps,1));
    }

    private static long time = 0;
    //play sound
    public static void play(int sound, int number) {
        if (System.currentTimeMillis() - time > 30) {
            AudioManager am = (AudioManager) UtilSound.context.getSystemService(UtilSound.context.AUDIO_SERVICE);
            float audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            float volumnRatio = audioCurrentVolume / audioMaxVolume;

            sp.play(3, 1, 1, 0, 0, 2f);//0.5-2.0 speed
            time = System.currentTimeMillis() ;
        }

    }
    private static long currentTime=0;
    private static long lastTime= SystemClock.elapsedRealtime();
    public static void voiceTips(int type, int size, boolean asyncFlag) {
        if (type == 0 && !asyncFlag) {
            for (int i = 0; i < size; i++) {
                currentTime = SystemClock.elapsedRealtime();
                if ((currentTime - lastTime) < 40) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                play(1,0);
                lastTime = currentTime;
            }
        } else {
            currentTime = SystemClock.elapsedRealtime();
            long l = currentTime - lastTime;
//            log("voiceTips", "time: " + l);
            if (l < 40) {
                return;
            }
            play(1,0);
            lastTime = currentTime;
        }
    }

}
