package org.smartregister.tbr.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.CursorSortOption;
import org.smartregister.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.BaseRegisterActivity;
import org.smartregister.tbr.activity.PresumptivePatientRegisterActivity;
import org.smartregister.tbr.jsonspec.model.RegisterConfiguration;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.tbr.provider.PatientRegisterProvider;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import util.TbrConstants;

import static org.smartregister.tbr.activity.BaseRegisterActivity.TOOLBAR_TITLE;
import static util.TbrConstants.PRESUMPTIVE_REGISTER_COLUMNS.DIAGNOSIS;
import static util.TbrConstants.PRESUMPTIVE_REGISTER_COLUMNS.PATIENT;
import static util.TbrConstants.PRESUMPTIVE_REGISTER_COLUMNS.RESULTS;

/**
 * Created by samuelgithengi on 11/6/17.
 */

public class PresumptivePatientRegisterFragment extends BaseRegisterFragment {

    private RegisterActionHandler registerActionHandler = new RegisterActionHandler();
    private ResultMenuListener resultMenuListener = new ResultMenuListener();
    private CommonPersonObjectClient patient;
    private Set<org.smartregister.tbr.jsonspec.model.View> visibleColumns;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_activity, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.register_toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(activity.getIntent().getStringExtra(TOOLBAR_TITLE));
        setupViews(view);
        return view;

    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
        view.findViewById(R.id.sorted_by_bar).setVisibility(View.GONE);
        processViewConfigurations();
        initializeQueries();
        updateSearchView();
        populateClientListHeaderView(view);
    }

    private void processViewConfigurations() {
        ViewConfiguration viewConfiguration = ((BaseRegisterActivity) getActivity()).viewConfiguration;
        RegisterConfiguration config = (RegisterConfiguration) viewConfiguration.getMetadata();
        if (config.getSearchBarText() != null && getView() != null)
            ((EditText) getView().findViewById(R.id.edt_search)).setHint(config.getSearchBarText());
        visibleColumns = new TreeSet<>(new ViewPositionComparator());
        for (org.smartregister.tbr.jsonspec.model.View view : viewConfiguration.getViews()) {
            if (view.isVisible())
                visibleColumns.add(view);
        }
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            initializeQueries();
        }
        updateSearchView();
        processViewConfigurations();
    }

    public void showResultMenu(View view) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        popup.inflate(R.menu.menu_register_result);
        popup.setOnMenuItemClickListener(resultMenuListener);
        MenuItem item = popup.getMenu().getItem(0);
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, item.getTitle().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        item.setTitle(s);
        popup.show();
    }

    private void initializeQueries() {
        String tableName = TbrConstants.PATIENT_TABLE_NAME;

        PatientRegisterProvider hhscp = new PatientRegisterProvider(getActivity(), visibleColumns, registerActionHandler, context().detailsRepository());
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, hhscp, context().commonrepository(tableName));
        clientsView.setAdapter(clientAdapter);

        setTablename(tableName);
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
        countqueryBUilder.SelectInitiateMainTableCounts(tableName);
        mainCondition = " presumptive =\"yes\" AND confirmed_tb IS NULL";
        countSelect = countqueryBUilder.mainCondition(mainCondition);
        super.CountExecute();

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, new String[]{
                tableName + ".relationalid",
                tableName + "." + TbrConstants.KEY.BASE_ENTITY_ID_COLUMN,
                tableName + "." + TbrConstants.KEY.FIRST_NAME,
                tableName + "." + TbrConstants.KEY.LAST_NAME,
                tableName + "." + TbrConstants.KEY.TBREACH_ID,
                tableName + "." + TbrConstants.KEY.GENDER,
                tableName + "." + TbrConstants.KEY.DOB
        });
        mainSelect = queryBUilder.mainCondition(mainCondition);
        Sortqueries = ((CursorSortOption) getDefaultOptionsProvider().sortOption()).sort();

        currentlimit = 20;
        currentoffset = 0;

        super.filterandSortInInitializeQueries();

        refresh();
    }


    private void populateClientListHeaderView(View view) {
        LinearLayout clientsHeaderLayout = (LinearLayout) view.findViewById(org.smartregister.R.id.clients_header_layout);
        clientsHeaderLayout.setVisibility(View.GONE);

        LinearLayout headerLayout = (LinearLayout) getLayoutInflater(null).inflate(R.layout.register_list_header, null);
        List<View> columns = new LinkedList<>();
        for (org.smartregister.tbr.jsonspec.model.View columnView : visibleColumns) {
            View column = null;
            switch (columnView.getIdentifier()) {
                case PATIENT:
                    column = headerLayout.findViewById(R.id.patient_header);
                    break;
                case RESULTS:
                    column = headerLayout.findViewById(R.id.results_header);
                    break;
                case DIAGNOSIS:
                    column = headerLayout.findViewById(R.id.diagnose_header);
                    break;
            }
            if (columnView.getResidence().getLayoutWeight() != null) {
                LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) column.getLayoutParams();
                param.weight = Float.valueOf(columnView.getResidence().getLayoutWeight());
                column.setLayoutParams(param);
            }
            columns.add(column);
        }
        ViewGroup allColumns = (ViewGroup) headerLayout.findViewById(R.id.registerHeaders);
        allColumns.removeAllViews();
        for (View column : columns)
            allColumns.addView(column);
        clientsView.addHeaderView(headerLayout);
        clientsView.setEmptyView(getActivity().findViewById(R.id.empty_view));

    }

    private void updateSearchView() {
        getSearchView().removeTextChangedListener(textWatcher);
        getSearchView().addTextChangedListener(textWatcher);
    }

    class RegisterActionHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
                patient = (CommonPersonObjectClient) view.getTag();
            }
            switch (view.getId()) {
                case R.id.result_lnk:
                    showResultMenu(view);
                    break;
                case R.id.diagnose_lnk:
                    PresumptivePatientRegisterActivity registerActivity = (PresumptivePatientRegisterActivity) getActivity();
                    registerActivity.startFormActivity("diagnosis", patient.getDetails().get("_id"), null);
                    break;
                default:
                    break;
            }
        }
    }

    class ResultMenuListener implements PopupMenu.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            PresumptivePatientRegisterActivity registerActivity = (PresumptivePatientRegisterActivity) getActivity();
            Map fields = new HashMap();
            fields.put("participant_id", patient.getDetails().get(TbrConstants.KEY.TBREACH_ID));
            JSONObject fieldOverridesJson = new JSONObject(fields);

            FieldOverrides fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
            switch (item.getItemId()) {
                case R.id.result_gene_xpert:
                    registerActivity.startFormActivity("result_gene_xpert", patient.getDetails().get("_id"), fieldOverrides.getJSONString());
                    return true;
                case R.id.result_smear:
                    registerActivity.startFormActivity("result_smear", patient.getDetails().get("_id"), fieldOverrides.getJSONString());
                    return true;
                case R.id.result_chest_xray:
                    registerActivity.startFormActivity("result_chest_xray", patient.getDetails().get("_id"), fieldOverrides.getJSONString());
                    return true;
                case R.id.result_culture:
                    registerActivity.startFormActivity("result_culture", patient.getDetails().get("_id"), fieldOverrides.getJSONString());
                    return true;
                default:
                    return false;
            }
        }
    }

    class ViewPositionComparator implements Comparator<org.smartregister.tbr.jsonspec.model.View> {

        @Override
        public int compare(org.smartregister.tbr.jsonspec.model.View v1, org.smartregister.tbr.jsonspec.model.View v2) {
            if (v1.getResidence() == null && v2.getResidence() == null)
                return 0;
            else if (v1.getResidence() == null && v2.getResidence() != null)
                return 1;
            else if (v1.getResidence() != null && v2.getResidence() == null)
                return -1;
            else
                return Integer.valueOf(v1.getResidence().getPosition()).compareTo(Integer.valueOf(v2.getResidence().getPosition()));
        }
    }
}
