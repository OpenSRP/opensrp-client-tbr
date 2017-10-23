package org.smartregister.tbr.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfigurableViewsRepository extends BaseRepository {
    private static final String TAG = ConfigurableViewsRepository.class.getCanonicalName();
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String TABLE_NAME = "configurable_views";
    private static final String ID = "view_id";
    private static final String IDENTIFIER = "identifier";
    private static final String SERVER_VERSION = "serverVersion";
    private static final String JSON = "json";
    private static final String DATE_CREATED = "date_created";
    private static final String DATE_UPDATED = "date_updated";

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            IDENTIFIER + "  VARCHAR NOT NULL,status VARCHAR NULL, " +
            SERVER_VERSION + "  Integer NULL," +
            JSON + "  VARCHAR NULL," +
            DATE_CREATED + "  DATETIME NULL," +
            DATE_UPDATED + "  TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP )";

    private static final String INDEX_ID = "CREATE INDEX " + TABLE_NAME + "_" + ID +
            "_index ON " + TABLE_NAME + "(" + ID + " COLLATE NOCASE);";

    private static final String INDEX_IDENTIFIER = "CREATE INDEX " + TABLE_NAME + "_" + IDENTIFIER +
            "_index ON " + TABLE_NAME + "(" + IDENTIFIER + " COLLATE NOCASE);";

    private static final String INDEX_SERVER_VERSION = "CREATE INDEX " + TABLE_NAME + "_" + SERVER_VERSION +
            "_index ON " + TABLE_NAME + "(" + SERVER_VERSION + " COLLATE NOCASE);";

    public ConfigurableViewsRepository(Repository repository) {
        super(repository);
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_ID);
        database.execSQL(INDEX_IDENTIFIER);
        database.execSQL(INDEX_SERVER_VERSION);
    }

    public void saveConfigurableViews(JSONArray jsonArray) {
        try {
            getWritableDatabase().beginTransaction();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String identifier = jsonObject.getString(IDENTIFIER);

                ContentValues values = new ContentValues();
                values.put(SERVER_VERSION, jsonObject.getLong(SERVER_VERSION));
                values.put(JSON, jsonObject.toString());
                if (configurableViewExists(identifier)) {
                    values.put(DATE_UPDATED, dateFormat.format(new Date()));
                    getWritableDatabase().update(TABLE_NAME, values, IDENTIFIER + " = ?", new String[]{identifier});
                } else {
                    values.put(IDENTIFIER, identifier);
                    getWritableDatabase().insert(TABLE_NAME, null, values);
                }
            }
            getWritableDatabase().setTransactionSuccessful();
        } catch (JSONException e) {
            Log.e(TAG, "error saving Configurable view");
        } finally {
            getWritableDatabase().endTransaction();
        }
    }

    private boolean configurableViewExists(String identifier) {
        boolean exists = false;
        Cursor c = getReadableDatabase().rawQuery("Select count(*) from " + TABLE_NAME + " Where " +
                        IDENTIFIER + " = ? ",
                new String[]{identifier});
        if (c.getCount() > 0)
            exists = true;
        c.close();
        return exists;

    }

    public String getConfigurableViewJson(String identifier) {
        String jsonString;
        Cursor cursor = getReadableDatabase().query(TABLE_NAME, new String[]{JSON}, IDENTIFIER + " = ?", new String[]{identifier}, null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            return null;
        } else {
            cursor.moveToFirst();
            jsonString = cursor.getString(0);
        }
        cursor.close();
        return jsonString;
    }

}
