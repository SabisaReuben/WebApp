package co.ciao.bluclub.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import co.ciao.bluclub.R;
import co.ciao.bluclub.data.SharedPref;

import co.ciao.bluclub.widget.BluClubAlertDialog;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.preference.PreferenceManager;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import static co.ciao.bluclub.utils.ItemAnimation.fadeIn;
import static co.ciao.bluclub.utils.ViewAnimation.fadeOut;


public class MainActivity extends AppCompatActivity implements SharedPreferences
        .OnSharedPreferenceChangeListener {

    /**
     * tag to log events
     **/
    private static final String TAG = MainActivity.class.getName();

    /**key to persist the {@link this#isFirstLaunch} when activity goes back stack**/
    private static final String LAUNCH_KEY = "key.LAUNCH";

    /*** file request code**/
    private static final int FILE_REQUEST_CODE = 0;

    /*** Tag for dialog displaying connection error**/
    private static final String NO_INTERNET_DIALOG_TAG = "dialog_no_internet";

    /*** The key to add or retrieve extra data to the intent.Use as key for Url*/
    private static final String EXTRA_OBJC = "key.EXTRA_OBJC";

    /*** adds or retrieves data showing if activity is launched fro first time*/
    private static final String EXTRA_NOTIF = "key.EXTRA_FROM_NOTIF";

    /**
     * Use the method to navigate the through the app
     *
     * @param activity   The activity requesting navigation
     * @param url        Url of page to be loaded
     * @param from_notif true if launched from notification, otherwise false
     * @see this#navigateBase(Context, String, boolean)
     */
    public static void navigate(Activity activity, String url, boolean from_notif) {
        Intent i = navigateBase(activity, url, from_notif);
        activity.startActivity(i);
    }

    /**
     * The method prepares intent that can be used for navigation
     *
     * @param context    The {@link Context}
     * @param url        url of the page to be loaded
     * @param from_notif true if launched from notification bar
     * @return Intent
     * @see this#navigate(Activity, String, boolean)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Intent navigateBase(Context context, String url, boolean from_notif) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_OBJC, url);
        intent.putExtra(EXTRA_NOTIF, from_notif);
        return intent;
    }

    /**
     * default Url to load
     */
    private String url;

    /*** launched from notification*/
    private boolean from_notif = false;

    /*** The app is launched for the firstTime*/
    private boolean isFirstLaunch = true;

    private boolean isConnected;

    private WebView webView;
    private ProgressBar progressBar;

    private View errorLayout, launcherLayout, mainContent, progress_launcher_determinate,
            progress_launcher_indeterminate;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView errorTextView;

    private ValueCallback<Uri[]> valueCallback;

    private String mCameraPhotoPath;

    private ConnectivityManager manager;

    private SharedPreferences sharedPreferences;
    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager
            .NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            isConnected = true;
            super.onAvailable(network);
        }

        @Override
        public void onLost(@NonNull Network network) {
            isConnected = false;
            super.onLost(network);

        }

        @Override
        public void onUnavailable() {
            isConnected = false;
            super.onUnavailable();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == FILE_REQUEST_CODE) {
                //uncomment this if intent was created using fileChooserParams.createIntent();
                // Uri[] uris = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
                //  if (uris == null || uris.length == 0) {
                //  Toast.makeText(this, "Zero file selected", Toast.LENGTH_SHORT).show();
                // }
                // valueCallback.onReceiveValue(uris);
                if (valueCallback == null) {
                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                }
                Uri[] results = null;
                //if there is no data then the image was taken
                if (null == data) {
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    //file was chosen
                    results = new Uri[]{Uri.parse(data.getDataString())};
                }
                //pass the values to the callback
                valueCallback.onReceiveValue(results);
                valueCallback = null; //release
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString("theme", "Light");
        //restore the values
        if(savedInstanceState!=null) isFirstLaunch = savedInstanceState.getBoolean(LAUNCH_KEY);
        
        setTheme(theme);

        setContentView(R.layout.activity_main);
        SharedPref sharedPref = new SharedPref(this);
        boolean firstLaunch = sharedPref.isFirstLaunch();
        //TODO:ensure the launch value in sharedPref is updated in corresponding launcher activity
        if(firstLaunch){
            Intent intent = new Intent(this, LauncherActivity.class);
            startActivity(intent);
        }
        FirebaseMessaging.getInstance().subscribeToTopic("topic").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Topic subscription successful");
            } else  Log.d(TAG, "Topic subscription failed");
        });
        
        webView.getSettings().setAppCacheEnabled(!sharedPreferences
                .getBoolean("cache", true));

        manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        init();
        //called if activity is launched through intent
        handleIntent(getIntent());
    }

    private void handleIntent(@Nullable Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            //avoid updating url with null value
            if ((intent.getExtras().getString(EXTRA_OBJC) != null)) {
                from_notif = intent.getExtras().getBoolean(EXTRA_NOTIF);
                intent.getExtras().getString(EXTRA_OBJC);
            }else{
                //use the default url
                url = sharedPreferences.getString("countryUrl", "https://www.truehost.co.ke");
            }

        }
    }

    public void init() {
        webView = findViewById(R.id.main_webView);
        progressBar = findViewById(R.id.progressBar);
        errorLayout = findViewById(R.id.lyt_error);
        launcherLayout = findViewById(R.id.lyt_launcher);
        mainContent = findViewById(R.id.lyt_main);
        swipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        errorTextView = findViewById(R.id.error_TextView);

        findViewById(R.id.bt_retry).setOnClickListener(v -> loadWebFromUrl());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            isFirstLaunch = false;
            webView.reload();
        });
        progress_launcher_determinate = findViewById(R.id.progress_launcher_horizontal);
        progress_launcher_indeterminate = findViewById(R.id.progress_launcher_indeterminate);
        findViewById(R.id.fab_settings).setOnClickListener(v -> startActivity(new Intent(
                this, SettingsActivity.class)));

        url = sharedPreferences.getString("countryUrl", "https://www.truehost.co.ke");

    }

    private void setTheme(String theme) {
        if (theme.equals("Dark")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                webView.setForceDarkAllowed(true);
            }
            getApplication().setTheme(R.style.BaseTheme_Dark);
        } else {
            getApplication().setTheme(R.style.BaseTheme);

        }
    }

    /*** The method load the requested pages into the webView*/
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void loadWebFromUrl() {
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(this), "android");
        webSetting.setDefaultTextEncodingName("utf-8");
        webSetting.setBuiltInZoomControls(false);
        webSetting.setDisplayZoomControls(false);
        webSetting.setLoadsImagesAutomatically(true);
        webSetting.setAllowContentAccess(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.setWebViewClient(new WebViewClient() {


            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (!isConnected) {
                    Toast no_connection = Toast.makeText(MainActivity.this,
                            "No connection", Toast.LENGTH_SHORT);
                    no_connection.setGravity(Gravity.CENTER, 0, 0);
                    no_connection.show();
                    //avoid loading the url
                    return true;
                }
                String webViewUrl = request.getUrl().getHost();
                if (webViewUrl.contains("facebook") || webViewUrl.contains("whatsApp"))
                    return startBrowser(webViewUrl);

                //update the url for retries initiated through loadWebFromUrl()
                url = webViewUrl;
                return false;
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (isFirstLaunch) {
                    //ensure progress View are into default visibility state
                    //progress_launcher_determinate & progress_launcher_indeterminate change
                    //toggle their visibility on progress 60 check WebChromeClient#onProgressChanged
                    //this is to restore the default state in case of retry
                    progress_launcher_determinate.setVisibility(View.GONE);
                    progress_launcher_indeterminate.setVisibility(View.VISIBLE);
                    return;
                }
                fadeOut(errorLayout);
                fadeIn(mainContent);
                fadeIn(progressBar);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefreshLayout.setRefreshing(false);
                if (isFirstLaunch) {
                    fadeIn(mainContent);
                    fadeOut(launcherLayout);
                    isFirstLaunch = false;
                    return;
                }
                fadeOut(progressBar);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.cancel();
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request,
                                        WebResourceError error) {
                view.loadUrl("about:blank");
                handleError(error.getErrorCode());
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                                        String failingUrl) {
                view.loadUrl("about:blank");
                handleError(errorCode);
            }

        });
        webView.setDownloadListener(
                (url1, userAgent, contentDisposition, mimetype, contentLength) -> {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url1));
                    String cookie = CookieManager.getInstance().getCookie(url1);
                    request.addRequestHeader("Cookie", cookie);
                    request.addRequestHeader("user-agent", userAgent);
                    request.setMimeType(mimetype);
                    request.setDescription("Downloading a file");
                    request.allowScanningByMediaScanner();
                    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    downloadManager.enqueue(request);

                });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (isFirstLaunch && progress > 60) {
                    //ensure animation occurs first time only
                    if (progress_launcher_determinate.getVisibility() != View.VISIBLE) {
                        fadeIn(progress_launcher_determinate);
                        fadeOut(progress_launcher_indeterminate);
                    }
                    ((ProgressBar) progress_launcher_determinate).setProgress(progress);
                }
            }

            @SuppressLint("MissingPermission")
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                //double check to ensure we don't have any existing callback
                if ((valueCallback != null)) {
                    valueCallback.onReceiveValue(null);
                }
                valueCallback = filePathCallback;
                //Though this method works doesn't guarantee if image capture
                //Intent intent = fileChooserParams.createIntent();
                // if (intent.resolveActivity(getPackageManager()) != null) {
                //startActivityForResult(intent, FILE_REQUEST_CODE);
                ///  }
                //onActivityForResult you can add the file chose to callback using
                //valueCallback.onReceiveValue(parseResult(resultCode,data))
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                try {
                    photoFile = createFile();
                } catch (IOException e) {
                    Log.d(TAG, "image camera file failed " + e.getMessage());
                }
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                } else takePhotoIntent = null;

                Intent contentIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentIntent.setType("image/*");
                Intent[] intentArray;
                //ensure file was created and the device has ability to capture the image i.e has Camera
                if (takePhotoIntent != null && takePhotoIntent.resolveActivity(getPackageManager())
                        != null) {
                    intentArray = new Intent[]{takePhotoIntent, contentIntent};
                } else intentArray = new Intent[]{};

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, FILE_REQUEST_CODE);
                return true;
            }
        });
        webView.loadUrl(url);
    }


    /*** @param errorCode {@link WebViewClient} error codes*/
    private void handleError(int errorCode) {
        swipeRefreshLayout.setRefreshing(false);
        webView.goBack();
        switch (errorCode) {
            case WebViewClient.ERROR_CONNECT:
                if (isFirstLaunch) {
                    showDialog("No internet", "No internet connections found." +
                            "Check your connection or try again", R.drawable.ic_no_internet);
                    return;
                }
                swipeMainContentForError("No internet connections found." +
                        " Check your connection or try again");
                break;
            case WebViewClient.ERROR_TIMEOUT:
                Toast.makeText(this, "Connection Timeout", Toast.LENGTH_SHORT).show();
                if (isFirstLaunch) {
                    showDialog("Timeout", "Connection timeout! ", 0);
                    return;
                }
                swipeMainContentForError("Connection Timeout");
                break;
            case WebViewClient.ERROR_FAILED_SSL_HANDSHAKE:
                if (isFirstLaunch) {
                    showDialog("Connection", "Unable to establish " +
                            "connection with server! ", 0);
                    return;
                }
                swipeMainContentForError("Failed to connect with server");
                break;
            case WebViewClient.ERROR_FILE_NOT_FOUND:
                if (isFirstLaunch) {
                    showDialog("File error", "File not found ", 0);
                    return;
                }
                swipeMainContentForError("File not find");
                break;
            case WebViewClient.ERROR_FILE:
                Toast.makeText(this, "error while handling the file ",
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                if (isFirstLaunch) {
                    showDialog(null, null, 0);
                    return;
                }
                swipeMainContentForError(null);
        }
    }


    @Override
    public void onBackPressed() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (webView.canGoBack()) webView.goBack();
        else doExitApp();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(LAUNCH_KEY, isFirstLaunch);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        webView.onResume();
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        manager.registerNetworkCallback(request, networkCallback);
        loadWebFromUrl();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        webView.onPause();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        manager.unregisterNetworkCallback(networkCallback);
        super.onPause();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "countryUrl":
                reInitializeApp();
                break;
            case "theme":
                setTheme(sharedPreferences.getString(key, "Light"));
                break;
            case "cache":
                webView.clearCache(sharedPreferences.getBoolean(key, false));
                Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show();
                break;
            case "passwords":
                if (sharedPreferences.getBoolean(key, false)) {
                    webView.clearFormData();
                    webView.clearHistory();
                    Toast.makeText(this, "All data cleared", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void reInitializeApp() {
        webView.destroy();
        recreate(); //TODO test this configuration
        Configuration configuration = new Configuration();
        getApplication().onConfigurationChanged(configuration);

    }

    static class WebAppInterface {
        private final Context context;

        public WebAppInterface(Context context) {
            this.context = context;
        }

        @SuppressWarnings("unused")
        @JavascriptInterface
        public void showToast(String message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "Press again to exit app", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }


    /* show ads */
    @SuppressWarnings("unused")
    public boolean showInterstitial() {
        return false;
    }


    public static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }

    /**
     * THe method handles external links not part of the application. The user is prompted to open on
     * a favorite browser.
     *
     * @param url The url address of the external link.
     * @return always true. The return value of this#shouldOverrideUrlLoading. Used as shorthand
     */
    private boolean startBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        Intent chooserIntent = Intent.createChooser(intent, "Choose a browser");
        startActivity(chooserIntent);
        return true;
    }

    /**
     * The method animates the UI change between the main content and error layout.
     *
     * @param error The error message to be displayed on the UI
     */
    private void swipeMainContentForError(String error) {
        if (error == null || error.isEmpty()) error = "Unknown error occurred";
        errorTextView.setText(error);
        fadeOut(mainContent);
        fadeIn(errorLayout);
    }

    private void showDialog(@Nullable String dialogTitle, @Nullable String errorMessage
            , @DrawableRes int icon) {
        //show dialog instead NB/ ensure only one instance available os visible
        BluClubAlertDialog dialog = BluClubAlertDialog.newInstance(
                dialogTitle == null ? "Error Occurred" : dialogTitle,
                errorMessage == null ? "Unknown error" : errorMessage,
                icon == 0 ? R.drawable.ic_error : icon,
                "Retry", "Exit"
        );
        dialog.setCallback(new BluClubAlertDialog.Callback() {
            @Override
            public void onPositiveClick(DialogInterface dialogInterface) {
                isFirstLaunch = true;
                loadWebFromUrl();
                //dismiss the dialog
                dialogInterface.dismiss();
            }

            @Override
            public void onNegativeClick(DialogInterface dialogInterface) {
                //dismiss the fragment and close the app
                dialogInterface.dismiss();
                finish();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(NO_INTERNET_DIALOG_TAG);
        //ensure only one dialog instance is visible
        if (fragment != null) fragmentTransaction.remove(fragment);

        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();
        dialog.show(fragmentManager, NO_INTERNET_DIALOG_TAG);
    }

    /**
     * @return File instance
     * @throws IOException if cannot be able to create the file
     */
    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    private File createFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timestamp = new SimpleDateFormat(
                "yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPG_" + timestamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

}

