package cam.equipment.life.com.equipmentlifecam.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.model.Profile;

public class ProfileDetailViewModel extends ViewModel {

    private LiveData<Profile> profile;

    public ProfileDetailViewModel(AppEquipmentLifeDatabase appDatabase, String profileEmail) {
        profile = appDatabase.profileDao().loadProfileByEmail(profileEmail);
    }

    public LiveData<Profile> getProfile() {
        return profile;
    }

}
