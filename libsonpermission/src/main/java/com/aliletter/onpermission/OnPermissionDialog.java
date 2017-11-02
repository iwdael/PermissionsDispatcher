package com.aliletter.onpermission;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.TextView;

/**
 * Author: aliletter
 * Github: http://github.com/aliletter
 * Data: 2017/9/28.
 */

public class OnPermissionDialog extends Dialog implements View.OnClickListener, DialogInterface.OnCancelListener {
    private TextView tv_content;
    private PermissionDialogListener mListener;
    private String mPermission, mContent;

    public OnPermissionDialog(Context context, String permission, String content) {
        this(context, R.style.OnPermissionDialog);
        mPermission = permission;
        mContent = content;
    }

    public OnPermissionDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }


    public void setPermissionDialogListener(PermissionDialogListener listener) {
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_onpermission_dialog);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_confirm).setOnClickListener(this);
        tv_content = findViewById(R.id.tv_content);
        setOnCancelListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mListener == null) return;
        if (view.getId() == R.id.tv_cancel) {
            mListener.onCancel(mPermission);
            dismiss();
        } else {
            mListener.onConfirm(mPermission);
            dismiss();
        }
    }

    @Override
    public void show() {
        setCanceledOnTouchOutside(false);
        super.show();
        tv_content.setText(mContent);
    }


    @Override
    public void onCancel(DialogInterface dialogInterface) {
        if (mListener != null)
            mListener.onCancel(mPermission);
    }
}
