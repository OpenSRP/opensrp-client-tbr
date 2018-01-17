package org.smartregister.tbr.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avocarrot.json2view.DynamicView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.tbr.util.Constants;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 24/11/2017.
 */


public class PresumptivePatientDetailsFragment extends BasePatientDetailsFragment implements View.OnClickListener {
    private static final String TAG = PresumptivePatientDetailsFragment.class.getCanonicalName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_presumptive_patient_detail, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(activity.getIntent().getStringExtra(Constants.INTENT_KEY.REGISTER_TITLE));
        setupViews(rootView);
        return rootView;
    }

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
        return Constants.CONFIGURATION.PRESUMPTIVE_PATIENT_DETAILS;
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
        //Overrides
    }

    @Override
    protected void processViewConfigurations(View rootView) {

        String jsonString = TbrApplication.getInstance().getConfigurableViewsRepository().getConfigurableViewJson(getViewConfigurationIdentifier());
        if (jsonString == null ) {
            renderDefaultLayout(rootView);
        } else {
            ViewConfiguration detailsView = TbrApplication.getJsonSpecHelper().getConfigurableView(jsonString);
            List<org.smartregister.tbr.jsonspec.model.View> views = detailsView.getViews();
            if (!views.isEmpty()) {
                Collections.sort(views, new Comparator<org.smartregister.tbr.jsonspec.model.View>() {
                    @Override
                    public int compare(org.smartregister.tbr.jsonspec.model.View registerA, org.smartregister.tbr.jsonspec.model.View registerB) {
                        return registerA.getResidence().getPosition() - registerB.getResidence().getPosition();
                    }
                });

                LinearLayout viewParent = (LinearLayout) rootView.findViewById(R.id.content_presumptive_patient_detail_container);
                for (org.smartregister.tbr.jsonspec.model.View componentView : views) {

                    try {
                        if (componentView.getResidence().getParent() == null) {
                            componentView.getResidence().setParent(detailsView.getIdentifier());
                        }

                        String jsonComponentString = TbrApplication.getInstance().getConfigurableViewsRepository().getConfigurableViewJson(componentView.getIdentifier());
                        ViewConfiguration componentViewConfiguration = TbrApplication.getJsonSpecHelper().getConfigurableView(jsonComponentString);
                        if (componentViewConfiguration != null) {
                            JSONObject jsonViewObject = new JSONObject(componentViewConfiguration.getJsonView());
                            View json2View = DynamicView.createView(getActivity().getApplicationContext(), jsonViewObject, viewParent);

                            View view = viewParent.findViewById(json2View.getId());
                            if (view != null) {
                                viewParent.removeView(view);
                            }
                            viewParent.addView(json2View);
                            if (componentViewConfiguration.getIdentifier().equals(Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_DEMOGRAPHICS)) {
                                renderDemographicsView(json2View, patientDetails);

                            } else if (componentViewConfiguration.getIdentifier().equals(Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_POSITIVE)) {
                                renderPositiveResultsView(json2View, patientDetails);
                                //Record Results click handler
                                TextView recordResults = (TextView) view.findViewById(R.id.record_results);
                                recordResults.setOnClickListener(this);

                            } else if (componentViewConfiguration.getIdentifier().equals(Constants.CONFIGURATION.COMPONENTS.PATIENT_DETAILS_SERVICE_HISTORY)) {

                                renderServiceHistoryView(json2View, patientDetails);
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
                processLanguageTokens(detailsView.getLabels(), languageTranslations, rootView);
            }
        }

    }

    @Override
    protected void renderDefaultLayout(View rootView) {

        renderDemographicsView(rootView, patientDetails);
        renderPositiveResultsView(rootView, patientDetails);
        renderServiceHistoryView(rootView, patientDetails);
    }

}
