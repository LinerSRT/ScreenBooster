package com.liner.screenboster.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.StringWriter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    public static boolean validate(String content, String regex){
        return Pattern.compile(regex).matcher(content).matches();
    }

    public static String getRegex(@NonNull String content, @NonNull String regex, int group){
        Matcher matcher = Pattern.compile(regex).matcher(content);
        if(matcher.find()){
            if(group > (matcher.groupCount())){
                return matcher.group(matcher.groupCount());
            } else {
                return matcher.group(group);
            }
        } else {
            return "0";
        }
    }

    public static String replace(String content, String pattern, int group, String replacement){
        Matcher matcher = Pattern.compile(pattern).matcher(content);
        while (matcher.find()){
            String fullMatch = matcher.group(0);
            content = content.replaceAll(fullMatch, Objects.requireNonNull(fullMatch).replace(matcher.group(group), replacement));
        }
        return content;
    }
}
