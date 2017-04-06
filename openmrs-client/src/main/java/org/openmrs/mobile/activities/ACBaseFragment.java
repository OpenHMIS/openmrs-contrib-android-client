package org.openmrs.mobile.activities;

import org.openmrs.mobile.BasePresenterContract;
import org.openmrs.mobile.BaseView;

public class ACBaseFragment<T extends BasePresenterContract> extends ACBaseActivity implements BaseView<T> {
    protected T mPresenter;

    @Override
    public void setPresenter(T presenter) {
        mPresenter = presenter;
    }

    /*public boolean isActive() {
        return isAdded();
    }*/

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }
}
