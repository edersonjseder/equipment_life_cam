package cam.equipment.life.com.equipmentlifecam.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import cam.equipment.life.com.equipmentlifecam.model.Profile;

@Dao
public interface ProfileDao {

    @Query("SELECT * FROM profile ORDER BY cpf")
    LiveData<List<Profile>> loadAllProfiles();

    @Insert
    void insertProfile(Profile profile);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateProfile(Profile profile);

    @Delete
    void deleteProfile(Profile profile);

    @Query("SELECT * FROM profile WHERE email = :email")
    LiveData<Profile> loadProfileByEmail(String email);

    @Query("SELECT * FROM profile WHERE email = :email")
    Profile fetchProfileByEmail(String email);

}
