package com.hkid.remotecamera.ui.home;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.wearable.Wearable;
import com.hkid.remotecamera.ui.BaseRemoteCameraPresenterImpl;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/18/17
 */
public class HomePresenterImpl extends BaseRemoteCameraPresenterImpl implements HomePresenter.Presenter {
    HomePresenter.View view;

    public HomePresenterImpl(HomePresenter.View view) {
        this.view = view;
    }

    @Override
    public void initPhoneNode(Context context) {
        getPhoneNode(context)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(node -> {

                    view.showNode("Devices connected: " + node.getDisplayName());
                }, throwable -> {
                    view.showNode("Devices not connected");
                });

    }

    private void openChannel(){
        try {
            Wearable.ChannelApi.openChannel(googleApiClient, phoneNode.getId(), "Testing").wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("HomePresenterImpl", e.getMessage());
        }
    }
}
