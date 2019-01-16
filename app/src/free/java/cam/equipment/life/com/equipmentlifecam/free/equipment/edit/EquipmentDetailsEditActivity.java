package cam.equipment.life.com.equipmentlifecam.free.equipment.edit;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.executors.AppExecutors;
import cam.equipment.life.com.equipmentlifecam.free.session.SessionManager;
import cam.equipment.life.com.equipmentlifecam.model.Equipment;
import cam.equipment.life.com.equipmentlifecam.utils.Utils;

import static cam.equipment.life.com.equipmentlifecam.free.equipment.details.EquipmentDetailsActivity.HAS_EDITED_EQUIPMENT;

public class EquipmentDetailsEditActivity extends AppCompatActivity {

    private static final String TAG = EquipmentDetailsEditActivity.class.getSimpleName();

    @BindView(R.id.et_equipment_edit_brand) EditText editTextEquipmentEditBrand;

    @BindView(R.id.et_equipment_edit_model) EditText editTextEquipmentEditModel;

    @BindView(R.id.et_equipment_edit_serial_number) EditText editTextEquipmentEditSerialNumber;

    @BindView(R.id.tv_equipment_edit_registration_date) TextView textViewEquipmentEditRegistrationDate;

    @BindView(R.id.tv_equipment_edit_owner) TextView textViewEquipmentEditOwner;

    @BindView(R.id.radioGroupEquipmentEditStatus) RadioGroup checkBoxEquipmentEditStatus;

    @BindView(R.id.et_equipment_edit_picture_file) EditText editTextEquipmentEditUploadPicture;

    @BindView(R.id.et_equipment_edit_short_description) EditText editTextEquipmentEditShortDescription;

    @BindView(R.id.image_btn_equipment_details_edit_save) ImageButton imageButtonEquipmentDetailsEditSave;

    @BindView(R.id.button_choose_equipment_edit_picture) Button btnEquipmentEditChoosePictureUpload;

    @BindView(R.id.button_upload_equipment_edit_picture) ImageButton imageButtonEquipmentEditPictureUpload;

    @BindView(R.id.progressBar_equipment_edit_upload_picture) ProgressBar progressBarEquipmentEditPictureUpload;

    @BindView(R.id.checkbox_upload_image_verify) CheckBox checkBoxUploadImageVerify;

    private ImageView imageViewEquipmentEditPictureToUpload;

    // Member variable for the database
    private AppEquipmentLifeDatabase mDb;

    private Equipment equipment;

    // Constants for priority
    public static final String EQUIPMENT_OWNED = "Owned";
    public static final String EQUIPMENT_SOLD = "Sold";
    public static final String EQUIPMENT_STONED = "Stolen";

    //Image request code
    private int PICK_IMAGE_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;

    //Firebase
    private FirebaseStorage storage;

    private StorageReference storageReference;

    private ContentResolver contentResolver;

    private String urlEquipmentPicture;

    private boolean isLandscapeMode;

    // To put a flag for details edition
    private SessionManager session;

