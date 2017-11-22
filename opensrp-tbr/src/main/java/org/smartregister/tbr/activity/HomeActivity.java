package org.smartregister.tbr.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.event.BaseEvent;
import org.smartregister.tbr.event.LanguageConfigurationEvent;
import org.smartregister.tbr.event.TriggerViewConfigurationSyncEvent;
import org.smartregister.tbr.event.ViewConfigurationSyncCompleteEvent;
import org.smartregister.tbr.fragment.RegisterFragment;
import org.smartregister.tbr.jsonspec.model.MainConfig;
import org.smartregister.tbr.util.Utils;

import java.util.Calendar;

/**
 * Created by ndegwamartin on 09/10/2017.
 */

public class HomeActivity extends BaseActivity {
    private static final String TAG = HomeActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.round_white_background);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null) {
            refreshView();
        }

    }

    //
    public void manualSync(View view) {
        Utils.showToast(this, "Manual Syncing ...");
        TriggerViewConfigurationSyncEvent viewConfigurationSyncEvent = new TriggerViewConfigurationSyncEvent();
        viewConfigurationSyncEvent.setManualSync(true);
        postEvent(viewConfigurationSyncEvent);
        if (view != null) {
            TextView textView = (TextView) view.getRootView().findViewById(R.id.registerLastSyncTime);
            textView.setText("Last sync: " + Utils.formatDate(Calendar.getInstance().getTime(), "MMM d H:m"));
        }
    }

    public void postEvent(BaseEvent event) {
        Utils.postEvent(event);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void refreshView() {

        String fullName = getOpenSRPContext().allSharedPreferences().getANMPreferredName(
                getOpenSRPContext().allSharedPreferences().fetchRegisteredANM());
        //set user initials
        if (fullName != null && !fullName.toString().isEmpty()) {
            TextView textView = (TextView) toolbar.findViewById(R.id.custom_toolbar_logo_text);
            textView.setText(Utils.getShortInitials(fullName));
        }
        //Set App Name
        MainConfig config = TbrApplication.getJsonSpecHelper().getMainConfiguration();
        if (config != null && config.getApplicationName() != null) {
            TextView title = (TextView) toolbar.findViewById(R.id.custom_toolbar_title);
            title.setText(config.getApplicationName());
        } else {
            Utils.showDialogMessage(this, "Error", "Missing Main Configuration on server");
        }
        try {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.registers_container, new RegisterFragment())
                    .commit();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void refreshViewFromConfigurationChange(ViewConfigurationSyncCompleteEvent syncCompleteEvent) {
        if (syncCompleteEvent != null) {
            refreshView();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void refreshViewFromLanguageChange(LanguageConfigurationEvent languageConfigurationEvent) {
        if (languageConfigurationEvent != null) {
            refreshView();
        }

    }

}
