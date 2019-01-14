package cam.equipment.life.com.equipmentlifecam.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import cam.equipment.life.com.equipmentlifecam.model.Equipment;

@Dao
public interface EquipmentDao {

    @Query("SELECT * FROM equipment")
    LiveData<List<Equipment>> loadAllEquipments();

    @Query("SELECT * FROM equipment")
    List<Equipment> fetchAllEquipments();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertEquipment(Equipment equipment);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEquipment(Equipment equipment);

    @Delete
    void deleteEquipment(Equipment equipment);

    @Query("SELECT * FROM equipment WHERE id = :id")
    LiveData<Equipment> loadEquipmentById(int id);

    @Query("SELECT * FROM equipment WHERE id = :id")
    Equipment fetchEquipmentById(int id);

    @Query("SELECT * FROM equipment WHERE status = :status")
    LiveData<List<Equipment>> loadEquipmentsByStatus(String status);

    @Query("SELECT * FROM equipment WHERE brand LIKE :brand")
    LiveData<List<Equipment>> loadEquipmentByContainingSomething(String brand);

}
