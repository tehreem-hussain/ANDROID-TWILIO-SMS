package com.akuh.twiliootp.interfaces;



public interface OtpReceivedInterface {
  void onOtpReceived(String otp);
  void onOtpTimeout();



}
