package org.smartregister.tbr.fragment;

import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.BasePatientDetailActivity;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.event.EnketoFormSaveCompleteEvent;
import org.smartregister.tbr.helper.view.RenderBMIHeightChartCardHelper;
import org.smartregister.tbr.helper.view.RenderContactScreeningCardHelper;
import org.smartregister.tbr.helper.view.RenderPatientDemographicCardHelper;
import org.smartregister.tbr.helper.view.RenderPatientFollowupCardHelper;
import org.smartregister.tbr.helper.view.RenderPositiveResultsCardHelper;
import org.smartregister.tbr.helper.view.RenderServiceHistoryCardHelper;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;
import org.smartregister.util.DateUtil;
import org.smartregister.view.fragment.SecuredFragment;

import java.util.HashMap;
import java.util.Map;

import util.TbrConstants;

import static util.TbrConstants.ENKETO_FORMS.FOLLOWUP_VISIT;

/**
 * Created by ndegwamartin on 06/12/2017.
 */

public abstract class BasePatientDetailsFragment extends SecuredFragment implements View.OnClickListener {

    protected Map<String, String> patientDetails;
    protected ResultMenuListener resultMenuListener;
    protected Map<String, String> languageTranslations;

    protected abstract void processViewConfigurations(View view);

    protected abstract void renderDefaultLayout(View view);

    protected abstract void setPatientDetails(Map<String, String> patientDetails);

    protected abstract String getViewConfigurationIdentifier();

    protected void renderPositiveResultsView(View view, Map<String, String> patientDetails) {
        RenderPositiveResultsCardHelper renderPositiveResultsHelper = new RenderPositiveResultsCardHelper(getActivity(), TbrApplication.getInstance().getResultsRepository());
        renderPositiveResultsHelper.renderView(view, patientDetails);
    }

    protected void renderDemographicsView(View view, Map<String, String> patientDetails) {

        RenderPatientDemographicCardHelper renderPatientDemographicCardHelper = new RenderPatientDemographicCardHelper(getActivity(), TbrApplication.getInstance().getResultDetailsRepository());
        renderPatientDemographicCardHelper.renderView(view, patientDetails);

    }

    protected void renderFollowUpView(View view, Map<String, String> patientDetails) {

        RenderPatientFollowupCardHelper renderPatientFollowupCardHelper = new RenderPatientFollowupCardHelper(getActivity(), TbrApplication.getInstance().getResultDetailsRepository());
        renderPatientFollowupCardHelper.renderView(view, patientDetails);

    }

    protected void renderServiceHistoryView(View view, Map<String, String> patientDetails) {
        RenderServiceHistoryCardHelper renderServiceHistoryHelper = new RenderServiceHistoryCardHelper(getActivity(), TbrApplication.getInstance().getResultDetailsRepository());
        renderServiceHistoryHelper.renderView(view, patientDetails);
    }


    protected void renderContactScreeningView(View view, Map<String, String> patientDetails) {
        RenderContactScreeningCardHelper renderContactScreeningHelper = new RenderContactScreeningCardHelper(getActivity(), TbrApplication.getInstance().getResultsRepository());
        renderContactScreeningHelper.renderView(view, patientDetails);
    }

    protected void processLanguageTokens(Map<String, String> viewLabelsMap, Map<String, String> languageTranslations, View parentView) {
        //Process token translations
        if (!viewLabelsMap.isEmpty()) {
            for (Map.Entry<String, String> entry : viewLabelsMap.entrySet()) {
                String uniqueIdentifier = entry.getKey();
                TextView textView = (TextView) parentView.findViewById(Utils.getLayoutIdentifierResourceId(getActivity(), uniqueIdentifier));
                if (textView != null && !languageTranslations.isEmpty() && languageTranslations.containsKey(entry.getKey())) {
                    textView.setText(languageTranslations.get(entry.getKey()));
                }
            }
        }
    }

    protected void renderBMIHeightChartView(View view, Map<String, String> patientDetails) {

        RenderBMIHeightChartCardHelper renderBMIHeightChartCardHelper = new RenderBMIHeightChartCardHelper(getActivity(), TbrApplication.getInstance().getResultDetailsRepository());
        renderBMIHeightChartCardHelper.renderView(view, patientDetails);

    }

    @Override
    protected void onCreation() {

        resultMenuListener = new ResultMenuListener(patientDetails.get(Constants.KEY._ID));

    }

    @Override
    protected void onResumption() {
        //Overrides
    }


    protected FieldOverrides getFieldOverrides() {
        Map fields = new HashMap();
        fields.put("participant_id", patientDetails.get(TbrConstants.KEY.TBREACH_ID));
        JSONObject fieldOverridesJson = new JSONObject(fields);
        FieldOverrides fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }

    protected FieldOverrides getTreatmentFieldOverrides() {
        Map fields = new HashMap();
        fields.put("participant_id", patientDetails.get(TbrConstants.KEY.TBREACH_ID));
        fields.put("first_name", patientDetails.get(TbrConstants.KEY.FIRST_NAME));
        fields.put("last_name", patientDetails.get(TbrConstants.KEY.LAST_NAME));

        fields.put("gender", patientDetails.get(TbrConstants.KEY.GENDER));
        String dobString = patientDetails.get(TbrConstants.KEY.DOB);
        String age = "";
        if (StringUtils.isNotBlank(dobString)) {
            try {
                DateTime birthDateTime = new DateTime(dobString);
                String duration = DateUtil.getDuration(birthDateTime);
                if (duration != null) {
                    age = duration.substring(0, duration.length() - 1);
                }
            } catch (Exception e) {
                Log.e(getClass().getName(), e.toString(), e);
            }
        }
        fields.put("age", age);
        JSONObject fieldOverridesJson = new JSONObject(fields);
        FieldOverrides fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }

