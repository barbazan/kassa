package com.plagame.game.kassa.utils;

import static com.plagame.game.kassa.GameConfig.DEFAULT_CLICK_VOLUME;
import static com.plagame.game.kassa.GameConfig.DEFAULT_SOUND_VOLUME;
import static com.plagame.game.kassa.Resources.SOUND_CLICK_FILENAME;
import static com.plagame.game.kassa.Resources.SOUND_CUSTOMER_CLICK_FILENAME;
import static com.plagame.game.kassa.Resources.SOUND_KASSA_CASH_FILENAME;
import static com.plagame.game.kassa.Resources.SOUND_KASSA_CLICK_FILENAME;
import static com.plagame.game.kassa.Resources.SOUND_KASSA_COIN_FILENAME;
import static com.plagame.game.kassa.Resources.SOUND_PRODUCT_CLICK_FILENAME;
import static com.plagame.game.kassa.Resources.SOUND_TERMINAL_CLICK_FILENAME;
import static com.plagame.game.kassa.Resources.SOUND_TERMINAL_OK_CLICK_FILENAME;
import static com.plagame.game.kassa.Resources.SOUND_WRONG_CLICK_FILENAME;

import com.badlogic.gdx.audio.Sound;
import com.plagame.game.kassa.beans.User;

/**
 * Created by Дмитрий Малышев on 07.06.2025.
 * Email: dmitry.malyshev@gmail.com
 */
public class SoundUtil {

    private static final long SOUND_SAFE_DELTA = 100;

    private static long safeSoundLastTime;

    public static void playClickSound() {
        playSound(SOUND_CLICK_FILENAME, DEFAULT_CLICK_VOLUME);
    }

    public static void playCustomerClickSound() {
        SoundUtil.playSoundSafe(SOUND_CUSTOMER_CLICK_FILENAME);
    }

    public static void playKassaClickSound() {
        SoundUtil.playSoundSafe(SOUND_KASSA_CLICK_FILENAME);
    }

    public static void playKassaCashSound() {
        SoundUtil.playSoundSafe(SOUND_KASSA_CASH_FILENAME);
    }

    public static void playKassaCoinSound() {
        SoundUtil.playSoundSafe(SOUND_KASSA_COIN_FILENAME);
    }

    public static void playProductClickSound() {
        SoundUtil.playSoundSafe(SOUND_PRODUCT_CLICK_FILENAME);
    }

    public static void playTerminalClickSound() {
        SoundUtil.playSoundSafe(SOUND_TERMINAL_CLICK_FILENAME);
    }

    public static void playTerminalOkSound() {
        SoundUtil.playSoundSafe(SOUND_TERMINAL_OK_CLICK_FILENAME);
    }

    public static void playWrongClickSound() {
        SoundUtil.playSoundSafe(SOUND_WRONG_CLICK_FILENAME);
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
