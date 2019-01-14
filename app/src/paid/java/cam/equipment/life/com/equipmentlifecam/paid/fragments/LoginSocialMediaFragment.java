package cam.equipment.life.com.equipmentlifecam.paid.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.executors.AppExecutors;
import cam.equipment.life.com.equipmentlifecam.model.Profile;
import cam.equipment.life.com.equipmentlifecam.paid.main.MainCamActivity;
import cam.equipment.life.com.equipmentlifecam.paid.owner.registration.ProfileRegistrationActivity;
import cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager;
import cam.equipment.life.com.equipmentlifecam.paid.signupfacebook.SignUpFacebookActivity;
import cam.equipment.life.com.equipmentlifecam.viewmodel.ProfileDetailViewModel;
import cam.equipment.life.com.equipmentlifecam.viewmodel.factory.ProfileViewModelFactory;

import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.FROM_FACEBOOK_SIGNUP;
import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.FROM_GOOGLE_SIGNUP;
import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.KEY_EMAIL;
import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.KEY_NAME;

public class LoginSocialMediaFragment extends Fragment implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = LoginSocialMediaFragment.class.getSimpleName();

    private static final String EMAIL_KEY = "emailReceived";
    private static final String EMAIL = "email";
    private static final String USER_NAME = "name";

    private static final int REQ_CODE = 9001;

    private SignInButton btnFrgGoogleSignIn;

    private LoginButton btnFrgFacebookSignIn;

    private String email;

    private Profile profile;

    // Session Manager Class
    private SessionManager session;

    // Member variable for the database
    private AppEquipmentLifeDatabase mDb;

    /** Google API **/
    private GoogleApiClient googleApiClient;

    /** Facebook API **/
    private CallbackManager callbackManager;

    public LoginSocialMediaFragment() {
        // Required empty public constructor
    }

    public static LoginSocialMediaFragment newInstance(String email) {

        LoginSocialMediaFragment fragment = new LoginSocialMediaFragment();

        Bundle args = new Bundle();

        args.putString(EMAIL_KEY, email);

        fragment.setArguments(args);

        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Session Manager
        session = new SessionManager(getActivity().getApplicationContext());

        mDb = AppEquipmentLifeDatabase.getsInstance(getActivity().getApplicationContext());

        if (getArguments() != null) {
            email = getArguments().getString(EMAIL_KEY);
        }

        // Facebook callbackManager initialized
        callbackManager = CallbackManager.Factory.create();

        // Google sign in
        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();

        getUserFromDatabase();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_social_media, container, false);

        btnFrgGoogleSignIn = view.findViewById(R.id.btn_frg_google_sign_in);
        btnFrgFacebookSignIn = view.findViewById(R.id.btn_frg_facebook_sign_in);

        btnFrgFacebookSignIn.setReadPermissions(Arrays.asList("public_profile", "email"));

        btnFrgGoogleSignIn.setOnClickListener(this);
        btnFrgFacebookSignIn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_frg_google_sign_in:
                goToSignUpGoogle();
                break;

            case R.id.btn_frg_facebook_sign_in:
                goToSignUpFacebook();
                break;

        }

    }

    private void goToSignUpFacebook() {

        btnFrgFacebookSignIn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

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

    }

    private void getData(JSONObject object) {
        Log.d(TAG, "getData() inside method");

        Context context = getActivity().getApplicationContext();
        Class destinationClass = MainCamActivity.class;

        try {

            session.editor.putBoolean(FROM_FACEBOOK_SIGNUP, true);
            session.editor.putString(KEY_NAME, object.getString(USER_NAME));
            session.editor.putString(KEY_EMAIL, object.getString(EMAIL));
            session.editor.commit();

            profile.setName(object.getString(USER_NAME));
            profile.setEmail(object.getString(EMAIL));

            Intent intentToStartMainListActivity = new Intent(context, destinationClass);
            intentToStartMainListActivity.putExtra(Intent.EXTRA_TEXT, profile);
            getActivity().startActivity(intentToStartMainListActivity);

            getActivity().finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void goToSignUpGoogle() {
        Log.i(TAG, "signInGoogle() inside method");

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult() inside method");

        if (requestCode == REQ_CODE) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        } else {

            callbackManager.onActivityResult(requestCode, resultCode, data);

        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {

            session.editor.putBoolean(FROM_GOOGLE_SIGNUP, result.isSuccess());
            session.editor.commit();

            GoogleSignInAccount account = result.getSignInAccount();

            String name = account.getDisplayName();
            String familyName = account.getFamilyName();
            String email = account.getEmail();

            String concat = name + " " + familyName;

            profile.setName(concat);
            profile.setEmail(email);

            session.createLoginSessionFromSocialSignIn(profile.getName(), profile.getEmail());

            updateUserToChangeSocialMediaLoginType();

        }

    }

    private void updateUserToChangeSocialMediaLoginType() {

        // temp solution
        AppExecutors.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {

                mDb.profileDao().updateProfile(profile);

                Context context = getActivity().getApplicationContext();
                Class destinationClass = MainCamActivity.class;

                Intent intentToStartRegistrationActivity = new Intent(context, destinationClass);
                intentToStartRegistrationActivity.putExtra(Intent.EXTRA_TEXT, profile);
                getActivity().startActivity(intentToStartRegistrationActivity);
                getActivity().finish();

            }
        });

    }

    private void getUserFromDatabase() {
        Log.i(TAG, "getUserFromDatabase() inside method");

        ProfileViewModelFactory factory = new ProfileViewModelFactory(mDb, email);

        final ProfileDetailViewModel viewModel =
                ViewModelProviders.of(this, factory).get(ProfileDetailViewModel.class);

        viewModel.getProfile().observe(this, new Observer<Profile>() {

            @Override
            public void onChanged(@Nullable Profile profile) {

                viewModel.getProfile().removeObserver(this);

                setProfile(profile);

            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
