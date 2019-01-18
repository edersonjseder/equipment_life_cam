package cam.equipment.life.com.equipmentlifecam.utils;

import android.os.AsyncTask;

import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.listeners.OnPostProfileTaskListener;
import cam.equipment.life.com.equipmentlifecam.model.Profile;

public class ProfileDetailsAsyncTask extends AsyncTask<String, Void, Profile> {

    private Profile profile;

    private final OnPostProfileTaskListener mOnPostTaskListener;

    private final AppEquipmentLifeDatabase mDb;

    private final String profileEmail;

    public ProfileDetailsAsyncTask(OnPostProfileTaskListener onPostTaskListener,
                                   AppEquipmentLifeDatabase mDb, String profileEmail) {

        this.mDb = mDb;
        this.mOnPostTaskListener = onPostTaskListener;
        this.profileEmail = profileEmail;
    }

    @Override
    protected Profile doInBackground(String... strings) {

        profile = new Profile();

        profile = mDb.profileDao().fetchProfileByEmail(profileEmail);

        return profile;
    }

    @Override
    protected void onPostExecute(Profile profile) {

        // The data is passed to the Activity class through here
        mOnPostTaskListener.onTaskCompleted(profile);

    }
}
