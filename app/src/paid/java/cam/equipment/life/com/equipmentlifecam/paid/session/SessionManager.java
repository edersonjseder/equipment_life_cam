package cam.equipment.life.com.equipmentlifecam.paid.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.HashMap;

import cam.equipment.life.com.equipmentlifecam.paid.initial.InitialScreenActivity;
import cam.equipment.life.com.equipmentlifecam.paid.login.LoginActivity;
import cam.equipment.life.com.equipmentlifecam.paid.logoutsocial.VerifyLogoutActivity;

public class SessionManager {
    // Shared Preferences
    public SharedPreferences pref;

    // Editor for Shared preferences
    public SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "EquipmentLife";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "is_logged_in";

    public static final String HAS_LOGGED_IN_FIRST_TIME_ALREADY = "has_logged_in_first_time_already";

    // Owner name
    public static final String KEY_NAME = "profile_name";

    // Owner family name
    public static final String KEY_FAMILY_NAME = "profile_family_name";

    // Username
    public static final String KEY_USERNAME = "profile_username";

    // Email
    public static final String KEY_EMAIL = "profile_email";

    // User password
    public static final String KEY_PASSWORD = "profile_password";

    public static final String FROM_GOOGLE_SIGNUP = "signUpFromGoogle";

    public static final String FROM_FACEBOOK_SIGNUP = "signUpFromFacebook";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String name, String email, String password){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // Storing password in pref
        editor.putString(KEY_PASSWORD, password);

        // Storing has logged in verification in pref
        editor.putBoolean(HAS_LOGGED_IN_FIRST_TIME_ALREADY, true);

        // commit changes
        editor.commit();
    }

    /**
     * Create login session
     * */
    public void createLoginSessionFromSocialSignIn(String name, String email){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // Storing has logged in verification in pref
        editor.putBoolean(HAS_LOGGED_IN_FIRST_TIME_ALREADY, true);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    public boolean verifyIfUserHasLoggedInAlreadyFirstTime() {

        //if SharedPreferences contains flag to check if user has logged in already
        if(pref.contains(HAS_LOGGED_IN_FIRST_TIME_ALREADY)){
            return true;
        }

        return false;
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.putBoolean(HAS_LOGGED_IN_FIRST_TIME_ALREADY, true);
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);

    }

    /**
     * Clear session details
     * */
    public void logoutUserFromGoogle(String email, boolean isLoginFromGoogle){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.putBoolean(HAS_LOGGED_IN_FIRST_TIME_ALREADY, true);
        editor.putBoolean(FROM_GOOGLE_SIGNUP, isLoginFromGoogle);
        editor.putString(KEY_EMAIL, email);
        editor.commit();

        Bundle bundle = new Bundle();

        // Save email to search when the user logs in with another account, so
        // it will be updated in his profile database
        bundle.putString(KEY_EMAIL, email);

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, VerifyLogoutActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        i.putExtras(bundle);

        // Staring Login Activity
        _context.startActivity(i);

    }

    /**
     * Clear session details
     * */
    public void logoutUserFromFacebook(String email, boolean isLoginFromFacebook){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.putBoolean(HAS_LOGGED_IN_FIRST_TIME_ALREADY, true);
        editor.putBoolean(FROM_FACEBOOK_SIGNUP, isLoginFromFacebook);
        editor.putString(KEY_EMAIL, email);
        editor.commit();

        Bundle bundle = new Bundle();

        // Save email to search when the user logs in with another account, so
        // it will be updated in his profile database
        bundle.putString(KEY_EMAIL, email);

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, VerifyLogoutActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        i.putExtras(bundle);

        // Staring Login Activity
        _context.startActivity(i);

    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
