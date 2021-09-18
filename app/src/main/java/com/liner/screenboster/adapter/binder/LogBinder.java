package com.liner.screenboster.adapter.binder;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.liner.screenboster.R;
import com.liner.screenboster.adapter.Binder;
import com.liner.screenboster.adapter.model.LogModel;
import com.liner.screenboster.views.YSMarqueTextView;
import com.liner.screenboster.views.YSTextView;

public class LogBinder extends Binder<LogModel> {
    private YSTextView logTime;
    private YSTextView logType;
    private YSTextView logText;


    @Override
    public void init() {
        logTime = find(R.id.logTime);
        logType = find(R.id.logType);
        logText = find(R.id.logText);
    }

    @Override
    public void bind(@NonNull LogModel model) {
        logTime.setText(model.getLogTime());
        switch (model.getType()){
            case DONE:
                logType.setTextColor(ContextCompat.getColor(getContext(), R.color.done));
                logType.setText("DONE");
                break;
            case INFO:
                logType.setTextColor(ContextCompat.getColor(getContext(), R.color.info));
                logType.setText("INFO");
                break;
            case WARN:
                logType.setTextColor(ContextCompat.getColor(getContext(), R.color.warning));
                logType.setText("WARNING");
                break;
            case ERROR:
                logType.setTextColor(ContextCompat.getColor(getContext(), R.color.error));
                logType.setText("ERROR");
                break;
            default:
                logType.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        }
        logText.setText(model.getText());
    }

    @Override
    public int getDragDirections() {
        return 0;
    }

    @Override
    public int getSwipeDirections() {
        return 0;
    }
}
