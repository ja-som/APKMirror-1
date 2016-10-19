package cf.vojtechh.apkmirror;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPrefs = getSharedPreferences("cf.vojtechh.apkmirror", MODE_PRIVATE);
        boolean darkSwitch = sharedPrefs.getBoolean("dark", true);
        boolean orientationSwitch = sharedPrefs.getBoolean("orientation", true);
        if(darkSwitch){
            this.setTheme(R.style.DarkSettings);
        }
        else{
            this.setTheme(R.style.Settings);
        }if(orientationSwitch){
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        else{
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_settings);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getResources().getColor(R.color.Recents));
            this.setTaskDescription(taskDesc);
        }


        boolean navbarSwitch = sharedPrefs.getBoolean("navcolor", true);
        if (navbarSwitch){

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            }
        }

        final Switch option1switch = (Switch) findViewById(R.id.optionswitch1);
        final Switch option2switch = (Switch) findViewById(R.id.optionswitch2);
        final Switch option3switch = (Switch) findViewById(R.id.optionswitch3);
        final Switch option4switch = (Switch) findViewById(R.id.optionswitch4);
        final Switch option5switch = (Switch) findViewById(R.id.optionswitch5);
        final Switch option6switch = (Switch) findViewById(R.id.optionswitch6);

        option1switch.setChecked(sharedPrefs.getBoolean("cache", true));
        option2switch.setChecked(sharedPrefs.getBoolean("javascript", true));
        option3switch.setChecked(sharedPrefs.getBoolean("navcolor", false));
        option4switch.setChecked(sharedPrefs.getBoolean("title", true));
        option5switch.setChecked(sharedPrefs.getBoolean("dark", true));
        option6switch.setChecked(sharedPrefs.getBoolean("orientation", true));

        //setting switch1

        option1switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    disableCache();
                }else{
                    enableCache();
                }
            }
        });

        //setting switch2

        option2switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    disableJavascript();
                }else{
                    enableJavascript();
                }
            }
        });

        //setting switch3

        option3switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    showActionbar();
                }else{
                    hideActionbar();
                }
            }
        });

        //setting switch4

        option4switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    showTitle();
                }else{
                    showFilename();
                }
            }
        });

        option5switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    darkEnabled();
                }else{
                    darkDisabled();
                }
            }
        });
        option6switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    orientationEnabled();
                    setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }else{
                    orientationDisabled();
                    setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });

    }

    public void disableCache() {

        SharedPreferences.Editor editor = getSharedPreferences("cf.vojtechh.apkmirror", MODE_PRIVATE).edit();
        editor.putBoolean("cache", true);
        editor.apply();
    }

    public void enableCache() {

        SharedPreferences.Editor editor = getSharedPreferences("cf.vojtechh.apkmirror", MODE_PRIVATE).edit();
        editor.putBoolean("cache", false);
        editor.apply();
    }

    public void disableJavascript() {
        SharedPreferences.Editor editor = getSharedPreferences("cf.vojtechh.apkmirror", MODE_PRIVATE).edit();
        editor.putBoolean("javascript", true);
        editor.apply();

    }

    public void enableJavascript() {
        SharedPreferences.Editor editor = getSharedPreferences("cf.vojtechh.apkmirror", MODE_PRIVATE).edit();
        editor.putBoolean("javascript", false);
        editor.apply();

    }

    public void showActionbar() {

        SharedPreferences.Editor editor = getSharedPreferences("cf.vojtechh.apkmirror", MODE_PRIVATE).edit();
        editor.putBoolean("navcolor", true);
        editor.apply();
        recreate();

    }

    public void hideActionbar() {
        SharedPreferences.Editor editor = getSharedPreferences("cf.vojtechh.apkmirror", MODE_PRIVATE).edit();
        editor.putBoolean("navcolor", false);
        editor.apply();
        recreate();

    }

    public void showTitle() {

        SharedPreferences.Editor editor = getSharedPreferences("cf.vojtechh.apkmirror", MODE_PRIVATE).edit();
        editor.putBoolean("title", true);
        editor.apply();

    }

    public void showFilename() {

        SharedPreferences.Editor editor = getSharedPreferences("cf.vojtechh.apkmirror", MODE_PRIVATE).edit();
        editor.putBoolean("title", false);
        editor.apply();

    }

    public void darkEnabled() {

        SharedPreferences.Editor editor = getSharedPreferences("cf.vojtechh.apkmirror", MODE_PRIVATE).edit();
        editor.putBoolean("dark", true);
        editor.apply();
        this.setTheme(R.style.DarkSettings);
        recreate();
    }

    public void darkDisabled() {

        SharedPreferences.Editor editor = getSharedPreferences("cf.vojtechh.apkmirror", MODE_PRIVATE).edit();
        editor.putBoolean("dark", false);
        editor.apply();
        this.setTheme(R.style.Settings);
        recreate();
    }

    public void orientationEnabled() {

        SharedPreferences.Editor editor = getSharedPreferences("cf.vojtechh.apkmirror", MODE_PRIVATE).edit();
        editor.putBoolean("orientation", true);
        editor.apply();

    }

    public void orientationDisabled() {

        SharedPreferences.Editor editor = getSharedPreferences("cf.vojtechh.apkmirror", MODE_PRIVATE).edit();
        editor.putBoolean("orientation", false);
        editor.apply();

    }

    public void onBackPressed() {
            Toast.makeText(this, R.string.settingsrestart, Toast.LENGTH_SHORT).show();

            Intent i = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(i);
            finish();

        }
}
