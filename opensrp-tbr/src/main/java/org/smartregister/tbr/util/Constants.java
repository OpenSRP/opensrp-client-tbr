package org.smartregister.tbr.util;

/**
 * Created by ndegwamartin on 13/10/2017.
 */

public class Constants {
    public static class INTENT_KEY {
        public static final String FULL_NAME = "full_name";
        public static final String IS_REMOTE_LOGIN = "is_remote_login";
        public static final String REGISTER_TITLE = "register_title";
        public static final String PATIENT_DETAIL_MAP = "patient_detail_map";
    }

    public static class CHAR {
        public static final String SPACE = " ";
        public static final String UNDERSCORE = "_";
        public static final String HYPHEN = "-";
        public static final String COLON = ":";
        public static final String HASH = "#";
        public static final String NO_CHAR = "";
    }

    public static class CONFIGURATION {
        public static final String LOGIN = "login";
        public static final String HOME = "home";
        public static final String MAIN = "main";
        public static final String LANG = "lang";
        public static final String PRESUMPTIVE_PATIENT_DETAILS = "presumptive_patient_details";
    }

    public static final class GENDER {

        public static final String MALE = "male";
        public static final String FEMALE = "female";
        public static final String TRANSGENDER = "transgender";
    }

    public static final class KEY {
        public static final String _ID = "_id";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String FIRST_ENCOUNTER = "first_encounter";
        public static final String DATE = "date";
        public static final String TBREACH_ID = "tbreach_id";
        public static final String DOB = "dob";
        public static final String GENDER = "gender";
        public static final String PARTICIPANT_ID = "participant_id";
    }

    public static final class FORM {
        public static final String NEW_PATIENT_REGISTRATION = "new_patient_registration";
        public static final String RESULT_SMEAR = "result_smear";
        public static final String RESULT_CHEST_XRAY = "result_chest_xray";
        public static final String RESULT_CULTURE = "result_culture";
        public static final String DIAGNOSIS = "diagnosis";
        public static final String RESULT_GENE_EXPERT = "result_gene_xpert";
    }
}
