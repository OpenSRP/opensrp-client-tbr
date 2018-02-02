package org.smartregister.tbr.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.jsonspec.ConfigurableViewsHelper;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.tbr.util.Constants;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.smartregister.tbr.util.Constants.INTENT_KEY.REGISTER_TITLE;

/**
 * Created by ndegwamartin on 24/11/2017.
 */


public class InTreatmentPatientDetailsFragment extends BasePatientDetailsFragment {
    private static final String TAG = InTreatmentPatientDetailsFragment.class.getCanonicalName();
    private ViewConfiguration detailsView;

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
    protected void processViewConfigurations(View rootView) {
        try {
            String jsonString = TbrApplication.getInstance().getConfigurableViewsRepository().getConfigurableViewJson(getViewConfigurationIdentifier());
            if (jsonString == null) {
                renderDefaultLayout(rootView);

            } else {
                detailsView = TbrApplication.getJsonSpecHelper().getConfigurableView(jsonString);
                List<org.smartregister.tbr.jsonspec.model.View> views = detailsView.getViews();
                if (!views.isEmpty()) {
                    Collections.sort(views, new Comparator<org.smartregister.tbr.jsonspec.model.View>() {
                        @Override
                        public int compare(org.smartregister.tbr.jsonspec.model.View registerA, org.smartregister.tbr.jsonspec.model.View registerB) {
                            return registerA.getResidence().getPosition() - registerB.getResidence().getPosition();
                        }
                    });

                    LinearLayout viewParent = (LinearLayout) rootView.findViewById(R.id.content_intreatment_patient_detail_container);
                    for (org.smartregister.tbr.jsonspec.model.View componentView : views) {

                        try {
                            if (componentView.getResidence().getParent() == null) {
                                componentView.getResidence().setParent(detailsView.getIdentifier());
                            }

                            String jsonComponentString = TbrApplication.getInstance().getConfigurableViewsRepository().getConfigurableViewJson(componentView.getIdentifier());
                            ViewConfiguration componentViewConfiguration = TbrApplication.getJsonSpecHelper().getConfigurableView(jsonComponentString);
                            if (componentViewConfiguration != null) {

                                ConfigurableViewsHelper configurableViewsHelper = TbrApplication.getInstance().getConfigurableViewsHelper();

                                View fallbackView = viewParent.findViewById(getCardviewIdentifierByConfiguration(componentViewConfiguration.getIdentifier()));
                                View json2View = TbrApplication.getJsonSpecHelper().isEnableJsonViews() ? configurableViewsHelper.inflateDynamicView(componentViewConfiguration, viewParent, fallbackView, componentView.isVisible()) : fallbackView;
                                if (componentView.isVisible()) {

                                    if (componentViewConfiguration.getIdentifier().equals(Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_DEMOGRAPHICS)) {
                                        json2View.setTag(R.id.VIEW_CONFIGURATION_ID, getViewConfigurationIdentifier());
                                        renderDemographicsView(json2View, patientDetails);

                                    } else if (componentViewConfiguration.getIdentifier().equals(Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_POSITIVE)) {
                                        renderPositiveResultsView(json2View, patientDetails);
                                        //Record Results click handler
                                        TextView recordResults = (TextView) json2View.findViewById(R.id.record_results);
                                        recordResults.setOnClickListener(this);

                                    } else if (componentViewConfiguration.getIdentifier().equals(Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_FOLLOWUP)) {

                                        renderFollowUpView(json2View, patientDetails);
                                        Button followUpButton = (Button) json2View.findViewById(R.id.follow_up_button);
                                        followUpButton.setTag(R.id.CLIENT_ID, patientDetails.get(Constants.KEY._ID));
                                        followUpButton.setOnClickListener(this);
                                    } else if (componentViewConfiguration.getIdentifier().equals(Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_BMI)) {
                                        json2View.setTag(R.id.BASE_ENTITY_ID, patientDetails.get(Constants.KEY._ID));
                                        renderBMIHeightChartView(json2View, patientDetails);

                                    } else if (componentViewConfiguration.getIdentifier().equals(Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_CONTACT_SCREENING)) {
                                        renderContactScreeningView(json2View, patientDetails);

                                        TextView addContactView = (TextView) json2View.findViewById(R.id.add_contact);
                                        addContactView.setTag(R.id.CLIENT_ID, patientDetails.get(Constants.KEY._ID));
                                        addContactView.setOnClickListener(this);

                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                } else {
                    renderDefaultLayout(rootView);
                }

                if (detailsView != null) {
                    processLanguageTokens(detailsView.getLabels(), rootView);
                }
            }
        } catch (Exception e) {

            Log.e(TAG, e.getMessage());
        }

    }

    @Override
    protected void renderDefaultLayout(View rootView) {

        renderDemographicsView(rootView, patientDetails);
        renderPositiveResultsView(rootView, patientDetails);
        renderContactScreeningView(rootView, patientDetails);
        renderFollowUpView(rootView, patientDetails);
        renderBMIHeightChartView(rootView, patientDetails);
    }

}
