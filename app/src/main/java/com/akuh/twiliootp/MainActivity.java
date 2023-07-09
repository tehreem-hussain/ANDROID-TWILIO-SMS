package com.akuh.twiliootp;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.akuh.twiliootp.helper.ClientApi;
import com.akuh.twiliootp.interfaces.ApiInterface;
import com.akuh.twiliootp.interfaces.OtpReceivedInterface;
import com.akuh.twiliootp.model.OTPModel;
import com.akuh.twiliootp.receiver.SmsBroadcastReceiver;
import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
    OtpReceivedInterface, GoogleApiClient.OnConnectionFailedListener {
  private static final String TAG = "TestTag000" ;
  GoogleApiClient mGoogleApiClient;
  SmsBroadcastReceiver mSmsBroadcastReceiver;
  private int RESOLVE_HINT = 2;
  EditText inputMobileNumber, inputOtp;
  Button btnGetOtp, btnVerifyOtp;
  RelativeLayout layoutInput, layoutVerify;
  private ApiInterface apiInterface;
  private TextView textViewTimer, textView1;
  ProgressDialog progressDialog;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.act_phone_auth);



    initViews();
    // init broadcast receiver
    mSmsBroadcastReceiver = new SmsBroadcastReceiver();

    //set google api client for hint request
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .enableAutoManage(this, this)
        .addApi(Auth.CREDENTIALS_API)
        .build();

    mSmsBroadcastReceiver.setOnOtpListeners(this);
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
    getApplicationContext().registerReceiver(mSmsBroadcastReceiver, intentFilter);

    // get mobile number from phone
    getHintPhoneNumber();
    btnGetOtp.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        // Call server API for requesting OTP and when you got success start
        // SMS Listener for listing auto read message lsitner
        if(inputMobileNumber.getText().toString()!=null) {
          postRequest(inputMobileNumber.getText().toString());
        }
      }
    });

    btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        // Call server API for requesting OTP and when you got success start
        // SMS Listener for listing auto read message lsitner
       verifyOTP(inputOtp.getText().toString(),inputMobileNumber.getText().toString());
      }
    });

    textViewTimer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        postRequest(inputMobileNumber.getText().toString());
      }
    });


  }

  private void initViews() {
    inputMobileNumber = findViewById(R.id.editTextInputMobile);
    textViewTimer = findViewById(R.id.textViewTimer);
    textView1 = findViewById(R.id.textView1);
    inputOtp = findViewById(R.id.editTextOTP);
    btnGetOtp = findViewById(R.id.buttonGetOTP);
    btnVerifyOtp = findViewById(R.id.buttonVerify);
    layoutInput = findViewById(R.id.getOTPLayout);
    layoutVerify = findViewById(R.id.verifyOTPLayout);
  }

  private void timer()
  {

  }

  CountDownTimer cTimer = null;

  //start timer function
  void startTimer() {
    cTimer = new CountDownTimer(30000, 1000) {
      public void onTick(long millisUntilFinished) {
        textViewTimer.setText("Resend available in " + millisUntilFinished / 1000);
        textViewTimer.setClickable(false);

      }
      public void onFinish() {
        textViewTimer.setText("Resend code again");
        textViewTimer.setClickable(true);

      }
    };
    cTimer.start();
  }


  //cancel timer
  void cancelTimer() {
    if(cTimer!=null)
      cTimer.cancel();
  }

  @Override public void onConnected(@Nullable Bundle bundle) {

  }

  @Override public void onConnectionSuspended(int i) {

  }

  @Override public void onOtpReceived(String msg) {
    Toast.makeText(this, "Otp Received " + msg, Toast.LENGTH_LONG).show();
    Pattern mPattern = Pattern.compile("(|^)\\d{6}");

    String otp = null;
    if (msg != null) {
      Matcher mMatcher = mPattern.matcher(msg);
      if (mMatcher.find()) {
        otp = mMatcher.group(0);
        Log.i(TAG, "Final OTP: " + otp);
      } else {
        //something went wrong
        Log.e(TAG, "Failed to extract the OTP!! ");
      }
    }
    inputOtp.setText(otp);
  }

  @Override public void onOtpTimeout() {
    Toast.makeText(this, "Time out, please resend", Toast.LENGTH_LONG).show();
  }

  @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  public void startSMSListener() {
    SmsRetrieverClient mClient = SmsRetriever.getClient(this);
    Task<Void> mTask = mClient.startSmsRetriever();
    mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override public void onSuccess(Void aVoid) {
        layoutInput.setVisibility(View.GONE);
        textView1.setText("Please enter the verification code sent to "+inputMobileNumber.getText().toString());
        layoutVerify.setVisibility(View.VISIBLE);
        startTimer();
//        Toast.makeText(MainActivity.this, "SMS Retriever starts", Toast.LENGTH_LONG).show();
      }
    });
    mTask.addOnFailureListener(new OnFailureListener() {
      @Override public void onFailure(@NonNull Exception e) {
       // Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
      }
    });
  }

  public void getHintPhoneNumber() {
    HintRequest hintRequest =
        new HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build();
    PendingIntent mIntent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);
    try {
      startIntentSenderForResult(mIntent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
    } catch (IntentSender.SendIntentException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    //Result if we want hint number
    if (requestCode == RESOLVE_HINT) {
      if (resultCode == Activity.RESULT_OK) {
        if (data != null) {
          Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
          // credential.getId();  <-- will need to process phone number string
          inputMobileNumber.setText(credential.getId());
        }

      }
    }
  }


  public void postRequest(String inputMobileNumber)
  {
    showProgressbar("Getting OTP code");
    apiInterface = ClientApi.getClient().create(ApiInterface.class);
    OTPModel otpModel=new OTPModel(inputMobileNumber,"akuh-twilio-otp-android");
    Call<OTPModel> call = apiInterface.postRequestCode(otpModel);
    call.enqueue(new Callback<OTPModel>() {
      @Override
      public void onResponse(Call<OTPModel> call, Response<OTPModel> response) {

        Gson gson=new Gson();
        if (response.body().getSuccess() =="true") {
          Log.d(TAG, "Get CODE: " + String.valueOf(gson.toJson(response.body())));
          hideProgressbar();
          startSMSListener();


        } else {
          Log.d(TAG, String.valueOf(response.code()));
          Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
          hideProgressbar();
        }

      }

      @Override
      public void onFailure(Call<OTPModel> call, Throwable t) {
        Log.d(TAG, "Failure: " + t.getMessage());
        Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();

        hideProgressbar();
      }
    });

  }


  public void verifyOTP(String otp,String inputMobileNumber)
  {
    showProgressbar("Verifying OTP");
    apiInterface = ClientApi.getClient().create(ApiInterface.class);
    OTPModel otpModel=new OTPModel(inputMobileNumber,"akuh-twilio-otp-android",otp);
    Call<OTPModel> call = apiInterface.postVerifyCode(otpModel);
    call.enqueue(new Callback<OTPModel>() {
      @Override
      public void onResponse(Call<OTPModel> call, Response<OTPModel> response) {
        hideProgressbar();
        Gson gson=new Gson();
        Log.d("code0111", String.valueOf(gson.toJson(response.body())));


        if (response.body().getSuccess() == "true") {

          Intent intent=new Intent(getApplicationContext(),VerifiedSuccMainActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);


          Log.d(TAG, "Get CODE: " + String.valueOf(response.code()));
          Log.d(TAG, "Get CODE: " + String.valueOf(response));
          Log.d(TAG, "Get CODE: " + String.valueOf(response.body()));
          Toast.makeText(getApplicationContext(),"Successfully verified",Toast.LENGTH_SHORT).show();




        } else {
          Toast.makeText(getApplicationContext(),"Invalid code" ,Toast.LENGTH_SHORT).show();

          Log.d(TAG, String.valueOf(response.code()));
          Toast.makeText(getApplicationContext(),"Invalid code",Toast.LENGTH_SHORT).show();

        }



      }

      @Override
      public void onFailure(Call<OTPModel> call, Throwable t) {
        hideProgressbar();
        Log.d(TAG, "Failure: " + t.getMessage());
        Toast.makeText(getApplicationContext(),"Failure verified",Toast.LENGTH_SHORT).show();


      }
    });

  }

  private void showProgressbar(String title) {
    progressDialog = new ProgressDialog(this);
    progressDialog.setTitle(title);
    progressDialog.show();
  }

  private void hideProgressbar() {
    progressDialog.dismiss();
  }


}
