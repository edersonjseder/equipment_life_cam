package cam.equipment.life.com.equipmentlifecam.paid.logoutsocial;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.paid.fragments.LoginSocialMediaFragment;

import static cam.equipment.life.com.equipmentlifecam.paid.session.SessionManager.KEY_EMAIL;

public class VerifyLogoutActivity extends AppCompatActivity {

    private static final String TAG = VerifyLogoutActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_logout_activity);
        Log.i(TAG, "onCreate() inside method");

        Bundle bundle = getIntent().getExtras();

        String email = bundle.getString(KEY_EMAIL);

        LoginSocialMediaFragment mLoginSocialMediaFragment = LoginSocialMediaFragment.newInstance(email);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.id_content_fragment_verify, mLoginSocialMediaFragment)
                .commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {

            fragment.onActivityResult(requestCode, resultCode, data);

        }
    }
}
