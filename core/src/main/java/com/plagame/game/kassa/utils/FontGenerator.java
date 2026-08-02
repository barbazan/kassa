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
    private final String FONT_PREFIX = "fonts/Montserrat-SemiBold_";
    private final String FONT_TOYZ_PREFIX = "fonts/Toyz_";

    public BitmapFont generateBigToyzBitmapFont(float scale, Color color) {
        int fontSize = 48;
        String fontFilename = FONT_TOYZ_PREFIX + fontSize + ".fnt";
        return generateBitmapFont(scale, color, fontFilename);
    }

    public BitmapFont generateVeryBigToyzBitmapFont(float scale, Color color) {
        int fontSize = 64;
        String fontFilename = FONT_TOYZ_PREFIX + fontSize + ".fnt";
        return generateBitmapFont(scale, color, fontFilename);
    }


    public BitmapFont generateDefaultBitmapFont(float scale, Color color) {
        System.out.println("getDefaultFontSize() = " + getDefaultFontSize());
        int fontSize = getDefaultFontSize();
        String fontFilename = FONT_PREFIX + fontSize + ".fnt";
        return generateBitmapFont(scale, color, fontFilename);
    }

    public BitmapFont generateHeaderBitmapFont(float scale, Color color) {
        int fontSize = getHeaderFontSize();
        System.out.println("------generateHeaderBitmapFont----------fontSize = " + fontSize);
        return generateBitmapFont(scale, color, fontSize);
    }

    public BitmapFont generateBigBitmapFont(float scale, Color color) {
        int fontSize = 48;
        return generateBitmapFont(scale, color, fontSize);
    }

    public BitmapFont generateVeryBigBitmapFont(float scale, Color color) {
        int fontSize = 64;
        return generateBitmapFont(scale, color, fontSize);
    }

    public BitmapFont generateSmallBitmapFont(float scale, Color color) {
        int fontSize = 24;
        return generateBitmapFont(scale, color, fontSize);
    }

   public BitmapFont generateBitmapFont(float scale, Color color, int fontSize) {
        String fontFilename = FONT_PREFIX + fontSize + ".fnt";
        System.out.println("------generateBitmapFont-------------fontFilename = " + fontFilename);
        return generateBitmapFont(scale, color, fontFilename);
    }

    public BitmapFont generateBitmapFont(float scale, Color color, String fontFilename) {
        BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal(fontFilename), false);
        bitmapFont.setColor(color);
        bitmapFont.getData().setScale(scale);
        return bitmapFont;
    }

    private int getDefaultFontSize() {
        float minSize = GameApplication.get().screenHeight;
        System.out.println("--------FontGenerator-------screenHeight = " + minSize);
        if(minSize <= 300) {
            return 14;
        } if(minSize <= 400) {
            return 18;
        } else if(minSize <= 720) {
            return 20;
        } else if(minSize <= 1080) {
            return 28;
        } else if(minSize <= 1280) {
            return 28;
        } else if(minSize <= 1600) {
            return 32;
        } else if(minSize <= 1790) {
            return 36;
        } else {
            return 36;
        }
    }

    private int getHeaderFontSize() {
        float minSize = GameApplication.get().minScreenSize;
        System.out.println("--------getHeaderFontSize-------minScreenSize = " + minSize);
        if(minSize <= 300) {
            return 18;
        } if(minSize <= 400) {
            return 20;
        } else if(minSize <= 720) {
            return 28;
        } else if(minSize <= 1080) {
            return 48;
        } else if(minSize <= 1280) {
            return 48;
        } else if(minSize <= 1600) {
            return 64;
        } else if(minSize <= 1790) {
            return 64;
        } else {
            return 96;
        }
    }
}
