package com.hkid.remotecamera.ui.timer;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hkid.remotecamera.R;
import com.hkid.remotecamera.ui.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 2/9/17
 */
public class TimerActivity extends BaseActivity {
    @BindView(R.id.icon)
    ImageView iconView;
    @BindView(R.id.action_button)
    FrameLayout actionButton;
    @BindView(R.id.text)
    TextView textView;
    @BindView(R.id.lnContainer)
    LinearLayout lnContainer;

    public static int currentTimer = 0;
    int[] currentTimerText = { R.string.action_timer_0, R.string.action_timer_1, R.string.action_timer_2 };
    int[] currentTimerIcon = { R.drawable.action_timer_0, R.drawable.action_timer_1, R.drawable.action_timer_2 };

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_timer;
    }

    @Override
    protected void setupViews() {
        updateCurrentTimer(currentTimer);

    }

    private void updateCurrentTimer(int timer){
        currentTimer = timer;
        setTextRes(currentTimerText[timer]);
        setIconRes(currentTimerIcon[timer]);
    }

    @OnClick({R.id.icon, R.id.action_button, R.id.text, R.id.lnContainer})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon:
            case R.id.action_button:
            case R.id.lnContainer:
                currentTimer = ( currentTimer + 1 ) % currentTimerText.length;
                updateCurrentTimer(currentTimer);
                break;
        }
    }

    public void setTextRes(int actionTextRes) {
        if (textView != null) {
            textView.setText(getResources().getText(actionTextRes));
        }
    }

    public void setIconRes(int actionIconRes) {
        if (iconView != null) {
            iconView.setImageResource(actionIconRes);
        }
    }

    public Drawable getBackground() {
        return new ColorDrawable(getResources().getColor(R.color.action_background_color));
    }

}
