package org.smartregister.tbr.fragment;

import android.view.View;

import org.smartregister.tbr.R;

import static util.TbrConstants.KEY.BASELINE;
import static util.TbrConstants.KEY.DIAGNOSIS_DATE;
import static util.TbrConstants.KEY.FUTURE_VISIT_DATE;
import static util.TbrConstants.KEY.NEXT_VISIT_DATE;
import static util.TbrConstants.KEY.SMR_NEXT_VISIT_DATE;
import static util.TbrConstants.KEY.TREATMENT_INITIATION_DATE;
import static util.TbrConstants.VIEW_CONFIGS.INTREATMENT_REGISTER_HEADER;

/**
 * Created by samuelgithengi on 11/27/17.
 */

public class InTreatmentPatientRegisterFragment extends BaseRegisterFragment {

    @Override
    protected void populateClientListHeaderView(View view) {
        View headerLayout = getLayoutInflater(null).inflate(R.layout.register_intreatment_list_header, null);
        populateClientListHeaderView(view, headerLayout, INTREATMENT_REGISTER_HEADER);
    }

    @Override
    protected String getMainCondition() {
        return TREATMENT_INITIATION_DATE + " IS NOT NULL";
    }

    @Override
    protected String[] getAdditionalColumns(String tableName) {
        return new String[]{
                tableName + "." + DIAGNOSIS_DATE,
                tableName + "." + TREATMENT_INITIATION_DATE,
                tableName + "." + BASELINE,
                tableName + "." + NEXT_VISIT_DATE,
                tableName + "." + FUTURE_VISIT_DATE,
                tableName + "." + SMR_NEXT_VISIT_DATE};
    }

}
