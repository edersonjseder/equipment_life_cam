package cam.equipment.life.com.equipmentlifecam.paid.equipment.registration;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.executors.AppExecutors;
import cam.equipment.life.com.equipmentlifecam.model.Equipment;
import cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager;
import cam.equipment.life.com.equipmentlifecam.utils.Utils;

import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.KEY_NAME;

public class EquipmentRegistrationActivity extends AppCompatActivity {

    private static final String TAG = EquipmentRegistrationActivity.class.getSimpleName();

    public static final String FLAG_LIMIT = "limitInsertions";

    @BindView(R.id.et_equipment_regist_brand) EditText editViewEquipmentBrand;

    @BindView(R.id.et_equipment_regist_model) EditText editViewEquipmentModel;

    @BindView(R.id.et_equipment_regist_serial_number) EditText editViewEquipmentSerialNumber;

    @BindView(R.id.et_equipment_owner) TextView textViewEquipmentOwner;

    @BindView(R.id.tv_equipment_registration_date_text) TextView textViewEquipmentRegistrationDate;

    @BindView(R.id.radioGroupEquipmentRegistrationStatus) RadioGroup radioGroupEquipmentStatus;

    @BindView(R.id.et_equipment_regist_picture_file) EditText editTextEquipmentPicture;

    @BindView(R.id.et_equipment_regist_short_description) EditText editTextEquipmentShortDescription;

    @BindView(R.id.iv_equipment_registration_save) ImageButton imageButtonEquipmentRegistSave;

    @BindView(R.id.button_upload_equipment_registration_picture) ImageButton imageButtonEquipmentRegistPictureUpload;

    @BindView(R.id.progressBar_equipment_registration_upload_picture) ProgressBar progressBarEquipmentRegistPictureUpload;

    @BindView(R.id.button_choose_equipment_registration_picture) Button btnEquipmentRegistChoosePictureUpload;

    @BindView(R.id.checkbox_registration_upload_image_verify) CheckBox checkBoxRegistrationUploadImageVerify;

    private ImageView imageViewEquipmentRegistrationPictureToUpload;

    // Member variable for the database
    private AppEquipmentLifeDatabase mDb;

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

    // To get user credentials
    private SessionManager session;

    private String ownerName;

    private boolean isLandscapeMode;

