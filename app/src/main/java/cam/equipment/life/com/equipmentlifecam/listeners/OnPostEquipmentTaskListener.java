package cam.equipment.life.com.equipmentlifecam.listeners;

import cam.equipment.life.com.equipmentlifecam.model.Equipment;

/**
 * Interface to retrieve the Equipment data from database to show on EquipmentDetailsActivity class
 */
public interface OnPostEquipmentTaskListener {

    void onTaskCompleted(Equipment equipment);

}