    protected FieldOverrides getRegistrationFieldOverrides() {
        Map fields = new HashMap();
        fields.put("participant_id", patientDetails.get(TbrConstants.KEY.TBREACH_ID));
        fields.put("first_name", patientDetails.get(TbrConstants.KEY.FIRST_NAME));
        fields.put("last_name", patientDetails.get(TbrConstants.KEY.LAST_NAME));

        fields.put("gender", patientDetails.get(TbrConstants.KEY.GENDER));
        String dobString = patientDetails.get(TbrConstants.KEY.DOB);
        String age = "";
        if (StringUtils.isNotBlank(dobString)) {
            try {
                DateTime birthDateTime = new DateTime(dobString);
                String duration = DateUtil.getDuration(birthDateTime);
                if (duration != null) {
                    age = duration.substring(0, duration.length() - 1);
                }
            } catch (Exception e) {
                Log.e(getClass().getName(), e.toString(), e);
            }
        }
        fields.put("age", age);
        JSONObject fieldOverridesJson = new JSONObject(fields);
        FieldOverrides fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }


    class ResultMenuListener implements PopupMenu.OnMenuItemClickListener {

        private String clientIdentifier;

        ResultMenuListener(String clientIdentifier) {
            this.clientIdentifier = clientIdentifier;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            BasePatientDetailActivity registerActivity = (BasePatientDetailActivity) getActivity();
            switch (item.getItemId()) {
                case R.id.result_gene_xpert:
                    registerActivity.startFormActivity(TbrConstants.ENKETO_FORMS.GENE_XPERT, clientIdentifier, getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_smear:
                    registerActivity.startFormActivity(TbrConstants.ENKETO_FORMS.SMEAR, clientIdentifier, getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_chest_xray:
                    registerActivity.startFormActivity(TbrConstants.ENKETO_FORMS.CHEST_XRAY, clientIdentifier, getFieldOverrides().getJSONString());
                    return true;
                case R.id.result_culture:
                    registerActivity.startFormActivity(TbrConstants.ENKETO_FORMS.CULTURE, clientIdentifier, getFieldOverrides().getJSONString());
                    return true;
                default:
                    return false;
            }
        }
    }


    public void showResultMenu(View view) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        popup.inflate(R.menu.menu_register_result);
        popup.setOnMenuItemClickListener(resultMenuListener);
        MenuItem item = popup.getMenu().getItem(0);
        String firstName = getActivity().getString(R.string.add_result_for) + Constants.CHAR.SPACE + patientDetails.get(Constants.KEY.FIRST_NAME);
        SpannableString s = new SpannableString(firstName);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, firstName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        item.setTitle(s);
        popup.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshView(EnketoFormSaveCompleteEvent enketoFormSaveCompleteEvent) {
        if (enketoFormSaveCompleteEvent != null) {
            processViewConfigurations(getView());
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_contact:
                Utils.showToast(getActivity(), "Launch TB Contact Form");
                break;
            case R.id.follow_up_button:
                ((BasePatientDetailActivity) getActivity()).startFormActivity(FOLLOWUP_VISIT, view.getTag(R.id.CLIENT_ID).toString(), getTreatmentFieldOverrides().getJSONString());
                break;
            case R.id.record_results:
                showResultMenu(view);
                break;
            default:
                break;

        }
    }

    protected void setupViews(View rootView) {

        //Load Language Token Map
        ViewConfiguration config = TbrApplication.getJsonSpecHelper().getLanguage(Utils.getLanguage());
        languageTranslations = config == null ? null : config.getLabels();


        //Remove patient button
        Button removePatientButton = (Button) rootView.findViewById(R.id.remove_patient);
        if (removePatientButton != null) {
            removePatientButton.setTag(R.id.CLIENT_ID, patientDetails.get(Constants.KEY._ID));
        }


        Button recordOutcomeButton = (Button) rootView.findViewById(R.id.record_outcome);
        if (recordOutcomeButton != null) {
            recordOutcomeButton.setTag(R.id.CLIENT_ID, patientDetails.get(Constants.KEY._ID));
            recordOutcomeButton.setVisibility(View.VISIBLE);
        }


        Button followUpButton = (Button) rootView.findViewById(R.id.follow_up_button);
        if (followUpButton != null) {
            followUpButton.setTag(R.id.CLIENT_ID, patientDetails.get(Constants.KEY._ID));
            followUpButton.setOnClickListener(this);
        }

        //Record Results click handler
        TextView recordResults = (TextView) rootView.findViewById(R.id.record_results);
        if (recordResults != null) {
            recordResults.setOnClickListener(this);
        }

        TextView addContactView = (TextView) rootView.findViewById(R.id.add_contact);
        if (addContactView != null) {
            addContactView.setOnClickListener(this);
        }
    }

}
