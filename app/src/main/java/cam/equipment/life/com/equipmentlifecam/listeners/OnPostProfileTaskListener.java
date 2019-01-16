package cam.equipment.life.com.equipmentlifecam.listeners;

import cam.equipment.life.com.equipmentlifecam.model.Profile;

/**
 * Interface to retrieve the Profile data from database to show on ProfileDetailsActivity class
 */
public interface OnPostProfileTaskListener {

    void onTaskCompleted(Profile profile);

}
