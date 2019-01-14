package cam.equipment.life.com.equipmentlifecam.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.model.Equipment;

public class EquipmentDetailViewModel extends ViewModel {

    private LiveData<Equipment> equipment;

    public EquipmentDetailViewModel(AppEquipmentLifeDatabase appDatabase, int equipmentId) {
        equipment = appDatabase.equipmentDao().loadEquipmentById(equipmentId);
    }

    public LiveData<Equipment> getEquipment() {
        return equipment;
    }

}
