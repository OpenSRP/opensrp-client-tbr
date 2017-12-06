package org.smartregister.tbr.sync;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.model.Result;
import org.smartregister.tbr.repository.ResultDetailsRepository;
import org.smartregister.tbr.repository.ResultsRepository;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samuelgithengi on 11/13/17.
 */

public class TbrClientProcessor extends ClientProcessor {

    private static final String TAG = "TbrClientProcessor";
    private static TbrClientProcessor instance;

    private static final String[] RESULT_TYPES = {"GeneXpert Result", "Smear Result",
            "Culture Result", "X-Ray Result"};

    public TbrClientProcessor(Context context) {
        super(context);
    }

    public static TbrClientProcessor getInstance(Context context) {
        if (instance == null) {
            instance = new TbrClientProcessor(context);
        }

        return instance;
    }

    @Override
    public synchronized void processClient(List<JSONObject> events) throws Exception {
        String clientClassificationStr = getFileContents("ec_client_classification.json");
        String clientResultStr = getFileContents("ec_client_result.json");

        if (!events.isEmpty()) {
            for (JSONObject event : events) {

                String eventType = event.has("eventType") ? event.getString("eventType") : null;
                if (eventType == null) {
                    continue;
                }

                if (Arrays.asList(RESULT_TYPES).contains(eventType)) {
                    JSONObject clientResultJson = new JSONObject(clientResultStr);
                    if (isNullOrEmptyJSONObject(clientResultJson)) {
                        continue;
                    }
                    processResult(event, clientResultJson);
                } else {
                    JSONObject clientClassificationJson = new JSONObject(clientClassificationStr);
                    if (isNullOrEmptyJSONObject(clientClassificationJson)) {
                        continue;
                    }
                    //iterate through the events
                    if (event.has("client")) {
                        processEvent(event, event.getJSONObject("client"), clientClassificationJson);
                    }
                }
            }
        }
    }

    private boolean processResult(JSONObject event, JSONObject clientResultJson) {

        try {

            if (event == null || event.length() == 0) {
                return false;
            }

            if (clientResultJson == null || clientResultJson.length() == 0) {
                return false;
            }
            ContentValues contentValues = processCaseModel(event, clientResultJson);
            // save the values to db
            if (contentValues != null && contentValues.size() > 0 && contentValues.getAsString(ResultsRepository.RESULT1) != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = simpleDateFormat.parse(contentValues.getAsString(ResultsRepository.DATE));
                ResultsRepository resultsRepository = TbrApplication.getInstance().getResultsRepository();
                Result result = new Result();
                result.setBaseEntityId(contentValues.getAsString(ResultsRepository.BASE_ENTITY_ID));
                result.setType(contentValues.getAsString(ResultsRepository.TYPE));
                result.setResult1(contentValues.getAsString(ResultsRepository.RESULT1));
                result.setValue1(contentValues.getAsString(ResultsRepository.VALUE1));
                result.setResult2(contentValues.getAsString(ResultsRepository.RESULT2));
                result.setValue2(contentValues.getAsString(ResultsRepository.VALUE2));
                result.setDate(date);
                result.setAnmId(contentValues.getAsString(ResultsRepository.ANMID));
                result.setLocationId(contentValues.getAsString(ResultsRepository.LOCATIONID));
                result.setSyncStatus(ResultsRepository.TYPE_Unsynced);
                String formSubmissionId = contentValues.getAsString(ResultsRepository.FORMSUBMISSION_ID);
                result.setFormSubmissionId(formSubmissionId);
                resultsRepository.saveResult(result);
                Map<String, String> obs = getObsFromEvent(event);
                ResultDetailsRepository resultDetailsRepository = TbrApplication.getInstance().getResultDetailsRepository();
                resultDetailsRepository.saveClientDetails(formSubmissionId, obs, date.getTime());
            }
            return true;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }

    }

