package cam.equipment.life.com.equipmentlifecam.utils;

import android.os.AsyncTask;

import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.listeners.OnPostEquipmentTaskListener;
import cam.equipment.life.com.equipmentlifecam.model.Equipment;

public class EquipmentDetailsAsyncTask extends AsyncTask<String, Void, Equipment> {

    private Equipment equipmentInfo;

    private final OnPostEquipmentTaskListener mOnPostTaskListener;

    private final AppEquipmentLifeDatabase mDb;

    private final int equipmentId;

    public EquipmentDetailsAsyncTask(OnPostEquipmentTaskListener onPostTaskListener,
                                     AppEquipmentLifeDatabase mDb, int equipmentId) {

        this.mDb = mDb;
        this.mOnPostTaskListener = onPostTaskListener;
        this.equipmentId = equipmentId;
    }

    @Override
    protected Equipment doInBackground(String... strings) {

        equipmentInfo = new Equipment();

        equipmentInfo = mDb.equipmentDao().fetchEquipmentById(equipmentId);

        return equipmentInfo;
    }

    @Override
    protected void onPostExecute(Equipment equipment) {

        // The data is passed to the Activity class through here
        mOnPostTaskListener.onTaskCompleted(equipment);

    }
}
