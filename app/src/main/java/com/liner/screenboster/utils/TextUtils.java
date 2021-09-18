package com.liner.screenboster.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.StringWriter;
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

    public static String replace(String regex, int groupID, String content, String replacement) {
        Matcher matcher = Pattern.compile(regex).matcher(content);
        if (matcher.find()) {
            return content.replace(matcher.group(0), matcher.group(0).replace(matcher.group(groupID), replacement));
        }
        return content;
    }
}
