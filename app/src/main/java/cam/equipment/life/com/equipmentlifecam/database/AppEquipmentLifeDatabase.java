package cam.equipment.life.com.equipmentlifecam.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.util.Log;

import cam.equipment.life.com.equipmentlifecam.dao.EquipmentDao;
import cam.equipment.life.com.equipmentlifecam.dao.ProfileDao;
import cam.equipment.life.com.equipmentlifecam.model.Equipment;
import cam.equipment.life.com.equipmentlifecam.model.Profile;
import cam.equipment.life.com.equipmentlifecam.typeconverter.DateConverter;
import cam.equipment.life.com.equipmentlifecam.typeconverter.ListConverter;

@Database(entities = {Equipment.class, Profile.class}, version = 2, exportSchema = false)
@TypeConverters({ListConverter.class, DateConverter.class})
public abstract class AppEquipmentLifeDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppEquipmentLifeDatabase.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "equipmentlifedb";
    private static AppEquipmentLifeDatabase sInstance;

    public static AppEquipmentLifeDatabase getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance..");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppEquipmentLifeDatabase.class, AppEquipmentLifeDatabase.DATABASE_NAME)
                        .addMigrations(MIGRATION_1_2)
                        .build();
            }
        }

        Log.d(LOG_TAG, "Getting the database instance");

        return sInstance;
    }

    // Migration from version 1 to version 2 to add email column in profile table
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE profile "
                    + " ADD COLUMN email TEXT");
        }
    };

    public abstract EquipmentDao equipmentDao();

    public abstract ProfileDao profileDao();

}
