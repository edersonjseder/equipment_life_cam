package cam.equipment.life.com.equipmentlifecam.paid.initial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.model.Profile;
import cam.equipment.life.com.equipmentlifecam.paid.login.LoginActivity;
import cam.equipment.life.com.equipmentlifecam.paid.logoutsocial.VerifyLogoutActivity;
import cam.equipment.life.com.equipmentlifecam.paid.main.MainCamActivity;
import cam.equipment.life.com.equipmentlifecam.paid.owner.registration.ProfileRegistrationActivity;
import cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager;
import cam.equipment.life.com.equipmentlifecam.paid.signupfacebook.SignUpFacebookActivity;

import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.FROM_FACEBOOK_SIGNUP;
import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.FROM_GOOGLE_SIGNUP;
import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.KEY_EMAIL;
import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.KEY_NAME;

public class InitialScreenActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = InitialScreenActivity.class.getSimpleName();

    private static final String FLAG_GOOGLE_DIALOG = "flagGoogleDialog";

    private static final String FLAG_CHANGED = "configChangeFlag";
    private static final String EMAIL_SAVED = "emailSaved";
    private static final String PASSWORD_SAVED = "passwordSaved";
    private static final String CONFIRM_PASSWORD_SAVED = "confirmPasswordSaved";
    private static final int REQ_CODE = 9001;

    @BindView(R.id.btn_initial_sign_in) Button btnInitialSignIn;
    @BindView(R.id.btn_initial_sign_up) Button btnInitialSignUp;

    @BindView(R.id.btn_sign_up_google) Button btnSignUpGoogle;
    @BindView(R.id.btn_sign_up_facebook) Button btnSignUpFacebook;

    /** Credentials Registration Dialog **/
    private EditText editViewEmailSignUp;
    private EditText editViewPasswordSignUp;
    private EditText editViewConfirmPasswordSignUp;
    private AppCompatButton btnEquipmentSignUp;
    private AppCompatButton btnEquipmentSignUpCancel;

    /** Google sign in Dialog **/
    private SignInButton btnGoogleSignInDialog;
    private AppCompatButton btnGoogleSignInCancel;

    /** Google API **/
    private GoogleApiClient googleApiClient;

    // Session Manager Class
    private SessionManager session;

    private AlertDialog dialogCredentialRegistration, dialogGoogleSignIn;

    private int configChangeFlag = 1;

    private int signInGoogleFlag = 0;

    private String email, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_screen);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        String email = session.pref.getString(KEY_EMAIL, "");
        String name = session.pref.getString(KEY_NAME, "");

        boolean google = session.pref.getBoolean(FROM_FACEBOOK_SIGNUP, false);
        boolean facebook = session.pref.getBoolean(FROM_GOOGLE_SIGNUP, false);


        if (session.isLoggedIn()) {

            Intent intent = new Intent(this, MainCamActivity.class);

            startActivity(intent);

            finish();

        } else if (session.verifyIfUserHasLoggedInAlreadyFirstTime()) {

            if (google || facebook) {

                Bundle bundle = new Bundle();

                bundle.putString(KEY_EMAIL, email);

                Intent intent = new Intent(this, VerifyLogoutActivity.class);

                intent.putExtras(bundle);

                startActivity(intent);


            } else {

                startActivity(new Intent(this, LoginActivity.class));

                finish();

            }

        }

        ButterKnife.bind(this);

        if (savedInstanceState != null) {

            signInGoogleFlag = savedInstanceState.getInt(FLAG_GOOGLE_DIALOG);
            configChangeFlag = savedInstanceState.getInt(FLAG_CHANGED);
            this.email = savedInstanceState.getString(EMAIL_SAVED);
            password = savedInstanceState.getString(PASSWORD_SAVED);
            confirmPassword = savedInstanceState.getString(CONFIRM_PASSWORD_SAVED);

        }

        if (signInGoogleFlag == 1) {
            goToSignUpGoogle();
        }

        if((configChangeFlag == 1) &&
                (this.email != null) &&
                (password != null) &&
                (confirmPassword != null)){

            showDialogLoginCredentialsRegistration();

        }

        // Google sign in
        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();



        btnInitialSignIn.setOnClickListener(this);
        btnInitialSignUp.setOnClickListener(this);
        btnSignUpGoogle.setOnClickListener(this);
        btnSignUpFacebook.setOnClickListener(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Log.i(TAG, "onSaveInstanceState() inside method");

        if(signInGoogleFlag == 1) {
            bundle.putInt(FLAG_GOOGLE_DIALOG, signInGoogleFlag);
        }

        if (editViewEmailSignUp != null &&
                editViewPasswordSignUp != null &&
                editViewConfirmPasswordSignUp != null) {

            email = editViewEmailSignUp.getText().toString();
            password = editViewPasswordSignUp.getText().toString();
            confirmPassword = editViewConfirmPasswordSignUp.getText().toString();

            configChangeFlag = 1;

            bundle.putInt(FLAG_CHANGED, configChangeFlag);
            bundle.putString(EMAIL_SAVED, email);
            bundle.putString(PASSWORD_SAVED, password);
            bundle.putString(CONFIRM_PASSWORD_SAVED, confirmPassword);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (dialogCredentialRegistration != null) {
            dialogCredentialRegistration.dismiss();
            dialogCredentialRegistration = null;
        }

        if (dialogGoogleSignIn != null) {
            dialogGoogleSignIn.dismiss();
            dialogGoogleSignIn = null;
        }

    }

    private void goToSignUpScreen() {

        showDialogLoginCredentialsRegistration();

    }

    private void goToLoginScreen() {

        startActivity(new Intent(this, LoginActivity.class));

    }

    private void showDialogLoginCredentialsRegistration() {
        Log.i(TAG, "showDialogLoginCredentialsRegistration() inside method");

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_login_credentials_registration, null);

        editViewEmailSignUp = view.findViewById(R.id.input_username_equipment_signup);
        editViewPasswordSignUp = view.findViewById(R.id.input_password_equipment_signup);
        editViewConfirmPasswordSignUp = view.findViewById(R.id.input_password_confirm_equipment_login);
        btnEquipmentSignUp = view.findViewById(R.id.btn_equipment_signup);
        btnEquipmentSignUpCancel = view.findViewById(R.id.btn_equipment_signup_cancel);

        if (configChangeFlag == 1) {

            configChangeFlag = 0;
            editViewEmailSignUp.setText(email);
            editViewPasswordSignUp.setText(password);
            editViewConfirmPasswordSignUp.setText(confirmPassword);

        }

        btnEquipmentSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveLoginCredentials();
            }
        });

        btnEquipmentSignUpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cleanFields();

                dialogCredentialRegistration.dismiss();
                dialogCredentialRegistration = null;
            }
        });

        mBuilder.setView(view);
        dialogCredentialRegistration = mBuilder.create();
        dialogCredentialRegistration.setCanceledOnTouchOutside(false);
        dialogCredentialRegistration.show();

    }

    private void goToSignUpGoogle() {
        Log.i(TAG, "goToSignUpGoogle() inside method");

        signInGoogleFlag = 1;

        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_login_google_sign_in, null);

        btnGoogleSignInDialog = view.findViewById(R.id.btn_dialog_sign_in_google);
        btnGoogleSignInCancel = view.findViewById(R.id.btn_dialog_signin_cancel);

        btnGoogleSignInDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGoogle();
            }
        });

        btnGoogleSignInCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signInGoogleFlag = 0;
                dialogGoogleSignIn.dismiss();
                dialogGoogleSignIn = null;

            }
        });

        mDialogBuilder.setView(view);
        dialogGoogleSignIn = mDialogBuilder.create();
        dialogGoogleSignIn.setCanceledOnTouchOutside(false);
        dialogGoogleSignIn.show();

    }

    private void goToSignUpFacebook() {


        startActivity(new Intent(this, SignUpFacebookActivity.class));

    }

    private void signInGoogle() {
        Log.i(TAG, "signInGoogle() inside method");

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult() inside method");

        if (requestCode == REQ_CODE) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResultFromActivity(result);

        }

    }

    private void handleResultFromActivity(GoogleSignInResult result) {
        Log.i(TAG, "handleResultFromActivity() inside method");

        if (result.isSuccess()) {

            session.editor.putBoolean(FROM_GOOGLE_SIGNUP, result.isSuccess());
            session.editor.commit();

            Profile profile = new Profile();

            GoogleSignInAccount account = result.getSignInAccount();

            String name = account.getDisplayName();
            String familyName = account.getFamilyName();
            String email = account.getEmail();

            // Populate part of profile object
            profile.setName(name + " " + familyName);
            profile.setEmail(email);

            Context context = this;
            Class destinationClass = ProfileRegistrationActivity.class;

            Intent intentToStartRegistrationActivity = new Intent(context, destinationClass);
            intentToStartRegistrationActivity.putExtra(Intent.EXTRA_TEXT, profile);
            startActivity(intentToStartRegistrationActivity);

            finish();

        }

    }

    private void onSaveLoginCredentials() {

        if (!validate()) {
            return;
        }

        populateProfileCredentials();

    }

    private void populateProfileCredentials() {

        Profile profile = new Profile();

        profile.setEmail(editViewEmailSignUp.getText().toString());
        profile.setPassword(editViewPasswordSignUp.getText().toString());

        Context context = this;
        Class destinationClass = ProfileRegistrationActivity.class;

        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, profile);
        startActivity(intentToStartDetailActivity);

        cleanFields();

    }

    private void cleanFields() {

        email = null;
        password = null;
        confirmPassword = null;

        editViewEmailSignUp = null;
        editViewPasswordSignUp = null;
        editViewConfirmPasswordSignUp = null;

    }

    public boolean validate() {
        boolean valid = true;

        Pattern emailPtrn = Pattern.compile(
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        String email = editViewEmailSignUp.getText().toString();
        String password = editViewPasswordSignUp.getText().toString();
        String confirmPassword = editViewConfirmPasswordSignUp.getText().toString();

        Matcher match = emailPtrn.matcher(email);

        if (email.isEmpty()) {

            editViewEmailSignUp.setError(getResources().getString(R.string.login_profile_validator_username_required_error));
            valid = false;

        } else if (!match.matches()) {

            editViewEmailSignUp.setError(getResources().getString(R.string.login_profile_validator_email_not_match));
            valid = false;

        } else if (password.isEmpty() || password.length() < 4 || password.length() > 10) {

            editViewPasswordSignUp.setError(getResources().getString(R.string.login_profile_validator_password_limit_error));
            valid = false;

        } else if (!password.equals(confirmPassword)) {

            editViewConfirmPasswordSignUp.setError(getResources().getString(R.string.login_profile_validator_password_not_match));
            valid = false;

        } else {
            editViewPasswordSignUp.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_initial_sign_in:
                goToLoginScreen();
                break;

            case R.id.btn_initial_sign_up:
                goToSignUpScreen();
                break;

            case R.id.btn_sign_up_google:
                goToSignUpGoogle();
                break;

            case R.id.btn_sign_up_facebook:
                goToSignUpFacebook();
                break;

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
