package org.openmrs.mobile.activities.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatImageView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.findpatientrecord.FindPatientRecordActivity;
import org.openmrs.mobile.api.FormListService;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.listeners.watcher.LoginValidatorWatcher;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ImageUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;
import org.openmrs.mobile.utilities.URLValidator;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends ACBaseFragment<LoginContract.Presenter> implements LoginContract.View {
    private TextView mForgotPass;
    private EditText mUrl;
    private EditText mUsername;
    private EditText mPassword;
    private CheckBox mShowPassword;
    private Button mLoginButton;
    private ProgressBar mSpinner;
    private Spinner mDropdownLocation;
    private AppCompatImageView mLoginSyncButton;
    private TextView mSyncStateLabel;
    private SparseArray<Bitmap> mBitmapCache;
    private ProgressBar mLocationLoadingProgressBar;

    private LoginValidatorWatcher loginValidatorWatcher;

    private static String mLastCorrectURL = "";
    private static List<Location> mLocationsList;
    final private String initialUrl = OpenMRS.getInstance().getServerUrl();
    protected OpenMRS mOpenMRS = OpenMRS.getInstance();


    public static LoginActivity newInstance() {
        return new LoginActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPresenter = new LoginPresenter(this, mOpenMRS);
        initViewFields();
        initListeners();
        if (mLastCorrectURL.equals(ApplicationConstants.EMPTY_STRING)) {
            mUrl.setText(OpenMRS.getInstance().getServerUrl());
            mLastCorrectURL = OpenMRS.getInstance().getServerUrl();
        } else {
            mUrl.setText(mLastCorrectURL);
        }
        hideURLDialog();
        // Font config
        FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));
    }

    private void initListeners() {
        mLoginSyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
                boolean syncState = prefs.getBoolean("sync", true);
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance()).edit();
                editor.putBoolean("sync", !syncState);
                editor.commit();
                setSyncButtonState(!syncState);
            }
        });

        loginValidatorWatcher = new LoginValidatorWatcher(mUrl, mUsername, mPassword, mDropdownLocation, mLoginButton);

        mUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (StringUtils.notEmpty(mUrl.getText().toString())
                        && !view.isFocused()
                        && loginValidatorWatcher.isUrlChanged()
                        || (loginValidatorWatcher.isUrlChanged() && !view.isFocused()
                        && loginValidatorWatcher.isLocationErrorOccurred())
                        || (!loginValidatorWatcher.isUrlChanged() && !view.isFocused())) {
                    /*((LoginFragment) getActivity()
                            .getSupportFragmentManager()
                            .findFragmentById(R.id.loginContentFrame))
                            .setUrl(mUrl.getText().toString());*/
                    loginValidatorWatcher.setUrlChanged(false);
                }
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.login(mUsername.getText().toString(),
                        mPassword.getText().toString(),
                        mUrl.getText().toString(),
                        initialUrl);
            }
        });

        /*mForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });

        mShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else {
                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });*/
    }

    private void initViewFields() {
        mUrl = (EditText) findViewById(R.id.loginUrlField);
        mUsername = (EditText) findViewById(R.id.loginUsernameField);
        mUsername.setText(OpenMRS.getInstance().getUsername());
        mPassword = (EditText) findViewById(R.id.loginPasswordField);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mSpinner = (ProgressBar) findViewById(R.id.loginLoading);
        mLoginSyncButton = ((AppCompatImageView) findViewById(R.id.loginSyncButton));
        mSyncStateLabel = ((TextView) findViewById(R.id.syncLabel));
        mDropdownLocation = (Spinner) findViewById(R.id.locationSpinner);
        //mForgotPass = (TextView) findViewById(R.id.forgotPass);
        mLocationLoadingProgressBar = (ProgressBar) findViewById(R.id.locationLoadingProgressBar);
    }

    @Override
    public void onResume() {
        super.onResume();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
        boolean syncState = prefs.getBoolean("sync", true);
        setSyncButtonState(syncState);
        hideUrlLoadingAnimation();

        bindDrawableResources();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawableResources();
    }

    @Override
    public void hideSoftKeys() {
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setSyncButtonState(boolean syncEnabled) {
        if (syncEnabled) {
            mSyncStateLabel.setText(getString(R.string.login_online));
        } else {
            mSyncStateLabel.setText(getString(R.string.login_offline));
        }
        mLoginSyncButton.setSelected(syncEnabled);
    }

    public void forgotPassword() {
        /*CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.forgot_dialog_title));
        bundle.setTextViewMessage(getString(R.string.forgot_dialog_message));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setLeftButtonText(getString(R.string.forgot_button_ok));
        ((LoginActivity) this.getActivity()).createAndShowDialog(bundle, ApplicationConstants.DialogTAG.LOGOUT_DIALOG_TAG);
    */
    }

    @Override
    public void showWarningDialog() {
       /* CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.warning_dialog_title));
        bundle.setTextViewMessage(getString(R.string.warning_lost_data_dialog));
        bundle.setRightButtonText(getString(R.string.dialog_button_ok));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.LOGIN);
        bundle.setLeftButtonText(getString(R.string.dialog_button_cancel));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        ((LoginActivity) this.getActivity()).createAndShowDialog(bundle, ApplicationConstants.DialogTAG.WARNING_LOST_DATA_DIALOG_TAG);
    */
    }

    @Override
    public void showLoadingAnimation() {
        mSpinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingAnimation() {
        mSpinner.setVisibility(View.GONE);
    }

    @Override
    public void showLocationLoadingAnimation() {
        mLoginButton.setEnabled(false);
        mLocationLoadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideUrlLoadingAnimation() {
        mLocationLoadingProgressBar.setVisibility(View.INVISIBLE);
        mSpinner.setVisibility(View.GONE);
    }

    @Override
    public void finishLoginActivity() {
        finish();
    }

    private void bindDrawableResources() {
        mBitmapCache = new SparseArray<>();
        ImageView openMrsLogoImage = (ImageView) findViewById(R.id.openmrsLogo);
        createImageBitmap(R.drawable.logo_openmrs, openMrsLogoImage.getLayoutParams());
        openMrsLogoImage.setImageBitmap(mBitmapCache.get(R.drawable.logo_openmrs));
    }

    private void createImageBitmap(Integer key, ViewGroup.LayoutParams layoutParams) {
        if (mBitmapCache.get(key) == null) {
            mBitmapCache.put(key, ImageUtils.decodeBitmapFromResource(getResources(), key,
                    layoutParams.width, layoutParams.height));
        }
    }

    private void unbindDrawableResources() {
        if (null != mBitmapCache) {
            for (int i = 0; i < mBitmapCache.size(); i++) {
                Bitmap bitmap = mBitmapCache.valueAt(i);
                bitmap.recycle();
            }
        }
    }

    public void initLoginForm(List<Location> locationsList, String serverURL) {
        setLocationErrorOccurred(false);
        mLastCorrectURL = serverURL;
        mUrl.setText(serverURL);
        mLocationsList = locationsList;
        List<String> items = getLocationStringList(locationsList);
        final LocationArrayAdapter adapter = new LocationArrayAdapter(this, items);
        mDropdownLocation.setAdapter(adapter);
        mLoginButton.setEnabled(false);
        mSpinner.setVisibility(View.GONE);
    }

    @Override
    public void userAuthenticated() {
        Intent intent = new Intent(mOpenMRS.getApplicationContext(), FindPatientRecordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mOpenMRS.getApplicationContext().startActivity(intent);
        mPresenter.saveLocationsToDatabase(mLocationsList, mDropdownLocation.getSelectedItem().toString());
    }

    @Override
    public void startFormListService() {
        Intent i = new Intent(this, FormListService.class);
        startService(i);
    }

    @Override
    public void showInvalidURLSnackbar(String message) {
        createSnackbar(message)
                .setAction(getResources().getString(R.string.snackbar_edit), view -> {
                    mUrl.requestFocus();
                    mUrl.selectAll();
                })
                .show();
    }

    @Override
    public void showInvalidLoginOrPasswordSnackbar() {
        String message = getResources().getString(R.string.invalid_login_or_password_message);
        createSnackbar(message)
                .setAction(getResources().getString(R.string.snackbar_edit), view -> {
                    mPassword.requestFocus();
                    mPassword.selectAll();
                })
                .show();
    }

    private Snackbar createSnackbar(String message) {
        return Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);


    }

    @Override
    public void setLocationErrorOccurred(boolean errorOccurred) {
        this.loginValidatorWatcher.setLocationErrorOccurred(errorOccurred);
        mLoginButton.setEnabled(!errorOccurred);
    }

    @Override
    public void showToast(String message, ToastUtil.ToastType toastType) {
        ToastUtil.showShortToast(this, toastType, message);
    }

    @Override
    public void showToast(int textId, ToastUtil.ToastType toastType) {
        ToastUtil.showShortToast(this, toastType, getResources().getString(textId));
    }


    private List<String> getLocationStringList(List<Location> locationList) {
        List<String> list = new ArrayList<String>();
        list.add(getString(R.string.login_location_select));
        for (int i = 0; i < locationList.size(); i++) {
            list.add(locationList.get(i).getDisplay());
        }
        return list;
    }

    public void setUrl(String url) {
        URLValidator.ValidationResult result = URLValidator.validate(url);
        if (result.isURLValid()) {
            mPresenter.loadLocations(result.getUrl());
        } else {
            showInvalidURLSnackbar("Invalid URL");
        }
    }

    public void hideURLDialog() {
        if (mLocationsList == null) {
            mPresenter.loadLocations(mLastCorrectURL);
        } else {
            initLoginForm(mLocationsList, mLastCorrectURL);
        }
    }

    public void login() {
        mPresenter.authenticateUser(mUsername.getText().toString(),
                mPassword.getText().toString(),
                mUrl.getText().toString());
    }

    public void login(boolean wipeDatabase) {
        mPresenter.authenticateUser(mUsername.getText().toString(),
                mPassword.getText().toString(),
                mUrl.getText().toString(), wipeDatabase);
    }
}
