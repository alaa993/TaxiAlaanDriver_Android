package com.taxialaan.drivers.GPS;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.widget.Button;
import android.widget.TextView;

import com.taxialaan.drivers.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class PermissionGroup {

    //region Variables
    public Context ctx;
    private Activity activity;
    static final String PREFS_NAME = "runtimePreference";


    private String[] ManifestPermissionArray;
    private int MY_PERMISSIONS_REQUEST_CODE;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ArrayList<String> permissionsNeeded;
    public PermissionRequestListener requestListener;

    //**
    private String alertExplanation_title = "مجوز ها";
    private String alertExplanation_message = "این برنامه نیاز به مجوز ها دارد لطفا تایید کنید";
    private String alertExplanation_positive = "باشه";
    private String alertExplanation_negative = "خیر";

    private String alertForIntentToSetting_title = "مجوز ها";
    private String alertForIntentToSetting_message = "برنامه برای ادامه فعالیت خود نیاز به برخی مجوز ها دارد. در ادامه به بخش تنظیمات برنامه، بخش مجوز ها رفته و مجوز ها را فعال کنید";
    private String alertForIntentToSetting_positive = "باشه";
    private String alertForIntentToSetting_negative = "خیر";

    Drawable imgAlertExplanation;
    Drawable imgAlertForIntentToSetting;

    boolean isActivity;
    Fragment fragment;
    boolean isShownDontAskRequest=false;


    //endregion

    //==================================== constructor ====================================
    public PermissionGroup(Context ctx, boolean isActivity, Fragment fragment, Activity activity, String[] ManifestPermissionArray, int requestCode) {


        this.ctx = ctx;
        this.isActivity=isActivity;
        this.activity = activity;
        this.fragment=fragment;
        this.ManifestPermissionArray = ManifestPermissionArray;
        sharedPref = ctx.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = sharedPref.edit();
        this.MY_PERMISSIONS_REQUEST_CODE = requestCode;
    }

    //===================================== main method ===================================
    public void showGroupPermissionFunction(PermissionRequestListener requestListener) {
        this.requestListener = requestListener;
        if (checkHasPermission()) {
            requestListener.onGranted();
        } else {
            //------------------------
            if (checkShouldShowRequestPermissionRationale()) {
                showPermissionExplanation();
            } else if (!checkPermissionPreference(permissionsNeeded)) {
                updatePermissionPreference(permissionsNeeded);
                requestPermission();

            } else {
                showPermissionExplanationForDoNotASkAgain();
            }
            //------------------------
        }
    }

    //======================================= check Permission ==============================
    private  boolean checkHasPermission() {
        permissionsNeeded = new ArrayList<>();

        for (String permission : ManifestPermissionArray) {
            if (ContextCompat.checkSelfPermission(ctx, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }
        return permissionsNeeded.size() <= 0;

    }

    private void requestPermission() {
        String[] permissionArray = new String[permissionsNeeded.size()];
        for (int i = 0; i < permissionsNeeded.size(); i++) {
            permissionArray[i] = permissionsNeeded.get(i);
        }

        if (isActivity) {
            ActivityCompat.requestPermissions(activity, permissionArray, MY_PERMISSIONS_REQUEST_CODE);
        }else {
            fragment.requestPermissions(permissionArray, MY_PERMISSIONS_REQUEST_CODE);
        }
    }

    private boolean checkShouldShowRequestPermissionRationale() {
        boolean shouldShow = false;
        ArrayList<Boolean> theList = new ArrayList<>();
        /*
         To help find situations where the user might need an explanation, Android provides a utiltity method,
          shouldShowRequestPermissionRationale(). This method returns true if the app has requested this permission previously and
          the user denied the request.
          Note: If the user turned down the permission request in the past and chose the Don't ask again option in
          the permission request system dialog, this method returns false. The method also returns false if
          a device policy prohibits the app from having that permission.
         */
        for (String permission : permissionsNeeded) {
            if (isActivity) {
                theList.add(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission));
            }else {
                theList.add(fragment.shouldShowRequestPermissionRationale( permission));
            }
        }

        if (!theList.contains(false)) {
            shouldShow = true;
        } else {
            for (int i = 0; i < theList.size(); i++) {

                if (!theList.get(i) && sharedPref.getBoolean(permissionsNeeded.get(i), false)) {
                    shouldShow = false;
                    break ;
                } else if (theList.get(i)) {
                    shouldShow = true;
                }
            }
        }

        return shouldShow;
    }

    private void showPermissionExplanation() {

        AlertDialogCallBack dialogCallBack = new AlertDialogCallBack() {
            @Override
            public void onPositive() {
                requestPermission();
            }

            @Override
            public void onNegative() {
                requestListener.onDenied();
            }
        };

        showAlertDialog(imgAlertExplanation, alertExplanation_title, alertExplanation_message, alertExplanation_positive, alertExplanation_negative, dialogCallBack);

    }

    private void showPermissionExplanationForDoNotASkAgain() {


        AlertDialogCallBack dialogCallBack = new AlertDialogCallBack() {
            @Override
            public void onPositive() {
                //============
                isShownDontAskRequest=true;
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", ctx.getPackageName(), null);
                intent.setData(uri);
                ctx.startActivity(intent);
                //==========
            }

            @Override
            public void onNegative() {
                requestListener.onDenied();
            }
        };

        showAlertDialog(imgAlertForIntentToSetting, alertForIntentToSetting_title, alertForIntentToSetting_message, alertForIntentToSetting_positive, alertForIntentToSetting_negative, dialogCallBack);

    }//

    public boolean isIntentAndHasPermission(){
        boolean isOk=false;
        if (isShownDontAskRequest && checkHasPermission()){
            isOk=true;
        }
        return isOk;
    }
    //=====================================================================================

    public void setAlertDialogExplanationText(Drawable img, String title, String message, String positive, String negative) {
        imgAlertExplanation = img;
        if (title != null) alertExplanation_title = title;
        if (message != null) alertExplanation_message = message;
        if (positive != null) alertExplanation_positive = positive;
        if (negative != null) alertExplanation_negative = negative;

    }

    public void setDoNotASkAgainAlertDialogText(Drawable img, String title, String message, String positive, String negative) {
        imgAlertForIntentToSetting = img;
        if (title != null) alertForIntentToSetting_title = title;
        if (message != null) alertForIntentToSetting_message = message;
        if (positive != null) alertForIntentToSetting_positive = positive;
        if (negative != null) alertForIntentToSetting_negative = negative;

    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CODE && isSameList(permissions)) {
            if (grantResults.length > 0 && isAllGranted(grantResults)) {
                requestListener.onGranted();
            } else {
                requestListener.onDenied();
            }
        }//
    }

    private boolean isSameList(String[] permissions) {
        boolean isSameList = false;
        if (permissionsNeeded.size() == permissions.length) {
            isSameList = true;
            for (String permission : permissions) {
                if (!permissionsNeeded.contains(permission)) {
                    isSameList = false;
                    break;
                }
            }
        }
        return isSameList;
    }

    private boolean isAllGranted(int[] grantResults) {
        boolean isAllGranted = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
                break;
            }
        }
        return isAllGranted;
    }

    //===================================== Dialog =============================
   private void showAlertDialog( Drawable img, String title, String message, String positiveText, String negativeText, final AlertDialogCallBack callBack) {


//        Typeface permissionTypeFace=G.typeFacePersian;

        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(false);
        builder.setTitle(title);
        if (img!=null){
            builder.setIcon(img);
        }
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callBack.onPositive();
            }
        });

        builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callBack.onNegative();
            }
        });

        AlertDialog dialog=builder.create();

        dialog.show();

        // Getting the view elements
        try {
            if (dialog.getWindow()!=null){
                TextView textView = dialog.getWindow().findViewById(android.R.id.message);
                TextView alertTitle =  dialog.getWindow().findViewById(R.id.alertTitle);
                Button button1 =  dialog.getWindow().findViewById(android.R.id.button1);
                Button button2 =  dialog.getWindow().findViewById(android.R.id.button2);

//                textView.setTypeface(permissionTypeFace);
//                alertTitle.setTypeface(permissionTypeFace, Typeface.BOLD);
//                button1.setTypeface(permissionTypeFace, Typeface.BOLD);
//                button2.setTypeface(permissionTypeFace, Typeface.BOLD);
            }

        }catch (Exception e){
//            G.log("alertdialogMessage",e.getMessage());
        }

    }


    //===================================== shared preferences =============================
    private void updatePermissionPreference(ArrayList<String> permissionListForUpdate) {

        for (String permission : permissionListForUpdate) {
            editor.putBoolean(permission, true);
            editor.commit();
        }

    }

    private boolean checkPermissionPreference(ArrayList<String> permissionListForCheck) {
        boolean showBefore = false;
        for (String permission : permissionListForCheck) {
            if (sharedPref.getBoolean(permission, false)) {
                showBefore = true;
                break;
            }
        }
        return showBefore;
        //second parameter of getBoolean() is for Value to return if this preference does not exist.
        //This value may be null.
    }

    //================================= interface =========================================
    public interface PermissionRequestListener {
        void onGranted();

        void onDenied();
    }
    public interface AlertDialogCallBack {

        void onPositive();
        void onNegative();
    }

    //====================================== END CLASS =======================================
}//end class