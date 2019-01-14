package cam.equipment.life.com.equipmentlifecam.paid.login;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.model.Profile;
import cam.equipment.life.com.equipmentlifecam.paid.main.MainCamActivity;
import cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager;
import cam.equipment.life.com.equipmentlifecam.viewmodel.ProfileDetailViewModel;
import cam.equipment.life.com.equipmentlifecam.viewmodel.factory.ProfileViewModelFactory;

import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.HAS_LOGGED_IN_FIRST_TIME_ALREADY;
import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.KEY_EMAIL;
import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.KEY_NAME;
import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.KEY_PASSWORD;
import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.KEY_USERNAME;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.input_email_equipment_login) EditText editViewEmailLogin;
    @BindView(R.id.input_password_equipment_login) EditText editViewPasswordLogin;
    @BindView(R.id.btn_equipment_login) AppCompatButton btnPasswordSignUp;

    private TextView textViewMessageAlertDialog;
    private Button btnAlertDialog;

    private AlertDialog dialog;

    // Session Manager Class
    private SessionManager session;

    private Profile profileOwner;

    // Member variable for the database
    private AppEquipmentLifeDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_equipment);
        Log.i(TAG, "onCreate() inside method");

        ButterKnife.bind(this);

        mDb = AppEquipmentLifeDatabase.getsInstance(getApplicationContext());

        // Session Manager
        session = new SessionManager(getApplicationContext());

        //if SharedPreferences contains username and name or password then directly redirect to Home activity
        if((session.pref.contains(KEY_EMAIL)) && (session.pref.contains(KEY_PASSWORD) || session.pref.contains(KEY_NAME))){
            startActivity(new Intent(this, MainCamActivity.class));
            finish();
        }

        btnPasswordSignUp.setOnClickListener(view -> loginUser());

    }

    private void loginUser() {

        if (!validate()) {
            return;
        }

        String email = editViewEmailLogin.getText().toString();
        String password = editViewPasswordLogin.getText().toString();

        session.editor.putBoolean(HAS_LOGGED_IN_FIRST_TIME_ALREADY, true);

        ProfileViewModelFactory factory = new ProfileViewModelFactory(mDb, email);

        final ProfileDetailViewModel viewModel =
                ViewModelProviders.of(this, factory).get(ProfileDetailViewModel.class);

        viewModel.getProfile().observe(this, new Observer<Profile>() {

            @Override
            public void onChanged(@Nullable Profile profile) {

                viewModel.getProfile().removeObserver(this);

                if (profile != null) {

                    setProfile(profile, password);

                } else {

                    showDialogError();

                }

            }
        });

    }

    private void setProfile(Profile profile, String password) {

        profileOwner = profile;

        if (!profileOwner.getPassword().equals(password)) {
            editViewPasswordLogin.setError(getResources().getString(R.string.login_password_error));
            return;
        }

        session.createLoginSession(profileOwner.getName(), profileOwner.getEmail(), profileOwner.getPassword());

        Context context = this;
        Class destinationClass = MainCamActivity.class;

        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, profileOwner);
        startActivity(intentToStartDetailActivity);
        finish();

    }

    private void showDialogError() {
        Log.i(TAG, "showDialogError() inside method");

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_error_message, null);

        textViewMessageAlertDialog = view.findViewById(R.id.tv_message_alert_dialog);
        btnAlertDialog = view.findViewById(R.id.btn_alert_dialog_message);

        textViewMessageAlertDialog.setText(getResources().getString(R.string.login_alert_dialog_error_login_message));

        btnAlertDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        mBuilder.setView(view);
        dialog = mBuilder.create();
        dialog.show();

    }

    @Override
    public void onBackPressed() {
       super.onBackPressed();
    }

    public boolean validate() {
        boolean valid = true;

        Pattern emailPtrn = Pattern.compile(
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        String email = editViewEmailLogin.getText().toString();
        String password = editViewPasswordLogin.getText().toString();

        Matcher match = emailPtrn.matcher(email);

        if (email.isEmpty()) {

            editViewEmailLogin.setError(getResources().getString(R.string.login_profile_validator_username_required_error));
            valid = false;

        } else if (!match.matches()) {

            editViewEmailLogin.setError(getResources().getString(R.string.login_profile_validator_email_not_match));
            valid = false;

        } else if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            editViewPasswordLogin.setError(getResources().getString(R.string.login_profile_validator_password_limit_error));
            valid = false;

        } else {
            editViewPasswordLogin.setError(null);
        }

        return valid;
    }
}
