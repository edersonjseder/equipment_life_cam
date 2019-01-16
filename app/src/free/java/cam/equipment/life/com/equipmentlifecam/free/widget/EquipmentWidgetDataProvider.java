package cam.equipment.life.com.equipmentlifecam.free.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.listeners.OnPostEquipmentListTaskListener;
import cam.equipment.life.com.equipmentlifecam.model.Equipment;
import cam.equipment.life.com.equipmentlifecam.utils.EquipmentListAsyncTask;

import static cam.equipment.life.com.equipmentlifecam.free.widget.EquipmentWidgetProvider.ACTION_TOAST;
import static cam.equipment.life.com.equipmentlifecam.free.widget.EquipmentWidgetProvider.EXTRA_STRING;

public class EquipmentWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory,
        OnPostEquipmentListTaskListener {

    // Constant for logging
    private static final String TAG = EquipmentWidgetDataProvider.class.getSimpleName();

    private List<Equipment> equipmentList = new ArrayList<>();
    private Context context;

    // Member variable for the database
    private AppEquipmentLifeDatabase mDb;


    private EquipmentListAsyncTask equipmentListAsyncTask;

    public EquipmentWidgetDataProvider(Context context, Intent intent) {

        this.context = context;

    }

    private void initData() {

        mDb = AppEquipmentLifeDatabase.getsInstance(context.getApplicationContext());

        equipmentListAsyncTask = new EquipmentListAsyncTask(this, mDb);
        equipmentListAsyncTask.execute();

    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {

        mDb = AppEquipmentLifeDatabase.getsInstance(context.getApplicationContext());

        equipmentListAsyncTask = new EquipmentListAsyncTask(this, mDb);
        equipmentListAsyncTask.execute();

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return equipmentList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.i(TAG, "getViewAt() inside method");

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_equipment_list_item);

        views.setTextViewText(R.id.appwidget_text_item, equipmentList.get(position).getBrand());
        views.setTextColor(android.R.id.text1, Color.BLACK);

        final Intent fillInIntent = new Intent();

        fillInIntent.setAction(ACTION_TOAST);

        final Bundle bundle = new Bundle();

        bundle.putString(EXTRA_STRING, equipmentList.get(position).getBrand());

        fillInIntent.putExtras(bundle);

        views.setOnClickFillInIntent(android.R.id.text1, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void setEquipmentList(List<Equipment> equipmentList) {
        this.equipmentList = equipmentList;
    }

    @Override
    public void onTaskCompleted(List<Equipment> equipments) {

        setEquipmentList(equipments);

    }
}
