package com.hm.hmzxing;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hm.hmzxinglibrary.CaptureActivity;
import com.hm.hmzxinglibrary.Intents;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    public static final int REQUEST_CODE = 14;
    private TextView textResult;
    private Button btnCaptureOnce;
    private Button btnCaptureRepeat;
    private String[] RQ_PREMS = new String[]{Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textResult = (TextView) findViewById(R.id.textResult);
        btnCaptureOnce = (Button) findViewById(R.id.btn_capture_once);
        btnCaptureRepeat = (Button) findViewById(R.id.btn_capture_repeat);
        btnCaptureOnce.setOnClickListener(this);
        btnCaptureRepeat.setOnClickListener(this);
    }

    public void captureOnce() {
        Intent intent = new Intent(CaptureActivity.ACTION);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void captureRepeat() {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                String result = data.getStringExtra(Intents.Scan.RESULT);
                textResult.setText(result);
            }
        }

    }

    @Override
    public void onClick(View v) {
        if (EasyPermissions.hasPermissions(this, RQ_PREMS)) {
            switch (v.getId()) {
                case R.id.btn_capture_once:
                    captureOnce();
                    break;
                case R.id.btn_capture_repeat:
                    captureRepeat();
                    break;
                default:
                    break;
            }
        } else {
            EasyPermissions.requestPermissions(this, "need camera permissions", REQUEST_CODE, RQ_PREMS);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Toast.makeText(this, "camera permission is granted now", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setRationale("you need set the camera permission")
                    .setRequestCode(AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE)
                    .setNegativeButton("cancel")
                    .setPositiveButton("go setting")
                    .build();
        }

    }


}