    private Date dateRegistered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_registration);
        Log.i(TAG, "onCreate() inside method");

        ButterKnife.bind(this);

        dateRegistered = new Date();

        session = new SessionManager(getApplicationContext());

        ownerName = session.pref.getString(KEY_NAME, "");

        // Boolean to verify whether is tablet mode or not
        isLandscapeMode = getResources().getBoolean(R.bool.isLand);

        if (isLandscapeMode) {
            imageViewEquipmentRegistrationPictureToUpload = findViewById(R.id.imageView_equipment_registration_picture_land);
        }

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        contentResolver = getContentResolver();

        imageButtonEquipmentRegistPictureUpload.setEnabled(false);
        imageButtonEquipmentRegistPictureUpload.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_upload_disabled));

        //Requesting storage permission
        requestStoragePermission();

        mDb = AppEquipmentLifeDatabase.getsInstance(getApplicationContext());

        textViewEquipmentOwner.setText(ownerName);
        textViewEquipmentRegistrationDate.setText(Utils.getFormattedDate(dateRegistered));

        // Using anonymous View.OnClickListener overriding onClick method:
        // Replaced by lambda
        imageButtonEquipmentRegistSave.setOnClickListener(view -> onSaveButtonClicked());
        btnEquipmentRegistChoosePictureUpload.setOnClickListener(view -> showFileChooser());
        imageButtonEquipmentRegistPictureUpload.setOnClickListener(view -> uploadImage());

        checkBoxRegistrationUploadImageVerify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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
        btnEquipmentRegistChoosePictureUpload.setEnabled(false);

        editTextEquipmentPicture.setEnabled(false);

        imageButtonEquipmentRegistPictureUpload.setEnabled(false);
        imageButtonEquipmentRegistPictureUpload.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_upload_disabled));

        // Disabling saving button so the user doesn't press it when is uploading the image
        imageButtonEquipmentRegistSave.setEnabled(true);
        imageButtonEquipmentRegistSave.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_toolbar_edit_ok));

    }

    private void onEnableComponentsToUpload() {

        // Enables the button for choosing the equipment picture
        btnEquipmentRegistChoosePictureUpload.setEnabled(true);

        // Disabling saving button so the user doesn't press it when is uploading the image
        imageButtonEquipmentRegistSave.setEnabled(false);
        imageButtonEquipmentRegistSave.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_toolbar_edit_ok_disabled));

    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new equipment data into the underlying database.
     */
    public void onSaveButtonClicked() {
        Log.i(TAG, "onSaveButtonClicked() inside method");

        final Equipment equipment = new Equipment();

        populateEntity(equipment);

        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {

                mDb.equipmentDao().insertEquipment(equipment);

                finish();
            }
        });

    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, getResources().getString(R.string.equipment_registration_storage_permission_request_granted), Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, getResources().getString(R.string.equipment_registration_storage_permission_request_not_granted), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void populateEntity(Equipment equipment) {

        Log.v(TAG, "Inside method populateEntity()");

        String status = getEquipmentStatusFromViews();

        equipment.setBrand(editViewEquipmentBrand.getText().toString());
        equipment.setModel(editViewEquipmentModel.getText().toString());
        equipment.setSerialNumber(editViewEquipmentSerialNumber.getText().toString());
        equipment.setOwner(textViewEquipmentOwner.getText().toString());
        equipment.setRegistrationDate(dateRegistered);
        equipment.setStatus(status);
        equipment.setShortDescription(editTextEquipmentShortDescription.getText().toString());

        if (urlEquipmentPicture != null) {

            equipment.setPicture(urlEquipmentPicture);

        } else {

            equipment.setPicture("");

        }

    }

    /**
     * getEquipmentStatusFromViews is called whenever the selected status needs to be retrieved
     */
    public String getEquipmentStatusFromViews() {

        String status = "";

        int checkedId = radioGroupEquipmentStatus.getCheckedRadioButtonId();

        switch (checkedId) {
            case R.id.rb_equipment_registration_status_owned:
                status = getResources().getString(R.string.equipment_owned);
                break;
            case R.id.rb_equipment_registration_status_sold:
                status = getResources().getString(R.string.equipment_sold);;
        }

        return status;

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

                imageViewEquipmentRegistrationPictureToUpload.setImageBitmap(bitmap);

            }


        }

        if (resultCode == RESULT_OK) {

            editTextEquipmentPicture.setEnabled(true);
            imageButtonEquipmentRegistPictureUpload.setEnabled(true);
            imageButtonEquipmentRegistPictureUpload.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_upload));

        }

    }

    private void uploadImage() {

        Log.v(TAG, "Inside method uploadImage()");

        if(filePath != null) {

            StorageReference sRef = storageReference.child(Utils.STORAGE_PATH_UPLOADS +
                                    editTextEquipmentPicture.getText() + System.currentTimeMillis() +
                                    "." + Utils.getFileExtension(filePath, contentResolver));

            UploadTask uploadTask = sRef.putFile(filePath);

            Task<Uri> urlTask = uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBarEquipmentRegistPictureUpload.setVisibility(View.VISIBLE);
                    progressBarEquipmentRegistPictureUpload.setProgress((int) progress);

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

                        progressBarEquipmentRegistPictureUpload.setVisibility(View.INVISIBLE);

                        Toast.makeText(EquipmentRegistrationActivity.this, "Successfully uploaded", Toast.LENGTH_LONG).show();

                        if (downloadUri != null) {
                            urlEquipmentPicture = downloadUri.toString();
                        }

                        editTextEquipmentPicture.setEnabled(false);
                        imageButtonEquipmentRegistSave.setEnabled(true);
                        imageButtonEquipmentRegistSave.setImageDrawable(ContextCompat.getDrawable(EquipmentRegistrationActivity.this, R.drawable.icon_save));

                    } else {

                        urlEquipmentPicture = "";

                    }
                }

            });

        }

    }

}