    private ContentValues processCaseModel(JSONObject entity, JSONObject clientClassificationJson) {
        try {
            JSONArray columns = clientClassificationJson.getJSONArray("columns");

            ContentValues contentValues = new ContentValues();

            for (int i = 0; i < columns.length(); i++) {
                JSONObject colObject = columns.getJSONObject(i);
                String columnName = colObject.getString("column_name");
                JSONObject jsonMapping = colObject.getJSONObject("json_mapping");
                String dataSegment = null;
                String fieldName = jsonMapping.getString("field");
                String fieldValue = null;
                String responseKey = null;
                String valueField = jsonMapping.has("value_field") ? jsonMapping.getString("value_field") : null;
                if (fieldName != null && fieldName.contains(".")) {
                    String fieldNameArray[] = fieldName.split("\\.");
                    dataSegment = fieldNameArray[0];
                    fieldName = fieldNameArray[1];
                    fieldValue = jsonMapping.has("concept") ? jsonMapping.getString("concept") : (jsonMapping.has("formSubmissionField") ? jsonMapping.getString("formSubmissionField") : null);
                    if (fieldValue != null) {
                        responseKey = VALUES_KEY;
                    }
                }

                Object jsonDocSegment = null;

                if (dataSegment != null) {
                    //pick data from a specific section of the doc
                    jsonDocSegment = entity.has(dataSegment) ? entity.get(dataSegment) : null;

                } else {
                    //else the use the main doc as the doc segment
                    jsonDocSegment = entity;

                }

                if (jsonDocSegment instanceof JSONArray) {

                    JSONArray jsonDocSegmentArray = (JSONArray) jsonDocSegment;

                    for (int j = 0; j < jsonDocSegmentArray.length(); j++) {
                        JSONObject jsonDocObject = jsonDocSegmentArray.getJSONObject(j);
                        String columnValue = null;
                        if (fieldValue == null) {
                            //this means field_value and response_key are null so pick the value from the json object for the field_name
                            if (jsonDocObject.has(fieldName)) {
                                columnValue = jsonDocObject.getString(fieldName);
                            }
                        } else {
                            //this means field_value and response_key are not null e.g when retrieving some value in the events obs section
                            String expectedFieldValue = jsonDocObject.getString(fieldName);
                            //some events can only be differentiated by the event_type value eg pnc1,pnc2, anc1,anc2

                            if (expectedFieldValue.equalsIgnoreCase(fieldValue)) {
                                if (StringUtils.isNotBlank(valueField) && jsonDocObject.has(valueField)) {
                                    columnValue = jsonDocObject.getString(valueField);
                                } else {
                                    List<String> values = getValues(jsonDocObject.get(responseKey));
                                    if (!values.isEmpty()) {
                                        columnValue = values.get(0);
                                    }
                                }
                            }
                        }
                        // after successfully retrieving the column name and value store it in Content value
                        if (columnValue != null) {
                            if (!jsonDocObject.has(valueField))
                                columnValue = getHumanReadableConceptResponse(columnValue, jsonDocObject);
                            contentValues.put(columnName, columnValue);
                        }
                    }

                } else {
                    //e.g client attributes section
                    String columnValue = null;
                    JSONObject jsonDocSegmentObject = (JSONObject) jsonDocSegment;
                    columnValue = jsonDocSegmentObject.has(fieldName) ? jsonDocSegmentObject.getString(fieldName) : "";
                    // after successfully retrieving the column name and value store it in Content value
                    if (columnValue != null) {
                        columnValue = getHumanReadableConceptResponse(columnValue, jsonDocSegmentObject);
                        contentValues.put(columnName, columnValue);
                    }

                }


            }

            return contentValues;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return null;
    }


    private Map<String, String> getObsFromEvent(JSONObject event) {
        Map<String, String> obs = new HashMap<String, String>();

        try {
            String obsKey = "obs";
            if (event.has(obsKey)) {
                JSONArray obsArray = event.getJSONArray(obsKey);
                if (obsArray != null && obsArray.length() > 0) {
                    for (int i = 0; i < obsArray.length(); i++) {
                        JSONObject object = obsArray.getJSONObject(i);
                        String key = object.has("formSubmissionField") ? object
                                .getString("formSubmissionField") : null;
                        List<String> values =
                                object.has(VALUES_KEY) ? getValues(object.get(VALUES_KEY)) : null;
                        for (String conceptValue : values) {
                            String value = getHumanReadableConceptResponse(conceptValue, object);
                            if (key != null && value != null) {
                                obs.put(key, value);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return obs;
    }

}
