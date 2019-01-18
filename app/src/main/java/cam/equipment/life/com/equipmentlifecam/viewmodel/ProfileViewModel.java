package cam.equipment.life.com.equipmentlifecam.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cam.equipment.life.com.equipmentlifecam.dao.ProfileDao;
import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.model.Profile;

class ProfileViewModel extends AndroidViewModel {
    // Constant for logging
    private static final String TAG = ProfileViewModel.class.getSimpleName();

    private final ExecutorService executorService;

    private final ProfileDao profileDao;

    public ProfileViewModel(@NonNull Application application) {
        super(application);

        profileDao = AppEquipmentLifeDatabase.getsInstance(this.getApplication()).profileDao();
        executorService = Executors.newSingleThreadExecutor();

    }


    public void saveProfile(Profile profile) {
        executorService.execute(() -> profileDao.insertProfile(profile));
    }

    public void deleteProfile(Profile profile) {
        executorService.execute(() -> profileDao.deleteProfile(profile));
    }

}
