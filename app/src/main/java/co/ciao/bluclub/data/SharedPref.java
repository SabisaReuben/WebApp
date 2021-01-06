package co.ciao.bluclub.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.WeakHashMap;

import co.ciao.bluclub.BuildConfig;


public class SharedPref {

   // private Context context;
    private final SharedPreferences sharedPreferences;

    private static final String FIRST_LAUNCH = "_.FIRST_LAUNCH";
    private static final String CONNECTED_AND_VERIFIED_WITH_FIREBASE = "_.FIREBASE_CONNECTION";
    private static final String FIREBASE_EMAIL = "_.KEY_EMAIL";
    private static final String FIREBASE_PASSWORD ="_.KEY_PASSWORD";
    private static final String FIREBASE_AUTH_TOKEN ="_.KEY_AUTH_TOKEN";

    public SharedPref(Context context) {
        //this.context = context;
        sharedPreferences = context.getSharedPreferences("MAIN_PREF", Context.MODE_PRIVATE);
    }

    public void setFirstLaunch(boolean flag) {
        sharedPreferences.edit().putBoolean(FIRST_LAUNCH, flag).apply();
    }

    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(FIRST_LAUNCH, true);
    }

    public void isConnectedAndVerifiedWithFirebase() {
        sharedPreferences.getBoolean(CONNECTED_AND_VERIFIED_WITH_FIREBASE, false);
    }

    public void setConnectedAndVerifiedWithFirebase(boolean flag) {
        sharedPreferences.edit().putBoolean(CONNECTED_AND_VERIFIED_WITH_FIREBASE,false).apply();
    }

    public void setVerifiedFirebaseEmailAndPassword(String email,String password){
        sharedPreferences.edit().putString(FIREBASE_PASSWORD,password).apply();
        sharedPreferences.edit().putString(FIREBASE_EMAIL,email).apply();
    }

    public void updateAuthToken(String token) {
        sharedPreferences.edit().putString(FIREBASE_AUTH_TOKEN, token).apply();
    }

    public  String getFirebasePassword(){
        return sharedPreferences.getString(FIREBASE_PASSWORD, "");
    }

    public String getFirebaseEmail() {
        return sharedPreferences.getString(FIREBASE_EMAIL, "");
    }
}
