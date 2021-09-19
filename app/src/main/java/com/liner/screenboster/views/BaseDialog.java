package com.liner.screenboster.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import com.liner.screenboster.R;
import com.liner.screenboster.utils.bottomsheetcore.BaseBottomSheet;
import com.liner.screenboster.utils.bottomsheetcore.BaseConfig;

import java.util.List;


@SuppressLint("ViewConstructor")
public class BaseDialog extends BaseBottomSheet {
    private FragmentActivity activity;
    private TextView dialogTitle;
    private FrameLayout dialogCustomView;
    private ProgressBar indeterminateProgress;
    private TextView dialogText;
    private TextView dialogCancel;
    private TextView dialogDone;
    private String dialogTitleText;
    private String dialogTextText;
    private String dialogDoneText;
    private String dialogCancelText;
    private View dialogView;
    private View.OnClickListener dialogCancelListener = null;
    private View.OnClickListener dialogDoneListener = null;
    private BaseDialogBuilder.Type dialogType;
    private String[] selectionList;
    private List<BaseDialogSelectionItem> selectionItemList;
    private BaseDialogSelectionListener selectionListener;
    private BaseDialogEditListener editListener;
    private int editMinCharacters;
    private String editHelpText;


    public BaseDialog(@NonNull FragmentActivity hostActivity, @NonNull BaseConfig config, BaseDialogBuilder builder) {
        super(hostActivity, config);
        this.activity = hostActivity;
        this.dialogTitleText = builder.dialogTitleText;
        this.dialogTextText = builder.dialogTextText;
        this.dialogDoneText = builder.dialogDoneText;
        this.dialogCancelText = builder.dialogCancelText;
        this.dialogView = builder.dialogView;
        this.dialogType = builder.dialogType;
        this.selectionList = builder.selectionList;
        this.selectionListener = builder.selectionListener;
        this.selectionItemList = builder.selectionItemList;
        this.editListener = builder.editListener;
        this.editMinCharacters = builder.editMinCharacters;
        this.editHelpText = builder.editHelpText;
    }

