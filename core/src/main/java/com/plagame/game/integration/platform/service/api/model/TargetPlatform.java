package com.plagame.game.integration.platform.service.api.model;

public enum TargetPlatform {
    LOCAL,
    ANDROID_GOOGLE_PLAY,
    ANDROID_RUSTORE,
    ANDROID_XSOLLA,
    HTML_YANDEX,
    ;

    public static TargetPlatform from(String value) {
        if (value == null) {
            return LOCAL;
        }
        try {
            return TargetPlatform.valueOf(value);
        } catch (IllegalArgumentException e) {
            return LOCAL;
        }
    }

    public String getShortName() {
        switch (this) {
            case LOCAL: return "L";
            case ANDROID_GOOGLE_PLAY: return "G";
            case ANDROID_RUSTORE: return "R";
            case ANDROID_XSOLLA: return "X";
            case HTML_YANDEX: return "Y";
        }
        return "_";
    }
}
