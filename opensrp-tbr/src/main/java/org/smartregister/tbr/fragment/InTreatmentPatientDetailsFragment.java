package org.smartregister.tbr.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.event.LanguageConfigurationEvent;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

import java.util.Locale;
import java.util.Map;

import static org.smartregister.tbr.util.Constants.INTENT_KEY.REGISTER_TITLE;

/**
 * Created by ndegwamartin on 24/11/2017.
 */


public class InTreatmentPatientDetailsFragment extends BasePatientDetailsFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_intreatment_patient_detail, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(activity.getIntent().getStringExtra(REGISTER_TITLE));
        rootView.setTag(R.id.VIEW_CONFIGURATION_ID, getViewConfigurationIdentifier());
        if (patientDetails != null && patientDetails.containsKey(Constants.KEY._ID)) {
            rootView.setTag(R.id.BASE_ENTITY_ID, patientDetails.get(Constants.KEY._ID));
        }
        setupViews(rootView);
        return rootView;
    }

    @Override
    public void setupViews(View rootView) {
        super.setupViews(rootView);
        processViewConfigurations(rootView);
    }

    @Override
    public void setPatientDetails(Map<String, String> patientDetails) {
        this.patientDetails = patientDetails;
    }

    @Override
    protected String getViewConfigurationIdentifier() {
        return Constants.CONFIGURATION.PATIENT_DETAILS_INTREATMENT;
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

    @Override
    protected void onResumption() {
        //Overridden method
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(TbrApplication.getInstance().getApplicationContext()));
            Utils.setLocale(new Locale(allSharedPreferences.getPreference("locale")));
            org.smartregister.tbr.util.Utils.postEvent(new LanguageConfigurationEvent(false));
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
