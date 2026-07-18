package com.plagame.game.kassa.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.plagame.game.kassa.GameApplication;

/**
 * Created by Дмитрий Малышев on 26.06.2025.
 * Email: dmitry.malyshev@gmail.com
 */
public class FontGenerator {

    final String FONT_CHARS_ALL = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzçğıiöşüñáéíóúАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZÇĞİÖŞÜ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>¡¿"; // не удалять, это используется в редакторе hiero для генерации атласов
    final String FONT_CHARS = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!$%#@|\\/?-+=()*&.;:,{}\"'<>"; // не удалять, это используется в редакторе hiero для генерации атласов
    final String DIGIT_CHARS = "0123456789-+=.,KMBTQWERYU"; // не удалять, это используется в редакторе hiero для генерации атласов

    public BitmapFont generateDefaultBitmapFont(float scale, Color color) {
        System.out.println("getDefaultFontSize() = " + getDefaultFontSize());
        int fontSize = getDefaultFontSize();
        String fontFilename = "fonts/Montserrat-SemiBold_" + fontSize + ".fnt";
        return generateBitmapFont(scale, color, fontFilename);
    }

    public BitmapFont generateHeaderBitmapFont(float scale, Color color) {
        float minSize = GameApplication.get().screenWidth;
        int fontSize = minSize <= 1080 ? 28 : 36;
        String fontFilename = "fonts/Montserrat-SemiBold_" + fontSize + ".fnt";
        return generateBitmapFont(scale, color, fontFilename);
    }

    public BitmapFont generateDialogHeaderBitmapFont(float scale, Color color) {
        float minSize = GameApplication.get().screenWidth;
        int fontSize = minSize <= 1080 ? 48 : 64;
        return generateBitmapFont(fontSize, scale, color);
    }

    public BitmapFont generateDialogButtonBitmapFont(float scale, Color color) {
        float minSize = GameApplication.get().screenWidth;
        int fontSize = minSize <= 1080 ? 36 : 48;
        return generateBitmapFont(fontSize, scale, color);
    }

    public BitmapFont generateRatingBitmapFont(float scale, Color color) {
        float minSize = GameApplication.get().screenWidth;
        int fontSize = minSize <= 1080 ? 36 : 48;
        String fontFilename = "fonts/Montserrat-SemiBold_" + fontSize + ".fnt";
        return generateBitmapFont(scale, color, fontFilename);
    }

    public BitmapFont generateVeryBigBitmapFont(float scale, Color color) {
        float minSize = GameApplication.get().screenWidth;
        int fontSize = minSize <= 1080 ? 96 : 128;
        return generateBitmapFont(fontSize, scale, color);
    }

    public BitmapFont generateSmallBitmapFont(float scale, Color color) {
        float minSize = GameApplication.get().screenWidth;
        int fontSize = minSize <= 1080 ? 28 : 36;
        return generateBitmapFont(fontSize, scale, color);
    }

    public BitmapFont generateVerySmallBitmapFont(float scale, Color color) {
        float minSize = GameApplication.get().screenWidth;
        int fontSize = minSize <= 1080 ? 24 : 28;
        String fontFilename = "fonts/Montserrat-SemiBold_" + fontSize + ".fnt";
        return generateBitmapFont(scale, color, fontFilename);
    }

    public BitmapFont generateBitmapFont(int size) {
        return generateBitmapFont(size, 1, Color.WHITE);
    }

    public BitmapFont generateBitmapFont(int size, float scale) {
        return generateBitmapFont(size, scale, Color.WHITE);
    }

    public BitmapFont generateBitmapFont(int size, Color color) {
        return generateBitmapFont(size, 1, color);
    }

    public BitmapFont generateBitmapFont(int size, float scale, Color color) {
        String fontFilename = getFontFilename(size);
        return generateBitmapFont(scale, color, fontFilename);
    }

    public BitmapFont generateBitmapFont(float scale, Color color, String fontFilename) {
        BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal(fontFilename), false);
        bitmapFont.setColor(color);
        bitmapFont.getData().setScale(scale);
        return bitmapFont;
    }

    private String getFontFilename(int size) {
        //return "fonts/Montserrat-SemiBold_" + size + ".fnt";
        System.out.println("------------------size = " + size);
        return "fonts/Simpler-Dnm_" + size + ".fnt";
    }

    private int getDefaultFontSize() {
        float minSize = GameApplication.get().screenWidth;
        System.out.println("---------------minSize = " + minSize);
        if(minSize <= 300) {
            return 14;
        } if(minSize <= 400) {
            return 18;
        } else if(minSize <= 720) {
            return 20;
        } else if(minSize <= 1080) {
            return 24;
        } else if(minSize <= 1280) {
            return 26;
        } else if(minSize <= 1600) {
            return 28;
        } else if(minSize <= 1790) {
            return 32;
        } else {
            return 36;
        }
    }
}
