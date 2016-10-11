package cf.vojtechh.apkmirror;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.view.KeyEvent;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;

public class MainActivity extends AppCompatActivity  {
    private final static int REQUEST_WRITE_STORAGE_RESULT = 112;
    public WebView mWebView;
    private SwipeRefreshLayout swipeRefreshLayout;
    public ProgressBar Pbar;
    String url;
    private static final String TAG = "mWebView";


    @SuppressLint("SetJavaScriptEnabled")

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Pbar = (ProgressBar) findViewById(R.id.loading);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getResources().getColor(R.color.Recents));
            this.setTaskDescription(taskDesc);
        }

        final ImageButton settingsbutt = (ImageButton) findViewById(R.id.settingsButton);

        //settings button onclick listener
        settingsbutt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSettings();
            }
        });

        //checking for permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {}
            else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, R.string.storage_access, Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE_RESULT);
            }
        }
        createBottomBar();
        //setting webview
        url = "http://www.apkmirror.com/";
        mWebView = (WebView) findViewById(R.id.apkmirror);
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(false);
        mWebView.getSettings().setSaveFormData (true);
        mWebView.getSettings().setSavePassword(true);
        mWebView.getSettings().setUserAgentString("user-agent-string");
        mWebView.setWebViewClient(new mWebClient());
        mWebView.setWebChromeClient(new mChromeClient());
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieSyncManager.createInstance(MainActivity.this);
        CookieSyncManager.getInstance().startSync();
        mWebView.loadUrl(url);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();

                //i know this is wrong, im sorry but idk how to do it
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }

        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent, R.color.colorAccent);


        //checking if shared preferences exist
        File f = new File(getFilesDir().getPath(), "../shared_prefs/cf.vojtechh.apkmirror.xml");
        if (!f.exists()){
            SharedPreferences.Editor editor = getSharedPreferences("cf.vojtechh.apkmirror", MODE_PRIVATE).edit();
            editor.putBoolean("cache", false);
            editor.putBoolean("javascript", false);
            editor.putBoolean("navcolor", true);
            editor.putBoolean("title", false);
            editor.putBoolean("dark", false);
            editor.putBoolean("orientation", false);
            editor.apply();
        }

        SharedPreferences prefs = getSharedPreferences("cf.vojtechh.apkmirror", MODE_PRIVATE);
        boolean cacheSwitch = prefs.getBoolean("cache", false);
        boolean javascriptSwitch = prefs.getBoolean("javascript", false);
        boolean navbarSwitch = prefs.getBoolean("navcolor", true);
        final boolean titleSwitch = prefs.getBoolean("title", false);
        boolean orientationSwitch = prefs.getBoolean("orientation", false);

        if(cacheSwitch){
            mWebView.getSettings().setAppCacheEnabled(false);
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            mWebView.clearCache(true);
            mWebView.clearHistory();
        }
        else{
            mWebView.getSettings().setAppCacheEnabled(true);
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            mWebView.clearCache(false);
            mWebView.clearHistory();
        }

        if (javascriptSwitch){

            mWebView.getSettings().setJavaScriptEnabled(false);
        }
        else {

            mWebView.getSettings().setJavaScriptEnabled(true);
        }

        if (navbarSwitch){

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            }
        }

        if (orientationSwitch){
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        else {
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimetype, final long contentLength) {
                final String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
                android.util.Log.d("Applog", "fileName:" + fileName);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType("application/vnd.android.package-archive");
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                final String dwn = getResources().getString(R.string.download);
                final View.OnClickListener opendown = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (titleSwitch){
                            String regex = "\\bAPK\\b";
                            String regex2 = "\\bDownload\\b\\s*";
                            String title = mWebView.getTitle();
                            String title1 = title.replaceAll(regex, "");
                            String title2 = title1.replaceAll(regex2, "");
                            File apkfile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_DOWNLOADS  + title2 + ".apk");
                            Intent i = new Intent();
                            i.setAction(android.content.Intent.ACTION_VIEW);
                            i.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
                            startActivity(i);

                        }else {
                            File apkfile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_DOWNLOADS + fileName);
                            Intent i = new Intent();
                            i.setAction(android.content.Intent.ACTION_VIEW);
                            i.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
                            startActivity(i);
                        }
                    }
                };
                BroadcastReceiver onComplete=new BroadcastReceiver() {
                    public void onReceive(Context ctxt, Intent intent) {
                        if (titleSwitch){
                            String regex = "\\bAPK\\b";
                            String regex2 = "\\bDownload\\b\\s*";
                            String title = mWebView.getTitle();
                            String title1 = title.replaceAll(regex, "");
                            String title2 = title1.replaceAll(regex2, "");

                            Snackbar.make(findViewById(android.R.id.content), dwn + " " + title2, Snackbar.LENGTH_LONG)
                                    //.setAction(R.string.open, opendown)
                                    .show();

                        }else {
                            Snackbar.make(findViewById(android.R.id.content), dwn + " " + fileName , Snackbar.LENGTH_LONG)
                                    //.setAction(R.string.open, opendown)
                                    .show();
                        }
                    }
                };
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                if (titleSwitch){
                    String regex = "\\bAPK\\b";
                    String regex2 = "\\bDownload\\b\\s*";
                    String title = mWebView.getTitle();
                    String title1 = title.replaceAll(regex, "");
                    String title2 = title1.replaceAll(regex2, "");

                    request.setTitle(title2 + ".apk");

                }

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

                manager.enqueue(request);
            }
        });

        //initializing bottom bar
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_homepage) {
                    if (findViewById(R.id.splash_screen).getVisibility() == View.VISIBLE) {
                        return;
                    }
                    if (findViewById(R.id.splash_screen).getVisibility() == View.GONE) {
                        mWebView.loadUrl(url);
                    }
                    mWebView.loadUrl(url);
                }if (tabId == R.id.tab_devs) {
                    mWebView.loadUrl("http://www.apkmirror.com/developers/");
                }if (tabId == R.id.tab_upload) {
                    mWebView.loadUrl("http://www.apkmirror.com/uploads/");
                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_homepage) {
                    mWebView.scrollTo(0,0);
                }if (tabId == R.id.tab_devs) {
                    mWebView.scrollTo(0,0);
                }if (tabId == R.id.tab_upload) {
                    mWebView.scrollTo(0,0);
                }
            }
        });

    }
    private class mWebClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            if (findViewById(R.id.splash_screen).getVisibility() == View.VISIBLE) {
                // show webview
                findViewById(R.id.main_view).setVisibility(View.VISIBLE);
                // hide splash screen
                findViewById(R.id.splash_screen).setVisibility(View.GONE);

            }
            findViewById(R.id.loading).setVisibility(View.GONE);
            CookieSyncManager.getInstance().sync();
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            findViewById(R.id.loading).setVisibility(View.VISIBLE);
        }
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!url.contains("apkmirror.com")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (Exception exc) {
                    Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            else {
                mWebView.loadUrl(url);
                return true;
            }
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            final Uri uri = request.getUrl();
            return handleUri(uri);
        }
        private boolean handleUri(final Uri uri) {
            Log.i(TAG, "Uri =" + uri);
            final String host = uri.getHost();
            final String scheme = uri.getScheme();
            // Based on some condition you need to determine if you are going to load the url
            // in your web view itself or in a browser.
            // You can use `host` or `scheme` or any part of the `uri` to decide.
            if (url.contains("apkmirror.com")) {
                // Returning false means that you are going to load this url in the webView itself
                return false;
            } else {
                // Returning true means that you need to handle what to do with the url
                // e.g. open web page in a Browser
                final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }
        }
    }

    private class mChromeClient extends WebChromeClient {
            public void onProgressChanged(WebView view, int progress) {
                if(progress < 100 && Pbar.getVisibility() == ProgressBar.GONE){
                    Pbar.setVisibility(ProgressBar.VISIBLE);
                }
                Pbar.setProgress(progress);
                if(progress == 100) {
                    Pbar.setVisibility(ProgressBar.GONE);
                }
        }
    }

    //settings start function
    public void openSettings(){
        Intent i = new Intent
                (MainActivity.this, SettingsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();

    }
    //back key
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }


    //requesting the permission to write to external storage
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == REQUEST_WRITE_STORAGE_RESULT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            }else{
                Toast.makeText(this,R.string.storage_access_denied, Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void createBottomBar(){
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_homepage) {
                    if (findViewById(R.id.splash_screen).getVisibility() == View.VISIBLE) {
                        return;
                    }
                    if (findViewById(R.id.splash_screen).getVisibility() == View.GONE) {
                        mWebView.loadUrl("http://www.apkmirror.com/");
                    }
                    mWebView.loadUrl("http://www.apkmirror.com/");
                }if (tabId == R.id.tab_devs) {
                    mWebView.loadUrl("http://www.apkmirror.com/developers/");
                }if (tabId == R.id.tab_upload) {
                    mWebView.loadUrl("http://www.apkmirror.com/uploads/");
                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_homepage) {
                    mWebView.scrollTo(0,0);
                }if (tabId == R.id.tab_devs) {
                    mWebView.scrollTo(0,0);
                }if (tabId == R.id.tab_upload) {
                    mWebView.scrollTo(0,0);
                }
            }
        });

    }

}
