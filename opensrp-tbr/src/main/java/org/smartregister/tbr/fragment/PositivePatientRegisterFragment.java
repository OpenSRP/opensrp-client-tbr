package org.smartregister.tbr.fragment;

import android.view.View;

import org.smartregister.tbr.R;

import java.util.Arrays;
import java.util.List;

import static util.TbrConstants.KEY.DIAGNOSIS_DATE;
import static util.TbrConstants.VIEW_CONFIGS.POSITIVE_REGISTER_HEADER;

/**
 * Created by samuelgithengi on 11/27/17.
 */

public class PositivePatientRegisterFragment extends BaseRegisterFragment {

    @Override
    protected void populateClientListHeaderView(View view) {
        View headerLayout = getLayoutInflater(null).inflate(R.layout.register_positive_list_header, null);
        populateClientListHeaderView(view, headerLayout, POSITIVE_REGISTER_HEADER);
    }

    @Override
    protected String getMainCondition() {
        return " confirmed_tb = \"yes\" AND treatment_initiation_date IS NULL";
    }

    @Override
    protected String[] getAdditionalColumns(String tableName) {
        return new String[]{
                tableName + "." + DIAGNOSIS_DATE};
    }

}
