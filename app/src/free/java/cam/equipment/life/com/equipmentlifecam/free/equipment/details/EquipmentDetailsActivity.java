package cam.equipment.life.com.equipmentlifecam.free.equipment.details;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.free.equipment.edit.EquipmentDetailsEditActivity;
import cam.equipment.life.com.equipmentlifecam.free.session.SessionManager;
import cam.equipment.life.com.equipmentlifecam.listeners.OnPostEquipmentTaskListener;
import cam.equipment.life.com.equipmentlifecam.model.Equipment;
import cam.equipment.life.com.equipmentlifecam.utils.EquipmentDetailsAsyncTask;
import cam.equipment.life.com.equipmentlifecam.utils.Utils;

public class EquipmentDetailsActivity extends AppCompatActivity implements OnPostEquipmentTaskListener {

    private static final String TAG = EquipmentDetailsActivity.class.getSimpleName();

    @BindView(R.id.imageView_equipment_details_picture) ImageView imageViewEquipmentDetailsPicture;

    @BindView(R.id.tv_equipment_brand) TextView textViewEquipmentBrandDetails;

    @BindView(R.id.tv_equipment_model) TextView textViewEquipmentModelDetails;

    @BindView(R.id.tv_equipment_serial_number) TextView textViewEquipmentSerialNumberDetails;

    @BindView(R.id.tv_equipment_registration_date) TextView textViewEquipmentRegisteredDateDetails;

    @BindView(R.id.tv_equipment_owner) TextView textViewEquipmentOwnerDetails;

    @BindView(R.id.tv_equipment_status) TextView textViewEquipmentStatusDetails;

    @BindView(R.id.tv_equipment_short_description) TextView textViewEquipmentShortDescriptionDetails;

    @BindView(R.id.layout_equipment_status_details) LinearLayout layoutEquipmentStatusDetails;

    @BindView(R.id.image_view_equipment_status_icon) ImageView imageViewEquipmentStatusIcon;

    @BindView(R.id.image_btn_equipment_details_edit) ImageButton imageButtonEquipmentDetailsEdit;

    private Equipment equipmentInfo;

    public static final String HAS_EDITED_EQUIPMENT = "hasEditedInfo";

    // Member variable for the database
    private AppEquipmentLifeDatabase mDb;

    // To put a flag for details edition
    private SessionManager session;

    private EquipmentDetailsAsyncTask equipmentDetailsAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_list_details);

        ButterKnife.bind(this);

        session = new SessionManager(getApplicationContext());

        mDb = AppEquipmentLifeDatabase.getsInstance(getApplicationContext());

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {

            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

                equipmentInfo = (Equipment) intentThatStartedThisActivity.getSerializableExtra(Intent.EXTRA_TEXT);

                setDetailsFields(equipmentInfo);

            }

        }

        // Using anonymous View.OnClickListener overriding onClick method:
        // Replaced by lambda
        imageButtonEquipmentDetailsEdit.setOnClickListener(view -> onEditButtonClicked());

    }

    private void onEditButtonClicked() {

        Context context = this;
        Class destinationClass = EquipmentDetailsEditActivity.class;

        Intent intentToStartDetailsEditActivity = new Intent(context, destinationClass);
        intentToStartDetailsEditActivity.putExtra(Intent.EXTRA_TEXT, equipmentInfo);
        startActivity(intentToStartDetailsEditActivity);

    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean hasEditedInfo = session.pref.getBoolean(HAS_EDITED_EQUIPMENT, false);

        if (hasEditedInfo) {;

            equipmentDetailsAsyncTask = new EquipmentDetailsAsyncTask(this, mDb, equipmentInfo.getId());
            equipmentDetailsAsyncTask.execute();

        }

    }

    private void setDetailsFields(Equipment equipment) {
        Log.i(TAG, "setDetailsFields() inside method");

        textViewEquipmentBrandDetails.setText(equipment.getBrand());
        textViewEquipmentModelDetails.setText(equipment.getModel());
        textViewEquipmentSerialNumberDetails.setText(equipment.getSerialNumber());
        textViewEquipmentRegisteredDateDetails.setText(Utils.getFormattedDate(equipment.getRegistrationDate()));
        textViewEquipmentOwnerDetails.setText(equipment.getOwner());

        if (equipment.getStatus().equals(getResources().getString(R.string.equipment_owned))) {

            textViewEquipmentStatusDetails.setText(getResources().getString(R.string.string_equipment_owned));
            textViewEquipmentStatusDetails.setTextColor(ContextCompat.getColor(this, R.color.dark_green));
            layoutEquipmentStatusDetails.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_corners_equipment_status_green));
            imageViewEquipmentStatusIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_ok));

        } else if (equipment.getStatus().equals(getResources().getString(R.string.equipment_sold))) {

            textViewEquipmentStatusDetails.setText(getResources().getString(R.string.string_equipment_sold));
            textViewEquipmentStatusDetails.setTextColor(ContextCompat.getColor(this, R.color.theme_primary));
            layoutEquipmentStatusDetails.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_corners_equipment_status_blue));
            imageViewEquipmentStatusIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_info));

        } else if (equipment.getStatus().equals(getResources().getString(R.string.equipment_stolen))) {

            textViewEquipmentStatusDetails.setText(getResources().getString(R.string.string_equipment_stolen));
            textViewEquipmentStatusDetails.setTextColor(ContextCompat.getColor(this, R.color.dark_red));
            layoutEquipmentStatusDetails.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_corners_equipment_status_red));
            imageViewEquipmentStatusIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_not_ok));

        }

        textViewEquipmentShortDescriptionDetails.setText(equipment.getShortDescription());

        Uri imageUrl = null;

        if ((equipment.getPicture() != null)) {

            if ((!equipment.getPicture().isEmpty()) || (equipment.getPicture() != "")) {

                imageUrl = Utils.buildImageUrl(equipment.getPicture());
            }

        }

        if ((imageUrl != null)) {

            Glide.with(getApplicationContext())
                    .load(imageUrl)
                    .into(imageViewEquipmentDetailsPicture);


        } else {

            imageViewEquipmentDetailsPicture.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.album_icon));

        }


    }

    @Override
    public void onTaskCompleted(Equipment equipment) {

        setDetailsFields(equipment);

    }
}
