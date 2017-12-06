package util;

import org.smartregister.AllConstants;
import org.smartregister.tbr.BuildConfig;

/**
 * Created by SGithengi on 09/10/17.
 */
public class
TbrConstants extends AllConstants {

    public static final long MAX_SERVER_TIME_DIFFERENCE = BuildConfig.MAX_SERVER_TIME_DIFFERENCE;
    public static final boolean TIME_CHECK = BuildConfig.TIME_CHECK;
    public static final String LAST_SYNC_TIMESTAMP = "LAST_SYNC_TIMESTAMP";

    public static final String PATIENT_TABLE_NAME = "ec_patient";

    public static final class KEY {
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String DOB = "dob";
        public static final String TBREACH_ID = "tbreach_id";
        public static final String GENDER = "gender";
        public static final String BASE_ENTITY_ID_COLUMN = "base_entity_id";
        public static final String FIRST_ENCOUNTER = "first_encounter";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String TREATMENT_INITIATION_DATE = "treatment_initiation_date";
    }


    public static final class RESULT {
        public static final String MTB_RESULT = "mtb_result";
        public static final String RIF_RESULT = "rif_result";
        public static final String XRAY_RESULT = "xray_result";
        public static final String CULTURE_RESULT = "culture_result";
        public static final String TEST_RESULT = "test_result";
    }

    public static final class REGISTER_COLUMNS {
        public static final String PATIENT = "patient";
        public static final String RESULTS = "results";
        public static final String DIAGNOSE = "diagnose";
        public static final String ENCOUNTER = "encounter";
        public static final String XPERT_RESULTS = "xpert_results";
        public static final String DROPDOWN = "dropdown";
        public static final String TREAT = "treat";
        public static final String DIAGNOSIS = "diagnosis";
    }

    public static final class VIEW_CONFIGS {

        public static final String COMMON_REGISTER_HEADER = "common_register_header";
        public static final String COMMON_REGISTER_ROW = "common_register_row";


        public static final String PRESUMPTIVE_REGISTER = "presumptive_register";
        public static final String PRESUMPTIVE_REGISTER_HEADER = "presumptive_register_header";
        public static final String PRESUMPTIVE_REGISTER_ROW = "presumptive_register_row";

        public static final String POSITIVE_REGISTER = "positive_register";
        public static final String POSITIVE_REGISTER_HEADER = "positive_register_header";
        public static final String POSITIVE_REGISTER_ROW = "positive_register_row";

        public static final String INTREATMENT_REGISTER = "positive_register";
    }

    public static final class ENKETO_FORMS {
        public static final String SCREENING_FORM = "new_patient_registration";
        public static final String GENE_XPERT = "result_gene_xpert";
        public static final String SMEAR = "result_smear";
        public static final String CHEST_XRAY = "result_chest_xray";
        public static final String CULTURE = "result_culture";
        public static final String DIAGNOSIS = "diagnosis";
        public static final String ADD_POSITIVE_PATIENT = "add_positive_patient";
        public static final String TREATMENT = "treatment_initiation";
    }
}