    @NonNull
    @Override
    public final View onCreateSheetContentView(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.base_dialog, this, false);
        dialogTitle = view.findViewById(R.id.baseDialogTitle);
        dialogCustomView = view.findViewById(R.id.baseDialogCustomView);
        dialogText = view.findViewById(R.id.baseDialogText);
        dialogCancel = view.findViewById(R.id.baseDialogCancel);
        dialogDone = view.findViewById(R.id.baseDialogDone);
        indeterminateProgress = view.findViewById(R.id.indeterminateProgress);
        return view;
    }

    public void showDialog() {
        updateViews();
        show(true);
    }






    public void closeDialog() {
        if(dialogCustomView.getChildCount() > 0)
            dialogCustomView.removeAllViews();
        dismiss(true);
    }

    public ProgressBar getProgressBar() {
        if (dialogType == BaseDialogBuilder.Type.PROGRESS || dialogType == BaseDialogBuilder.Type.INDETERMINATE)
            return (ProgressBar) dialogCustomView.getChildAt(0);
        else return null;
    }

    public View getDialogView() {
        return dialogCustomView.getChildAt(0);
    }

    public void setDialogCancel(String text, View.OnClickListener dialogCancelListener) {
        this.dialogCancelText = text;
        this.dialogCancelListener = dialogCancelListener;
        updateViews();
    }

    public void setDialogDone(String text, View.OnClickListener dialogDoneListener) {
        this.dialogDoneText = text;
        this.dialogDoneListener = dialogDoneListener;
        updateViews();
    }

    public void hideActionButtons() {
        dialogCancel.setVisibility(GONE);
        dialogDone.setVisibility(GONE);
    }

    public void showActionButtons() {
        if (dialogCancelListener != null)
            dialogCancel.setVisibility(VISIBLE);
        dialogDone.setVisibility(VISIBLE);
    }

    public void setDialogTitleText(String dialogTitleText) {
        this.dialogTitleText = dialogTitleText;
        updateViews();
    }

    public void setDialogTextText(String dialogTextText) {
        this.dialogTextText = dialogTextText;
        updateViews();
    }

    public void setDialogDoneText(String dialogDoneText) {
        this.dialogDoneText = dialogDoneText;
        updateViews();
    }

    public void setDialogCancelText(String dialogCancelText) {
        this.dialogCancelText = dialogCancelText;
        updateViews();
    }

    public void setDialogType(BaseDialogBuilder.Type dialogType) {
        this.dialogType = dialogType;
        updateViews();
    }

    public class SelectionAdapter extends BaseAdapter {
        public SelectionAdapter() {
        }
        @Override
        public int getCount() {
            return selectionItemList == null ? selectionList == null? 0:selectionList.length:selectionItemList.size();
        }

        @Override
        public Object getItem(int i) {
            return selectionItemList == null?selectionList[i]:selectionItemList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @NonNull
        @Override
        public View getView(final int position, View view, @NonNull ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.base_dialog_choise_layout, parent, false);
            }
            ImageView selectionIcon = view.findViewById(R.id.selectionIcon);
            TextView selectionText = view.findViewById(R.id.selectionText);
            Object item = getItem(position);
            if(item instanceof BaseDialogSelectionItem){
                selectionIcon.setImageResource(((BaseDialogSelectionItem) item).getIconResource());
                selectionText.setText(((BaseDialogSelectionItem) item).getItemName());
                view.setOnClickListener(((BaseDialogSelectionItem) item).getClickListener());
            } else if (item instanceof String){
                selectionIcon.setImageResource(R.drawable.radiobutton_unchecked);
                selectionText.setText(String.valueOf(item));
                view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        if(selectionListener != null)
                            selectionListener.onItemClick(position);
                        closeDialog();
                    }
                });
            }
            return view;
        }
    }

    public interface BaseDialogSelectionListener{
        void onItemClick(int position);
    }

    public interface BaseDialogEditListener{
        void onEditFinished(String text);
    }


    private void updateViews(){
        indeterminateProgress.setIndeterminate(true);
        indeterminateProgress.setVisibility(GONE);
        if (dialogView != null) {
            dialogCustomView.removeAllViews();
            dialogCustomView.addView(dialogView);
        }
        if (dialogCancelListener == null) {
            dialogCancel.setVisibility(GONE);
        } else {
            dialogCancel.setText(dialogCancelText);
            dialogCancel.setOnClickListener(dialogCancelListener);
        }

        dialogDone.setText(dialogDoneText);
        if (dialogDoneListener == null) {
            dialogCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss(true);
                }
            });
        } else {
            dialogDone.setOnClickListener(dialogDoneListener);
        }
        dialogText.setText(dialogTextText);
        dialogTitle.setText(dialogTitleText);
        switch (dialogType) {
            case ERROR:
                dialogTitle.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.error_icon), null, null, null);
                break;
            case INFO:
                dialogTitle.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.info_icon), null, null, null);
                break;
            case WARNING:
                dialogTitle.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.warning_icon), null, null, null);
                break;
            case QUESTION:
                dialogTitle.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.question_icon), null, null, null);
                break;
            case PROGRESS:
                ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
                progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.primarySecondaryColor), PorterDuff.Mode.SRC_IN);
                progressBar.setMax(100);
                progressBar.setProgress(0);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                progressBar.setLayoutParams(layoutParams);
                dialogCustomView.addView(progressBar);
                hideActionButtons();
                break;
            case INDETERMINATE:
                indeterminateProgress.setVisibility(VISIBLE);
                hideActionButtons();
                break;
            case SINGLE_CHOOSE:
                ListView listView = new ListView(getContext());
                SelectionAdapter selectionAdapter = new SelectionAdapter();
                listView.setAdapter(selectionAdapter);
                dialogCustomView.addView(listView);
                hideActionButtons();
                break;
        }
    }
}