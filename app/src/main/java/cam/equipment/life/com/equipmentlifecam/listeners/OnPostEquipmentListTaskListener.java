package cam.equipment.life.com.equipmentlifecam.listeners;

import java.util.List;

import cam.equipment.life.com.equipmentlifecam.model.Equipment;

/**
 * Interface to retrieve the Equipment list from database to show on MainCamActivity class
 */
public interface OnPostEquipmentListTaskListener {

    void onTaskCompleted(List<Equipment> equipments);

}
