package cam.equipment.life.com.equipmentlifecam.viewmodel.factory;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.viewmodel.ProfileDetailViewModel;

public class ProfileViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppEquipmentLifeDatabase mDb;
    private final String mProfileEmail;

    public ProfileViewModelFactory(AppEquipmentLifeDatabase database, String mProfileEmail) {
        this.mDb = database;
        this.mProfileEmail = mProfileEmail;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ProfileDetailViewModel(mDb, mProfileEmail);
    }
}
