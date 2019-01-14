package cam.equipment.life.com.equipmentlifecam.viewmodel.factory;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.viewmodel.EquipmentDetailViewModel;

public class EquipmentViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppEquipmentLifeDatabase mDb;
    private final int mEquipmentId;

    public EquipmentViewModelFactory(AppEquipmentLifeDatabase database, int mEquipmentId) {
        this.mDb = database;
        this.mEquipmentId = mEquipmentId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EquipmentDetailViewModel(mDb, mEquipmentId);
    }
}
