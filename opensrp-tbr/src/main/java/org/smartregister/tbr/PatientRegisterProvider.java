package org.smartregister.tbr;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.util.DateUtil;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import util.TbrConstants;

import static org.smartregister.util.Utils.fillValue;
import static org.smartregister.util.Utils.getName;
import static org.smartregister.util.Utils.getValue;

/**
 * Created by samuelgithengi on 11/8/17.
 */

public class PatientRegisterProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private final LayoutInflater inflater;
    private final Context context;

    public PatientRegisterProvider(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, View convertView) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        String firstName = getValue(pc.getColumnmaps(), TbrConstants.KEY.FIRST_NAME, true);
        String lastName = getValue(pc.getColumnmaps(), TbrConstants.KEY.LAST_NAME, true);
        String patientName = getName(firstName, lastName);

        fillValue((TextView) convertView.findViewById(R.id.patient_name), patientName);

        fillValue((TextView) convertView.findViewById(R.id.participant_id), getValue(pc.getColumnmaps(), TbrConstants.KEY.TBREACH_ID, false));


        String gender = getValue(pc.getColumnmaps(), TbrConstants.KEY.GENDER, true);

        DateTime birthDateTime;
        String dobString = getValue(pc.getColumnmaps(), TbrConstants.KEY.DOB, false);
        String age = "";
        if (StringUtils.isNotBlank(dobString)) {
            try {
                birthDateTime = new DateTime(dobString);
                String duration = DateUtil.getDuration(birthDateTime);
                if (duration != null) {
                    age = duration.substring(0, duration.length() - 1);
                }
            } catch (Exception e) {
                Log.e(getClass().getName(), e.toString(), e);
            }
        }
        String ageAndGender = String.format("%s, %s", age, gender);
        fillValue((TextView) convertView.findViewById(R.id.age_gender), ageAndGender);

    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        return inflater().inflate(R.layout.register_list_row, null);
    }
}
