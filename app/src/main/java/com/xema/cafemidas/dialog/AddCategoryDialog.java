package com.xema.cafemidas.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.xema.cafemidas.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddCategoryDialog extends Dialog {
    @BindView(R.id.edt_category)
    EditText edtCategory;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_register)
    TextView tvRegister;

    private OnRegisterListener listener;

    public AddCategoryDialog(@NonNull Context context) {
        super(context);
    }

    public interface OnRegisterListener {
        void onRegister(String name);
    }

    public void setListener(OnRegisterListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제

        setContentView(R.layout.dialog_add_category);
        ButterKnife.bind(this);

        tvCancel.setOnClickListener(v -> dismiss());
        tvRegister.setOnClickListener(v -> {
            if (listener != null) listener.onRegister(edtCategory.getText().toString());
            dismiss();
        });
    }
}