    private boolean hasEditedInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_list_details_edit);
        Log.i(TAG, "onCreate() inside method");

        ButterKnife.bind(this);

        session = new SessionManager(getApplicationContext());

        session.editor.putBoolean(HAS_EDITED_EQUIPMENT, false);
        session.editor.commit();

        hasEditedInfo = session.pref.getBoolean(HAS_EDITED_EQUIPMENT, false);

        // Boolean to verify whether is tablet mode or not
        isLandscapeMode = getResources().getBoolean(R.bool.isLand);

        if (isLandscapeMode) {
            imageViewEquipmentEditPictureToUpload = findViewById(R.id.imageView_equipment_edit_picture_land);
        }

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        contentResolver = getContentResolver();

        mDb = AppEquipmentLifeDatabase.getsInstance(getApplicationContext());

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

                equipment = (Equipment) intentThatStartedThisActivity.getSerializableExtra(Intent.EXTRA_TEXT);

                setDetailsFields(equipment);

            }
        }

        imageButtonEquipmentEditPictureUpload.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_upload_disabled));
        // Disabling picture upload button so the user doesn't press it without upload image
        imageButtonEquipmentEditPictureUpload.setEnabled(false);

        imageButtonEquipmentDetailsEditSave.setOnClickListener(view -> onSaveImageButtonClicked(equipment));
        btnEquipmentEditChoosePictureUpload.setOnClickListener(view -> showFileChooser());
        imageButtonEquipmentEditPictureUpload.setOnClickListener(view -> uploadImage());

        checkBoxUploadImageVerify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    onEnableComponentsToUpload();
                } else {
                    onDisableComponentsToUpload();
                }

            }
        });

    }

    private void onDisableComponentsToUpload() {

        // Enables the button for choosing the equipment picture
        btnEquipmentEditChoosePictureUpload.setEnabled(false);

        editTextEquipmentEditUploadPicture.setEnabled(false);

        imageButtonEquipmentEditPictureUpload.setEnabled(false);
        imageButtonEquipmentEditPictureUpload.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_upload_disabled));

        // Disabling saving button so the user doesn't press it when is uploading the image
        imageButtonEquipmentDetailsEditSave.setEnabled(true);
        imageButtonEquipmentDetailsEditSave.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_toolbar_edit_ok));

    }

    private void onEnableComponentsToUpload() {

        // Enables the button for choosing the equipment picture
        btnEquipmentEditChoosePictureUpload.setEnabled(true);

        // Disabling saving button so the user doesn't press it when is uploading the image
        imageButtonEquipmentDetailsEditSave.setEnabled(false);
        imageButtonEquipmentDetailsEditSave.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_toolbar_edit_ok_disabled));

    }

    private void setDetailsFields(Equipment equipment) {
        Log.i(TAG, "setDetailsFields() inside method");

        setEquipmentStatusInViews(equipment.getStatus());
        editTextEquipmentEditBrand.setText(equipment.getBrand());
        editTextEquipmentEditModel.setText(equipment.getModel());
        editTextEquipmentEditSerialNumber.setText(equipment.getSerialNumber());
        textViewEquipmentEditRegistrationDate.setText(Utils.getFormattedDate(equipment.getRegistrationDate()));
        textViewEquipmentEditOwner.setText(equipment.getOwner());
        editTextEquipmentEditShortDescription.setText(equipment.getShortDescription());

        String picture = Utils.getPlainText(equipment.getPicture());

        editTextEquipmentEditUploadPicture.setText(picture);

        if (isLandscapeMode) {

            if ((!equipment.getPicture().isEmpty()) || (equipment.getPicture() != "")){

                Uri imageUrl = Utils.buildImageUrl(equipment.getPicture());

                Glide.with(getApplicationContext())
                        .load(imageUrl)
                        .into(imageViewEquipmentEditPictureToUpload);

            }

        }

    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveImageButtonClicked(Equipment equipment) {

        populateEditedEntity(equipment);

        hasEditedInfo = true;
        session.editor.putBoolean(HAS_EDITED_EQUIPMENT, hasEditedInfo);
        session.editor.commit();

        // temp solution
        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {

                mDb.equipmentDao().updateEquipment(equipment);

                finish();
            }
        });

    }

    private void populateEditedEntity(Equipment equipment) {
        Log.i(TAG, "populateEditedEntity() inside method");

        equipment.setBrand(editTextEquipmentEditBrand.getText().toString());
        equipment.setModel(editTextEquipmentEditModel.getText().toString());
        equipment.setSerialNumber(editTextEquipmentEditSerialNumber.getText().toString());
        equipment.setOwner(textViewEquipmentEditOwner.getText().toString());
        equipment.setStatus(getEquipmentStatusFromViews());
        equipment.setShortDescription(editTextEquipmentEditShortDescription.getText().toString());

        if (urlEquipmentPicture != null) {

            equipment.setPicture(urlEquipmentPicture);
        }

    }

    /**
     * getEquipmentStatusFromViews is called whenever the selected status needs to be retrieved
     */
    public String getEquipmentStatusFromViews() {

        String status = "";

        int checkedId = checkBoxEquipmentEditStatus.getCheckedRadioButtonId();

        switch (checkedId) {
            case R.id.rb_equipment_edit_status_owned:
                status = getResources().getString(R.string.equipment_owned);
                break;
            case R.id.rb_equipment_edit_status_sold:
                status = getResources().getString(R.string.equipment_sold);
                break;
            case R.id.rb_equipment_edit_status_stolen:
                status = getResources().getString(R.string.equipment_stolen);
        }

        return status;

    }

    /**
     * setEquipmentStatusInViews is called when we receive a task from MainActivity
     *
     * @param priority the priority value
     */
    public void setEquipmentStatusInViews(String priority) {

        switch (priority) {
            case EQUIPMENT_OWNED:
                checkBoxEquipmentEditStatus.check(R.id.rb_equipment_edit_status_owned);
                break;
            case EQUIPMENT_SOLD:
                checkBoxEquipmentEditStatus.check(R.id.rb_equipment_edit_status_sold);
                break;
            case EQUIPMENT_STONED:
                checkBoxEquipmentEditStatus.check(R.id.rb_equipment_edit_status_stolen);
        }

    }

    // 1 - method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // 2 - handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            // Boolean to verify whether is tablet mode or not
            boolean isLandscapeMode = getResources().getBoolean(R.bool.isLand);

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (isLandscapeMode) {

                imageViewEquipmentEditPictureToUpload.setImageBitmap(bitmap);

            }

        }

        if (resultCode == RESULT_OK) {

            editTextEquipmentEditUploadPicture.setText("");
            editTextEquipmentEditUploadPicture.setEnabled(true);
            imageButtonEquipmentEditPictureUpload.setEnabled(true);
            imageButtonEquipmentEditPictureUpload.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_upload));

        }

    }

    private void uploadImage() {

        Log.v(TAG, "Inside method uploadImage()");

        if(filePath != null) {

            StorageReference sRef = storageReference.child(Utils.STORAGE_PATH_UPLOADS +
                    editTextEquipmentEditUploadPicture.getText() + System.currentTimeMillis() +
                    "." + Utils.getFileExtension(filePath, contentResolver));

            UploadTask uploadTask = sRef.putFile(filePath);

            Task<Uri> urlTask = uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBarEquipmentEditPictureUpload.setVisibility(View.VISIBLE);
                    progressBarEquipmentEditPictureUpload.setProgress((int) progress);

                }

            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return sRef.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {

                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();

                        progressBarEquipmentEditPictureUpload.setVisibility(View.INVISIBLE);

                        Toast.makeText(EquipmentDetailsEditActivity.this, getResources().getString(R.string.equipment_upload_image_successful), Toast.LENGTH_LONG).show();

                        if (downloadUri != null) {
                            urlEquipmentPicture = downloadUri.toString();
                            Log.v(TAG, "Equipment Image URI " + urlEquipmentPicture);
                        }

                        imageButtonEquipmentDetailsEditSave.setEnabled(true);
                        imageButtonEquipmentDetailsEditSave.setImageDrawable(ContextCompat.getDrawable(EquipmentDetailsEditActivity.this, R.drawable.icon_toolbar_edit_ok));

                    } else {

                        urlEquipmentPicture = "";

                    }
                }

            });

        }

    }

}
