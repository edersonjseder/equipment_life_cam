package cam.equipment.life.com.equipmentlifecam.utils;

import android.os.AsyncTask;

import java.util.List;

import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.listeners.OnPostEquipmentListTaskListener;
import cam.equipment.life.com.equipmentlifecam.model.Equipment;

public class EquipmentListAsyncTask extends AsyncTask<Void, Void, List<Equipment>> {

    private List<Equipment> equipmentList;

    private final OnPostEquipmentListTaskListener mOnPostListTaskListener;

    private final AppEquipmentLifeDatabase mDb;

    public EquipmentListAsyncTask(OnPostEquipmentListTaskListener mOnPostListTaskListener,
                                  AppEquipmentLifeDatabase mDb) {

        this.mOnPostListTaskListener = mOnPostListTaskListener;
        this.mDb = mDb;

    }

    @Override
    protected List<Equipment> doInBackground(Void... voids) {

        equipmentList = mDb.equipmentDao().fetchAllEquipments();

        return equipmentList;

    }

    @Override
    protected void onPostExecute(List<Equipment> equipments) {

        mOnPostListTaskListener.onTaskCompleted(equipments);

    }
}
