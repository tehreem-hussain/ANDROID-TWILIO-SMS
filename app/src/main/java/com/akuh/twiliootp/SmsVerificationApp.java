package com.akuh.twiliootp;

import android.app.Application;
import com.akuh.twiliootp.helper.AppSignatureHelper;

/**
 * Created on : May 21, 2019
 * Author     : AndroidWave
 */
public class SmsVerificationApp extends Application {
  @Override public void onCreate() {
    super.onCreate();
    AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
    appSignatureHelper.getAppSignatures();
  }
}
