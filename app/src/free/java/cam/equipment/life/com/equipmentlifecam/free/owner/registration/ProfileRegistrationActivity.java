package cam.equipment.life.com.equipmentlifecam.free.owner.registration;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.executors.AppExecutors;
import cam.equipment.life.com.equipmentlifecam.free.main.MainCamActivity;
import cam.equipment.life.com.equipmentlifecam.free.session.SessionManager;
import cam.equipment.life.com.equipmentlifecam.model.Profile;
import cam.equipment.life.com.equipmentlifecam.utils.MaskWatcher;

import static cam.equipment.life.com.equipmentlifecam.free.session.SessionManager.FROM_FACEBOOK_SIGNUP;
import static cam.equipment.life.com.equipmentlifecam.free.session.SessionManager.FROM_GOOGLE_SIGNUP;

public class ProfileRegistrationActivity extends AppCompatActivity {

    private static final String TAG = ProfileRegistrationActivity.class.getSimpleName();

    @BindView(R.id.et_owner_name) EditText editViewProfileName;

    @BindView(R.id.et_owner_cpf) EditText editViewProfileCpf;

    @BindView(R.id.et_owner_phone) EditText editViewProfileTelephone;

    @BindView(R.id.et_owner_address) EditText editTextProfileAddress;

    @BindView(R.id.et_owner_email) EditText editTextProfileEmail;

    @BindView(R.id.et_owner_city) EditText editViewProfileCity;

    @BindView(R.id.et_owner_state) EditText editTextProfileState;

    @BindView(R.id.image_btn_profile_registration_save) ImageButton imageButtonProfileRegistrationSave;

    private Profile profile;

    // Member variable for the database
    private AppEquipmentLifeDatabase mDb;

    // Session Manager Class
    private SessionManager session;

    // Flag to check if login was from Google
    private boolean isLoginFromGoogle;

    // Flag to check if login was from facebook
    private boolean isLoginFromFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_registration);
        Log.i(TAG, "onCreate() inside method");

        ButterKnife.bind(this);

        mDb = AppEquipmentLifeDatabase.getsInstance(getApplicationContext());

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // Receives the login credentials from SignUpActivity
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {

            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

                profile = (Profile) intentThatStartedThisActivity.getSerializableExtra(Intent.EXTRA_TEXT);

            }
        }

        // Check if the login was from Google
        isLoginFromGoogle = session.pref.getBoolean(FROM_GOOGLE_SIGNUP, false);

        // Check if the login was from Facebook
        isLoginFromFacebook = session.pref.getBoolean(FROM_FACEBOOK_SIGNUP, false);

        if (isLoginFromGoogle) {

            applyNameAndEmailFromSocialSignUp();

        }

        if (isLoginFromFacebook) {

            applyNameAndEmailFromSocialSignUp();

        }

        initFieldsWithMasks();

        // Using anonymous View.OnClickListener overriding onClick method:
        // Replaced by lambda
        imageButtonProfileRegistrationSave.setOnClickListener(view -> onSaveButtonClicked());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initFieldsWithMasks() {

        editViewProfileCpf.addTextChangedListener(MaskWatcher.buildCpf());
        editViewProfileTelephone.addTextChangedListener(new MaskWatcher("(##) #####-####"));

        editTextProfileEmail.setText(profile.getEmail());
        editTextProfileEmail.setEnabled(false);

    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {

        if (!validate()) {
            return;
        }

        if (isLoginFromGoogle) {

            session.createLoginSessionFromSocialSignIn(profile.getName(), profile.getEmail());

        } else if (isLoginFromFacebook) {

            session.createLoginSessionFromSocialSignIn(profile.getName(), profile.getEmail());

        } else {

            session.createLoginSession(profile.getName(), profile.getEmail(), profile.getPassword());

        }

        populateProfileEntity(profile);

        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {

                mDb.profileDao().insertProfile(profile);

                Context context = ProfileRegistrationActivity.this;
                Class destinationClass = MainCamActivity.class;

                Intent intentToStartDetailActivity = new Intent(context, destinationClass);
                intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, profile);
                startActivity(intentToStartDetailActivity);
                finish();

            }
        });

    }

    private void populateProfileEntity(Profile profile) {

        profile.setName(editViewProfileName.getText().toString());
        profile.setCpf(editViewProfileCpf.getText().toString());
        profile.setTelephone(editViewProfileTelephone.getText().toString());
        profile.setEmail(editTextProfileEmail.getText().toString());
        profile.setAddress(editTextProfileAddress.getText().toString());
        profile.setCity(editViewProfileCity.getText().toString());
        profile.setState(editTextProfileState.getText().toString());

    }

    private void applyNameAndEmailFromSocialSignUp() {

        editViewProfileName.setText(profile.getName());
        editViewProfileName.setEnabled(false);

    }

    public boolean validate() {
        boolean valid = true;

        Pattern emailPtrn = Pattern.compile(
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        Pattern cpfPtrn = Pattern.compile("^((\\d{3}).(\\d{3}).(\\d{3})-(\\d{2}))*$");

        String name = editViewProfileName.getText().toString();

        String cpf = editViewProfileCpf.getText().toString();

        String phone = editViewProfileTelephone.getText().toString();

        String email = editTextProfileEmail.getText().toString();

        String address = editTextProfileAddress.getText().toString();

        String city = editViewProfileCity.getText().toString();

        String state = editTextProfileState.getText().toString();

        Matcher match = emailPtrn.matcher(email)
                ;
        Matcher cpfMatch = cpfPtrn.matcher(cpf);

        if (name.isEmpty()) {

            editViewProfileName.setError(getResources().getString(R.string.registration_profile_validator_name_required));
            valid = false;

        } else if (cpf.isEmpty()) {

            editViewProfileCpf.setError(getResources().getString(R.string.registration_profile_validator_cpf_required));
            valid = false;

        } else if (!cpfMatch.matches()) {
            editViewProfileCpf.setError(getResources().getString(R.string.login_profile_validator_cpf_not_match));
            valid = false;

        } else if (phone.isEmpty()) {

            editViewProfileTelephone.setError(getResources().getString(R.string.registration_profile_validator_phone_required));
            valid = false;

        } else if (email.isEmpty()) {

            editTextProfileEmail.setError(getResources().getString(R.string.login_profile_validator_email_required));
            valid = false;

        } else if (address.isEmpty()) {

            editTextProfileAddress.setError(getResources().getString(R.string.registration_profile_validator_address_required));
            valid = false;

        } else if (city.isEmpty()) {

            editViewProfileCity.setError(getResources().getString(R.string.registration_profile_validator_city_required));
            valid = false;

        } else if (state.isEmpty()) {

            editTextProfileState.setError(getResources().getString(R.string.registration_profile_validator_state_required));
            valid = false;

        } else if (!match.matches()) {
            editTextProfileEmail.setError(getResources().getString(R.string.login_profile_validator_email_not_match));
            valid = false;

        } else {
            editTextProfileEmail.setError(null);
        }

        return valid;
    }
}
