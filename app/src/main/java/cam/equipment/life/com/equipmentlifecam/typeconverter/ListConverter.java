package cam.equipment.life.com.equipmentlifecam.typeconverter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import cam.equipment.life.com.equipmentlifecam.model.Equipment;

public class ListConverter {

    @TypeConverter
    public String fromEquipmentValuesList(List<Equipment> equipmentList) {

        if (equipmentList == null) {
            return (null);
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<Equipment>>() {}.getType();
        String json = gson.toJson(equipmentList, type);

        return json;

    }

    @TypeConverter
    public List<Equipment> toEquipmentValuesList(String listEquipmentsString) {

        if (listEquipmentsString == null) {
            return (null);
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<Equipment>>() {}.getType();
        List<Equipment> integerList = gson.fromJson(listEquipmentsString, type);

        return integerList;

    }

}
