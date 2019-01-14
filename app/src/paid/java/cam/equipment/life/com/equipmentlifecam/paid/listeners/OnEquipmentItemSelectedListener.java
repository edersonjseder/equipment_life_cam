package cam.equipment.life.com.equipmentlifecam.paid.listeners;

import cam.equipment.life.com.equipmentlifecam.model.Equipment;

public interface OnEquipmentItemSelectedListener {

    // called when user selects a Equipment from the list
    void onEquipmentItemSelected(Equipment equipment, int position);

}
