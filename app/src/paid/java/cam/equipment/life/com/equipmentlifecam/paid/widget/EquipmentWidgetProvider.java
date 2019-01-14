package cam.equipment.life.com.equipmentlifecam.paid.widget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.paid.initial.InitialScreenActivity;
import cam.equipment.life.com.equipmentlifecam.paid.main.MainCamActivity;

/**
 * Implementation of App Widget functionality.
 */
public class EquipmentWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_TOAST = "widget_toast";
    public static final String EXTRA_STRING = "extra_string";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = initViews(context, appWidgetManager, appWidgetId);

            // Adding collection list item handler
            final Intent onItemClick = new Intent(context, EquipmentWidgetProvider.class);

            onItemClick.setAction(ACTION_TOAST);

            onItemClick.setData(Uri.parse(onItemClick.toUri(Intent.URI_INTENT_SCHEME)));

            final PendingIntent onClickPendingIntent = PendingIntent
                    .getBroadcast(context, 0, onItemClick, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setPendingIntentTemplate(R.id.widgetCollectionList, onClickPendingIntent);

            // Adding header click listener
            Intent intent = new Intent(context, MainCamActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            views.setOnClickPendingIntent(R.id.widgetImgLauncher, pendingIntent);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetCollectionList);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    // Now handling click event in WidgetProvider -> onReceive:
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ACTION_TOAST)) {

            String item = intent.getExtras().getString(EXTRA_STRING);

            Toast.makeText(context, item, Toast.LENGTH_LONG).show();

        }

        super.onReceive(context, intent);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private RemoteViews initViews(Context context, AppWidgetManager widgetManager, int widgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_equipment_list);

        Intent intent = new Intent(context, EquipmentWidgetService.class);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        views.setRemoteAdapter(widgetId, R.id.widgetCollectionList, intent);

        return views;
    }

}

