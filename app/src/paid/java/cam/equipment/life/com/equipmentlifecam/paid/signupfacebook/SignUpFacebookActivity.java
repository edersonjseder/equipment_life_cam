package cam.equipment.life.com.equipmentlifecam.paid.signupfacebook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.model.Profile;
import cam.equipment.life.com.equipmentlifecam.paid.main.MainCamActivity;
import cam.equipment.life.com.equipmentlifecam.paid.owner.registration.ProfileRegistrationActivity;
import cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager;

import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.FROM_FACEBOOK_SIGNUP;
import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.KEY_EMAIL;
import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.KEY_NAME;

public class SignUpFacebookActivity extends AppCompatActivity {

    private static final String TAG = SignUpFacebookActivity.class.getSimpleName();

    private static final String USER_NAME = "name";
    private static final String EMAIL = "email";

    /** Facebook sign in **/
    @BindView(R.id.btn_facebook_sign_in) LoginButton btnFacebookSignIn;
    @BindView(R.id.btn_facebook_sign_in_cancel) AppCompatButton btnFacebookSignInCancel;

    /** Facebook API **/
    private CallbackManager callbackManager;

    // Session Manager Class
    private SessionManager session;

    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_facebook);

        ButterKnife.bind(this);

        Log.d(TAG, "onCreate() inside method");

        callbackManager = CallbackManager.Factory.create();

        // Session Manager
        session = new SessionManager(getApplicationContext());

        btnFacebookSignIn.setReadPermissions(Arrays.asList("public_profile", "email"));

        btnFacebookSignInCancel.setOnClickListener(view -> onBackPressed());

        btnFacebookSignIn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        getData(object);
                    }

                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,name");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }

        });

        if (AccessToken.getCurrentAccessToken() != null) {

            profile = new Profile();

            String name = session.pref.getString(KEY_NAME, "");
            String email = session.pref.getString(KEY_EMAIL, "");

            profile.setName(name);
            profile.setEmail(email);

            Context context = this;
            Class destinationClass = ProfileRegistrationActivity.class;

            Intent intentToStartRegistrationActivity = new Intent(context, destinationClass);
            intentToStartRegistrationActivity.putExtra(Intent.EXTRA_TEXT, profile);
            startActivity(intentToStartRegistrationActivity);

            finish();

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void getData(JSONObject object) {
        Log.d(TAG, "getData() inside method");

        profile = new Profile();

        Context context = this;
        Class destinationClass = ProfileRegistrationActivity.class;

        try {

            session.editor.putBoolean(FROM_FACEBOOK_SIGNUP, true);
            session.editor.putString(KEY_NAME, object.getString(USER_NAME));
            session.editor.putString(KEY_EMAIL, object.getString(EMAIL));
            session.editor.commit();

            profile.setName(object.getString(USER_NAME));
            profile.setEmail(object.getString(EMAIL));

            Intent intentToStartRegistrationActivity = new Intent(context, destinationClass);
            intentToStartRegistrationActivity.putExtra(Intent.EXTRA_TEXT, profile);
            startActivity(intentToStartRegistrationActivity);

            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
