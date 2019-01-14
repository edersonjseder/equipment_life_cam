package cam.equipment.life.com.equipmentlifecam.free.signupfacebook;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.free.owner.registration.ProfileRegistrationActivity;
import cam.equipment.life.com.equipmentlifecam.free.session.SessionManager;
import cam.equipment.life.com.equipmentlifecam.model.Profile;

import static cam.equipment.life.com.equipmentlifecam.free.session.SessionManager.FROM_FACEBOOK_SIGNUP;
import static cam.equipment.life.com.equipmentlifecam.free.session.SessionManager.KEY_EMAIL;
import static cam.equipment.life.com.equipmentlifecam.free.session.SessionManager.KEY_NAME;

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

        profile = new Profile();

        try {

            session.editor.putBoolean(FROM_FACEBOOK_SIGNUP, true);
            session.editor.putString(KEY_NAME, object.getString(USER_NAME));
            session.editor.putString(KEY_EMAIL, object.getString(EMAIL));
            session.editor.commit();

            profile.setName(object.getString(USER_NAME));
            profile.setEmail(object.getString(EMAIL));

            Context context = this;
            Class destinationClass = ProfileRegistrationActivity.class;

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
