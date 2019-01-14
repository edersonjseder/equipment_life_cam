package cam.equipment.life.com.equipmentlifecam.free.owner.edit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.executors.AppExecutors;
import cam.equipment.life.com.equipmentlifecam.free.session.SessionManager;
import cam.equipment.life.com.equipmentlifecam.model.Profile;

import static cam.equipment.life.com.equipmentlifecam.free.owner.details.ProfileDetailsActivity.HAS_EDITED;
import static cam.equipment.life.com.equipmentlifecam.free.session.SessionManager.FROM_FACEBOOK_SIGNUP;
import static cam.equipment.life.com.equipmentlifecam.free.session.SessionManager.FROM_GOOGLE_SIGNUP;

public class ProfileDetailsEditActivity extends AppCompatActivity {

    private static final String TAG = ProfileDetailsEditActivity.class.getSimpleName();

    @BindView(R.id.layout_owner_passwords) LinearLayout linearLayoutOwnerPasswords;

    @BindView(R.id.et_profile_name) EditText editViewProfileEditName;

    @BindView(R.id.et_profile_cpf) EditText editViewProfileEditCpf;

    @BindView(R.id.et_profile_phone) EditText editViewProfileEditTelephone;

    @BindView(R.id.et_profile_email) EditText editTextProfileEditEmail;

    @BindView(R.id.et_profile_address) EditText editTextProfileEditAddress;

    @BindView(R.id.et_profile_city) EditText editViewProfileEditCity;

    @BindView(R.id.et_profile_state) EditText editTextProfileEditState;

    @BindView(R.id.et_profile_password) EditText editTextProfileEditPassword;

    @BindView(R.id.et_profile_password_confirm) EditText editTextProfileEditPasswordConfirm;

    @BindView(R.id.image_btn_profile_details_edit_save) ImageButton imageButtonProfileDetailsEditSave;

    // Member variable for the database
    private AppEquipmentLifeDatabase mDb;

    private Profile profile;

    // To put a flag for details edition
    private SessionManager session;

    private boolean hasEditedInfo;

    private boolean isLoginFromGoogle;

    private boolean isLoginFromFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details_edit);

        ButterKnife.bind(this);

        session = new SessionManager(getApplicationContext());

        session.editor.putBoolean(HAS_EDITED, false);
        session.editor.commit();

        // Check if the login was from Google
        isLoginFromGoogle = session.pref.getBoolean(FROM_GOOGLE_SIGNUP, false);

        // Check if the login was from Facebook
        isLoginFromFacebook = session.pref.getBoolean(FROM_FACEBOOK_SIGNUP, false);

        if (isLoginFromGoogle || isLoginFromFacebook) {
            hidePasswordLayout();
        }

        mDb = AppEquipmentLifeDatabase.getsInstance(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        hasEditedInfo = session.pref.getBoolean(HAS_EDITED, false);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {

            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

                profile = (Profile) bundle.getSerializable(Intent.EXTRA_TEXT);

                setDetailsEditFields(profile);

            }
        }

        imageButtonProfileDetailsEditSave.setOnClickListener(view -> onSaveImageButtonClicked(profile));

    }

    private void setDetailsEditFields(Profile profile) {

        if (isLoginFromGoogle || isLoginFromFacebook) {

            editViewProfileEditName.setEnabled(false);
            editTextProfileEditEmail.setEnabled(false);

        }

        editViewProfileEditName.setText(profile.getName());
        editViewProfileEditCpf.setText(profile.getCpf());
        editViewProfileEditTelephone.setText(profile.getTelephone());
        editTextProfileEditEmail.setText(profile.getEmail());
        editTextProfileEditAddress.setText(profile.getAddress());
        editViewProfileEditCity.setText(profile.getCity());
        editTextProfileEditState.setText(profile.getState());

        editTextProfileEditPassword.setText(profile.getPassword());
        editTextProfileEditPasswordConfirm.setText(profile.getPassword());

    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveImageButtonClicked(Profile profile) {

        if (isLoginFromGoogle || isLoginFromFacebook) {

            if (!validateInfoFromSocial()) {
                return;
            }

        } else {

            if (!validate()) {
                return;
            }
        }

        populateEditedEntity(profile);

        hasEditedInfo = true;
        session.editor.putBoolean(HAS_EDITED, hasEditedInfo);
        session.editor.commit();

        // temp solution
        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {

                mDb.profileDao().updateProfile(profile);

                finish();
            }
        });

    }

    private void populateEditedEntity(Profile profile) {

        profile.setName(editViewProfileEditName.getText().toString());
        profile.setCpf(editViewProfileEditCpf.getText().toString());
        profile.setTelephone(editViewProfileEditTelephone.getText().toString());
        profile.setEmail(editTextProfileEditEmail.getText().toString());
        profile.setAddress(editTextProfileEditAddress.getText().toString());
        profile.setCity(editViewProfileEditCity.getText().toString());
        profile.setState(editTextProfileEditState.getText().toString());

        profile.setPassword(editTextProfileEditPassword.getText().toString());

    }

    private void hidePasswordLayout() {

        linearLayoutOwnerPasswords.setVisibility(View.GONE);

    }

    public boolean validate() {
        boolean valid = true;

        Pattern emailPtrn = Pattern.compile(
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        String name = editViewProfileEditName.getText().toString();

        String cpf = editViewProfileEditCpf.getText().toString();

        String phone = editViewProfileEditTelephone.getText().toString();

        String email = editTextProfileEditEmail.getText().toString();

        String password = editTextProfileEditPassword.getText().toString();

        String confirmPassword = editTextProfileEditPasswordConfirm.getText().toString();

        Matcher match = emailPtrn.matcher(email);

        if (name.isEmpty()) {

            editViewProfileEditName.setError(getResources().getString(R.string.registration_profile_validator_name_required));
            valid = false;

        } else if (cpf.isEmpty()) {

            editViewProfileEditCpf.setError(getResources().getString(R.string.registration_profile_validator_cpf_required));
            valid = false;

        } else if (phone.isEmpty()) {

            editViewProfileEditTelephone.setError(getResources().getString(R.string.registration_profile_validator_phone_required));
            valid = false;

        } else if (email.isEmpty()) {

            editTextProfileEditEmail.setError(getResources().getString(R.string.login_profile_validator_email_required));
            valid = false;

        } else if (password.isEmpty() || password.length() < 4 || password.length() > 10) {

            editTextProfileEditPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;

        } else if (!password.equals(confirmPassword)) {

            editTextProfileEditPassword.setError("Passwords must be matched");
            valid = false;

        } else if (!match.matches()) {

            editTextProfileEditEmail.setError(getResources().getString(R.string.login_profile_validator_email_not_match));
            valid = false;

        } else {

            editTextProfileEditPassword.setError(null);
        }

        return valid;
    }

    public boolean validateInfoFromSocial() {
        boolean valid = true;


        String cpf = editViewProfileEditCpf.getText().toString();
        String phone = editViewProfileEditTelephone.getText().toString();

        if (cpf.isEmpty()) {

            editViewProfileEditCpf.setError(getResources().getString(R.string.registration_profile_validator_cpf_required));
            valid = false;

        } else if (phone.isEmpty()) {

            editViewProfileEditTelephone.setError(getResources().getString(R.string.registration_profile_validator_phone_required));
            valid = false;

        } else {

            editViewProfileEditTelephone.setError(null);
        }

        return valid;
    }

}
