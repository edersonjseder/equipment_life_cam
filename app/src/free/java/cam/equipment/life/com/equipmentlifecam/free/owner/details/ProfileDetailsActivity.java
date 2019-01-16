package cam.equipment.life.com.equipmentlifecam.free.owner.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.free.owner.edit.ProfileDetailsEditActivity;
import cam.equipment.life.com.equipmentlifecam.free.session.SessionManager;
import cam.equipment.life.com.equipmentlifecam.listeners.OnPostProfileTaskListener;
import cam.equipment.life.com.equipmentlifecam.model.Profile;
import cam.equipment.life.com.equipmentlifecam.utils.ProfileDetailsAsyncTask;

public class ProfileDetailsActivity extends AppCompatActivity implements OnPostProfileTaskListener {

    private static final String TAG = ProfileDetailsActivity.class.getSimpleName();

    @BindView(R.id.tv_owner_name_text) TextView textViewProfileName;

    @BindView(R.id.et_profile_cpf) TextView textViewProfileCpf;

    @BindView(R.id.et_profile_phone) TextView textViewProfileTelephone;

    @BindView(R.id.tv_owner_email_text) TextView textViewProfileEmail;

    @BindView(R.id.tv_owner_address_text) TextView textViewProfileAddress;

    @BindView(R.id.tv_owner_city_text) TextView textViewProfileCity;

    @BindView(R.id.tv_owner_state_text) TextView textViewProfileState;

    @BindView(R.id.image_btn_profile_edit) ImageButton imageButtonProfileDetailsEdit;

    private Profile profileInfo;

    // Member variable for the database
    private AppEquipmentLifeDatabase mDb;

    // To put a flag for details edition
    private SessionManager session;

    private ProfileDetailsAsyncTask profileDetailsAsyncTask;

    public static final String HAS_EDITED = "hasEditedInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        ButterKnife.bind(this);

        mDb = AppEquipmentLifeDatabase.getsInstance(getApplicationContext());

        session = new SessionManager(getApplicationContext());

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {

            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

                profileInfo = (Profile) intentThatStartedThisActivity.getSerializableExtra(Intent.EXTRA_TEXT);

                setDetailsFields(profileInfo);

            }

        }

        // Using anonymous View.OnClickListener overriding onClick method:
        // Replaced by lambda
        imageButtonProfileDetailsEdit.setOnClickListener(view -> onEditButtonClicked());

    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean hasEditedInfo = session.pref.getBoolean(HAS_EDITED, false);

        if (hasEditedInfo) {

            profileDetailsAsyncTask = new ProfileDetailsAsyncTask(this, mDb, profileInfo.getEmail());
            profileDetailsAsyncTask.execute();

        }
    }

    private void onEditButtonClicked() {

        Context context = this;
        Class destinationClass = ProfileDetailsEditActivity.class;

        Intent intentToStartDetailsEditActivity = new Intent(context, destinationClass);

        Bundle bundle = new Bundle();

        bundle.putSerializable(Intent.EXTRA_TEXT, profileInfo);

        intentToStartDetailsEditActivity.putExtras(bundle);

        startActivity(intentToStartDetailsEditActivity);

    }

    private void setDetailsFields(Profile profile) {

        textViewProfileName.setText(profile.getName());
        textViewProfileCpf.setText(profile.getCpf());
        textViewProfileTelephone.setText(profile.getTelephone());
        textViewProfileEmail.setText(profile.getEmail());
        textViewProfileAddress.setText(profile.getAddress());
        textViewProfileCity.setText(profile.getCity());
        textViewProfileState.setText(profile.getState());

    }

    @Override
    public void onTaskCompleted(Profile profile) {

        setDetailsFields(profile);

    }

}
