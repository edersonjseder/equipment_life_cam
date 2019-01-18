package cam.equipment.life.com.equipmentlifecam.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.model.Equipment;


public class EquipmentListWithQueryViewModel extends AndroidViewModel {
    // Constant for logging
    private static final String TAG = EquipmentListWithQueryViewModel.class.getSimpleName();

    private LiveData<List<Equipment>> equipments;
    private final AppEquipmentLifeDatabase database;

    public EquipmentListWithQueryViewModel(@NonNull Application application) {
        super(application);

        database = AppEquipmentLifeDatabase.getsInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the equipments from the database");

    }

    public LiveData<List<Equipment>> getEquipmentsListWithQuery(String query) {

        equipments = database.equipmentDao().loadEquipmentByContainingSomething(query);

        return equipments;
    }

    public LiveData<List<Equipment>> getEquipmentsListByStatus(String status) {

        equipments = database.equipmentDao().loadEquipmentsByStatus(status);

        return equipments;
    }

}
