package com.plagame.game.kassa.utils;

import static com.plagame.game.kassa.GameConfig.DEFAULT_CLICK_VOLUME;
import static com.plagame.game.kassa.GameConfig.DEFAULT_SOUND_VOLUME;
import static com.plagame.game.kassa.Resources.SOUND_CLICK_FILENAME;
import static com.plagame.game.kassa.Resources.SOUND_SPEND_MONEY_FILENAME;

import com.badlogic.gdx.audio.Sound;
import com.plagame.game.kassa.beans.User;

/**
 * Created by Дмитрий Малышев on 07.06.2025.
 * Email: dmitry.malyshev@gmail.com
 */
public class SoundUtil {

    private static final long SOUND_SAFE_DELTA = 500;

    private static long safeSoundLastTime;

    public static void playClickSound() {
        playSound(SOUND_CLICK_FILENAME, DEFAULT_CLICK_VOLUME);
    }

//    public static Long playGotMoneySound() {
//        return playSound(SOUND_MONEY_FILENAME);
//    }

    public static void playSpentMoneySound() {
        SoundUtil.playSoundSafe(SOUND_SPEND_MONEY_FILENAME);
    }

    public static void playSoundSafe(String filename) {
        playSoundSafe(filename, DEFAULT_SOUND_VOLUME);
    }

    public static void playSoundSafe(String filename, float volume) {
        if(System.currentTimeMillis() > safeSoundLastTime + SOUND_SAFE_DELTA) {
            safeSoundLastTime = System.currentTimeMillis();
            playSound(filename, volume);
        }
    }

    public static Long playSound(String filename) {
        return playSound(filename, DEFAULT_SOUND_VOLUME);
    }

    public static Long playSound(String filename, float volume) {
        if(User.get().soundOn) {
            Sound sound = AssetUtil.getSound(filename);
            if(sound != null) {
                return sound.play(volume);
            }
        }
        return null;
    }

    public static void stopSound(String filename, Long soundId) {
        if(User.get().soundOn && soundId != null) {
            Sound sound = AssetUtil.getSound(filename);
            if(sound != null) {
                sound.stop(soundId);
            }
        }
    }
}
