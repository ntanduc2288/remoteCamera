package com.hkid.remotecamera.customView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hkid.remotecamera.R;

import butterknife.BindView;
import butterknife.OnClick;



public class DeleteDialogFragment extends BaseDialogFragment {

    public interface OnDeleteListener {
        void onDoDelete();
        void onCancelDelete();
    }

    @BindView(R.id.dialog_title)
    TextView tvDialogTitle;

    public OnDeleteListener mCallback;
    public String dialogTitle;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnDeleteListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnUploadListener");
        }
    }

    public static DeleteDialogFragment newInstance(String title) {
        DeleteDialogFragment f = new DeleteDialogFragment();
        f.dialogTitle = title;
        return f;
    }

    public static DeleteDialogFragment newInstance() {
        DeleteDialogFragment f = new DeleteDialogFragment();
        return f;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.dialog_delete;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        if (dialogTitle != null && dialogTitle.length() > 0){
            tvDialogTitle.setText(dialogTitle);
        }
    }
    @OnClick({R.id.btn_yes, R.id.btn_no,R.id.rlContainer})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_yes:
                if (mCallback != null) {
                    mCallback.onDoDelete();
                }
                DeleteDialogFragment.this.dismiss();
                break;
            case R.id.rlContainer:
            case R.id.btn_no:
                if (mCallback != null) {
                    mCallback.onCancelDelete();
                }
                DeleteDialogFragment.this.dismiss();
                break;
        }
    }
}
