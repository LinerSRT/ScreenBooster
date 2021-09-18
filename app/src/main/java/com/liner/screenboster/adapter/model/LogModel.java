package com.liner.screenboster.adapter.model;

public class LogModel {
    private final String logTime;
    private final Type type;
    private final String text;

    public LogModel(String logTime, Type type, String text) {
        this.logTime = logTime;
        this.type = type;
        this.text = text;
    }

    public String getLogTime() {
        return logTime;
    }

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public enum Type{
        INFO,
        ERROR,
        WARN,
        DONE
    }
}
