package com.example.alarm;


public interface IFrontCaptureCallback {

    void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults);

    public void onPhotoCaptured(String filePath);

    public void onCaptureError(int errorCode);

    public static enum ErrorCode {

    }
}