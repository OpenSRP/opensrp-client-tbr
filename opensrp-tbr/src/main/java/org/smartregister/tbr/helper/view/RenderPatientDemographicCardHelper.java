package org.smartregister.tbr.helper.view;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.repository.ResultDetailsRepository;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

import java.util.Map;

/**
 * Created by ndegwamartin on 23/11/2017.
 */

public class RenderPatientDemographicCardHelper extends BaseRenderHelper {
    private static final String TAG = RenderPatientDemographicCardHelper.class.getCanonicalName();

    public RenderPatientDemographicCardHelper(Context context, ResultDetailsRepository detailsRepository) {
        super(context, detailsRepository);
    }

    @Override
    public void renderView(final View view, final Map<String, String> patientDetails) {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                try {
                    /*if (view.getTag(R.id.VIEW_CONFIGURATION_ID) == Constants.CONFIGURATION.PATIENT_DETAILS_INTREATMENT) {
                        Map<String, String> details = TbrApplication.getInstance().getContext().detailsRepository().getAllDetailsForClient(patientDetails.get(Constants.KEY._ID));

                        TextView patientTypeTextView = (TextView) view.findViewById(R.id.patientTypeTextView);
                        if (details.get(Constants.KEY.PATIENT_TYPE) != null && !details.get(Constants.KEY.PATIENT_TYPE).isEmpty()) {
                            patientTypeTextView.setText(WordUtils.capitalizeFully(details.get(Constants.KEY.PATIENT_TYPE).replace(Constants.CHAR.UNDERSCORE, Constants.CHAR.SPACE)));
                            patientTypeTextView.setVisibility(View.VISIBLE);
                        }
                        if (details.get(Constants.KEY.SITE_OF_DISEASE) != null && !details.get(Constants.KEY.SITE_OF_DISEASE).isEmpty()) {
                            TextView siteOfDiseaseTextView = (TextView) view.findViewById(R.id.siteOfDiseaseTextView);
                            siteOfDiseaseTextView.setText(Utils.getTBTypeByCode(details.get(Constants.KEY.SITE_OF_DISEASE)));
                            siteOfDiseaseTextView.setVisibility(View.VISIBLE);
                        }
                    }*/
                    TextView tbReachIdTextView = (TextView) view.findViewById(R.id.tbReachIdTextView);
                    tbReachIdTextView.setText(Utils.formatIdentifier(patientDetails.get(Constants.KEY.PARTICIPANT_ID)));

                    TextView clientAgeTextView = (TextView) view.findViewById(R.id.clientAgeTextView);
                    String dobString = patientDetails.get(Constants.KEY.DOB);
                    String formattedAge = Utils.getFormattedAgeString(dobString);
                    if(formattedAge.contains("y") && TbrApplication.getInstance().getApplicationContext().getResources().getConfiguration().locale.getLanguage().equalsIgnoreCase("es")){
                        formattedAge = formattedAge.replace('y','a');
                    }
                    if(formattedAge.contains("w") && TbrApplication.getInstance().getApplicationContext().getResources().getConfiguration().locale.getLanguage().equalsIgnoreCase("es")){
                        formattedAge = formattedAge.replace('w','s');
                    }
                    clientAgeTextView.setText(formattedAge);

                    TextView clientNameTextView = (TextView) view.findViewById(R.id.clientNameTextView);
                    String fullName = patientDetails.get(Constants.KEY.FIRST_NAME) + " " + patientDetails.get(Constants.KEY.LAST_NAME);
                    clientNameTextView.setText(fullName);
                    TextView clientGenderTextView = (TextView) view.findViewById(R.id.clientGenderTextView);

                    if(patientDetails.get(Constants.KEY.GENDER).equalsIgnoreCase("male"))
                        clientGenderTextView.setText(context.getString(R.string.male));
                    else
                        clientGenderTextView.setText(context.getString(R.string.female));

                    TextView clientInitalsTextView = (TextView) view.findViewById(R.id.clientInitalsTextView);
                    clientInitalsTextView.setText(Utils.getShortInitials(fullName));

                    if (patientDetails.get(Constants.KEY.GENDER).equals(Constants.GENDER.MALE)) {
                        clientInitalsTextView.setBackgroundColor(context.getResources().getColor(R.color.male_light_blue));
                        clientInitalsTextView.setTextColor(context.getResources().getColor(R.color.male_blue));

                    } else if (patientDetails.get(Constants.KEY.GENDER).equals(Constants.GENDER.FEMALE)) {
                        clientInitalsTextView.setBackgroundColor(context.getResources().getColor(R.color.female_light_pink));
                        clientInitalsTextView.setTextColor(context.getResources().getColor(R.color.female_pink));
                    } else {
                        clientInitalsTextView.setBackgroundColor(context.getResources().getColor(R.color.gender_neutral_light_green));
                        clientInitalsTextView.setTextColor(context.getResources().getColor(R.color.gender_neutral_green));
                    }

                    // MOTHER NAME:
                    TextView motherNameTextView = (TextView) view.findViewById(R.id.mother_name);
                    String motherName = patientDetails.get(Constants.KEY.MOTHER_NAME);
                    motherNameTextView.setText(motherName);

                    // Address:
                    TextView addressTextView = (TextView) view.findViewById(R.id.address);
                    String address = patientDetails.get(Constants.KEY.FULL_ADDRESS);
                    addressTextView.setText(address);

                    // Developemnatal Disability:
                    TextView developmentalDisabilityTextView = (TextView) view.findViewById(R.id.developmentDisability);
                    String developmentDisability = patientDetails.get(Constants.KEY.DEVELOPMENTAL_DISABILITY);
                    if(developmentDisability.equalsIgnoreCase("no"))
                        developmentalDisabilityTextView.setText(context.getString(R.string.no));
                    else
                        developmentalDisabilityTextView.setText(context.getString(R.string.yes));

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

        });
    }
}
