package cam.equipment.life.com.equipmentlifecam.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.model.Equipment;

public class EquipmentViewModel extends AndroidViewModel {
    // Constant for logging
    private static final String TAG = EquipmentViewModel.class.getSimpleName();

    private LiveData<List<Equipment>> equipments;

    public EquipmentViewModel(@NonNull Application application) {
        super(application);

        AppEquipmentLifeDatabase database = AppEquipmentLifeDatabase.getsInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the equipments from the database");
        equipments = database.equipmentDao().loadAllEquipments();

    }

    public LiveData<List<Equipment>> getAllEquipments() {

        return equipments;
    }

}
