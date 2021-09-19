package com.liner.screenboster.views.coloredtextview;

import androidx.annotation.ColorInt;

public class Span {
    private final String key;
    private final String text;
    @ColorInt
    private final int color;
    private final Typeface typeface;
    private final int textSize;

    public Span(String key, String text, int color, String typeface, int textSize) {
        this.key = key;
        this.text = text;
        this.color = color;
        if (typeface == null)
            this.typeface = Typeface.NORMAL;
        else
            switch (typeface) {
                case "bold":
                case "BOLD":
                    this.typeface = Typeface.BOLD;
                    break;
                case "italic":
                case "ITALIC":
                    this.typeface = Typeface.ITALIC;
                    break;
                case "bold_italic":
                case "BOLD_ITALIC":
                    this.typeface = Typeface.BOLD_ITALIC;
                    break;
                default:
                    this.typeface = Typeface.NORMAL;
            }
        this.textSize = textSize;
    }


    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    public int getColor() {
        return color;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public int getTextSize() {
        return textSize;
    }

    public enum Typeface {
        NORMAL,
        BOLD,
        ITALIC,
        BOLD_ITALIC
    }

    @Override
    public String toString() {
        return "Span{" +
                "key='" + key + '\'' +
                ", text='" + text + '\'' +
                ", color=" + color +
                ", typeface=" + typeface +
                ", textSize=" + textSize +
                '}';
    }
}
