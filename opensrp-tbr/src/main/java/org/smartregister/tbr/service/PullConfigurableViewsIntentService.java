package org.smartregister.tbr.service;

import android.app.IntentService;
import android.content.Intent;

import org.smartregister.Context;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.event.LanguageConfigurationEvent;
import org.smartregister.tbr.event.ViewConfigurationSyncCompleteEvent;
import org.smartregister.tbr.sync.ECSyncHelper;
import org.smartregister.tbr.util.Utils;

import static org.smartregister.util.Log.logError;

/**
 * Created by SGithengi on 19/10/2017.
 * An {@link IntentService} subclass for handling asynchronous tasks for fetching the user configurable views
 * <p>
 */
public class PullConfigurableViewsIntentService extends IntentService {
    public static final String VIEWS_URL = "/rest/viewconfiguration/sync";

    private static final String TAG = PullConfigurableViewsIntentService.class.getCanonicalName();

    private PullConfigurableViewsServiceHelper pullConfigurableViewsServiceHelper;

    public PullConfigurableViewsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                int count = pullConfigurableViewsServiceHelper.processIntent();
                if (count > 0) {
                    LanguageConfigurationEvent event = new LanguageConfigurationEvent(true);//To Do add check for language configs
                    Utils.postEvent(event);
                }

                Utils.postEvent(new ViewConfigurationSyncCompleteEvent());

            } catch (Exception e) {
                logError(TAG + " Error fetching configurable Views");
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = TbrApplication.getInstance().getContext();
        pullConfigurableViewsServiceHelper = new PullConfigurableViewsServiceHelper(getApplicationContext(),
                TbrApplication.getInstance().getConfigurableViewsRepository(), context.getHttpAgent(),
                context.configuration().dristhiBaseURL(), ECSyncHelper.getInstance(getApplicationContext()),
                TbrApplication.getInstance().getPassword() != null);
    }

}
