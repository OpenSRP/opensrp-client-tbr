package org.smartregister.tbr.jsonspec;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.avocarrot.json2view.DynamicView;

import org.json.JSONObject;
import org.smartregister.tbr.jsonspec.model.View;
import org.smartregister.tbr.jsonspec.model.ViewConfiguration;
import org.smartregister.tbr.repository.ConfigurableViewsRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by samuelgithengi on 11/21/17.
 */

public class ConfigurableViewsHelper {

    private static final String TAG = "ConfigurableViewsHelper";

    private final ConfigurableViewsRepository configurableViewsRepository;

    private final JsonSpecHelper jsonSpecHelper;

    private final Context context;

    public ConfigurableViewsHelper(ConfigurableViewsRepository configurableViewsRepository, JsonSpecHelper jsonSpecHelper, Context context) {
        this.configurableViewsRepository = configurableViewsRepository;
        this.jsonSpecHelper = jsonSpecHelper;
        this.context = context;
    }

    private final Map<String, ViewConfiguration> viewConfigurations = new ConcurrentHashMap<>();

    public void registerViewConfigurations(List<String> viewIdentifiers) {
        for (String viewIdentifier : viewIdentifiers) {
            String jsonString = configurableViewsRepository.getConfigurableViewJson(viewIdentifier);
            if (jsonString == null)
                continue;
            else
                viewConfigurations.put(viewIdentifier, jsonSpecHelper.getConfigurableView(jsonString));
        }
    }

    public Set<View> getRegisterActiveColumns(String identifier) {
        Set<View> visibleColumns = new TreeSet<>(new ViewPositionComparator());
        for (View view : viewConfigurations.get(identifier).getViews()) {
            if (view.isVisible())
                visibleColumns.add(view);
        }
        return visibleColumns;
    }

    public void processRegisterColumns(Map<String, Integer> columnMapping, android.view.View view, Set<View> visibleColumns, int parentComponent) {
        //Dont process  for more than 3 columns
        if (visibleColumns.size() > 3) return;
        List<android.view.View> columns = new LinkedList<>();
        for (View columnView : visibleColumns) {
            try {
                android.view.View column = view.findViewById(columnMapping.get(columnView.getIdentifier()));
                if (columnView.getResidence().getLayoutWeight() != null) {
                    LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) column.getLayoutParams();
                    param.weight = Float.valueOf(columnView.getResidence().getLayoutWeight());
                    column.setLayoutParams(param);
                }
                column.setVisibility(android.view.View.VISIBLE);
                columns.add(column);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        ViewGroup allColumns = (ViewGroup) view.findViewById(parentComponent);
        allColumns.removeAllViews();
        for (android.view.View column : columns)
            allColumns.addView(column);

    }

    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        for (String viewIdentifier : viewIdentifiers)
            viewConfigurations.remove(viewIdentifier);
    }

    public ViewConfiguration getViewConfiguration(String identifier) {
        return viewConfigurations.get(identifier);
    }

    public android.view.View inflateDynamicView(ViewConfiguration viewConfiguration, ViewConfiguration commonConfiguration, int parentViewId, boolean isHeader) {
        JSONObject jsonView = new JSONObject(viewConfiguration.getJsonView());
        android.view.View view = DynamicView.createView(context, jsonView);
        jsonView = new JSONObject(commonConfiguration.getJsonView());
        android.view.View commonRegisterColumns = DynamicView.createView(context, jsonView);
        ViewGroup registerColumns = (ViewGroup) view.findViewById(parentViewId);
        ViewGroup commonColumns = (ViewGroup) commonRegisterColumns.findViewById(parentViewId);
        while (registerColumns.getChildCount() > 0) {
            android.view.View column = registerColumns.getChildAt(0);
            registerColumns.removeView(column);
            commonColumns.addView(column);
        }
        if (isHeader)
            commonColumns.setLayoutParams(
                    new AbsListView.LayoutParams(
                            AbsListView.LayoutParams.MATCH_PARENT,
                            AbsListView.LayoutParams.MATCH_PARENT));
        else
            commonColumns.setLayoutParams(
                    new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT));
        return commonColumns;
    }

}
