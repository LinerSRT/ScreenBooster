package com.liner.screenboster.views.coloredtextview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColoredTextView extends AppCompatTextView {

    public ColoredTextView(@NonNull Context context) {
        this(context, null);
    }

    public ColoredTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setText(processSpannableText(getText().toString()));
    }

    public void setText(String text) {
        setText(processSpannableText(text));
    }

    private Spannable processSpannableText(String text) {
        SpannableString spannableString;
        List<Span> spanList = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\|c=([#a-fA-F0-9]{7}) f=([a-zA-Z]*) s=([-0-9]*) t=([a-zA-Zа-яА-Я0-9! @#$%^&()_+-=,.\\\\\\/~`]*)\\|").matcher(text);
        while (matcher.find()) {
            spanList.add(
                    new Span(
                            matcher.group(0),
                            matcher.group(4),
                            Color.parseColor(matcher.group(1)),
                            matcher.group(2),
                            Integer.parseInt(matcher.group(3))
                    )
            );
        }
        for (Span span : spanList) {
            text = text.replace(span.getKey(), span.getText());
        }
        spannableString = new SpannableString(text);
        for (Span span : spanList) {
            int startIndex = text.indexOf(span.getText());
            int endIndex = startIndex + span.getText().length();
            spannableString.setSpan(new ForegroundColorSpan(span.getColor()), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            switch (span.getTypeface()) {
                case BOLD:
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case ITALIC:
                    spannableString.setSpan(new StyleSpan(Typeface.ITALIC), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case BOLD_ITALIC:
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                default:
                    spannableString.setSpan(new StyleSpan(Typeface.NORMAL), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (span.getTextSize() != -1)
                spannableString.setSpan(new AbsoluteSizeSpan(span.getTextSize(), true), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableString;
    }


    public static class Builder {
        private final StringBuilder spannableString;

        public Builder() {
            this.spannableString = new StringBuilder();
        }

        public Builder add(String text) {
            spannableString.append(text);
            return this;
        }

        public Builder add(String text, @ColorInt int color) {
            return add(text, color, Span.Typeface.NORMAL, -1);
        }
        public Builder add(String text, @ColorInt int color, int textSize) {
            return add(text, color, Span.Typeface.NORMAL, textSize);
        }

        public Builder add(String text, @ColorInt int color, Span.Typeface typeface, int textSize) {
            spannableString.append("|c=").append(String.format("#%06X", (0xFFFFFF & color))).append(" f=").append(typeface).append(" s=").append(textSize).append(" t=").append(text).append("|");
            return this;
        }

        public String build() {
            Log.d("TAGTAG", "build: "+spannableString.toString());
            return spannableString.toString();
        }
    }
}