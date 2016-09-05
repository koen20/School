package com.koenhabets.school;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.widget.RemoteViews;

import com.koenhabets.school.api.GradesRequest;

/**
 * Created by koenh on 9/5/2016.
 */
public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setTextViewText(R.id.textView_widget, GradesRequest.getLooset());

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
