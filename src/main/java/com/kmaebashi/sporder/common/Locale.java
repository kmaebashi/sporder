package com.kmaebashi.sporder.common;

public enum Locale {
    JP(1),
    EN(2);

    private final int code;

    Locale(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Locale fromCodeStr(String codeStr) {
        int code = Integer.parseInt(codeStr);
        for (Locale l : values()) {
            if (l.code == code) {
                return l;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
