package com.cradle.neptune.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.cradle.neptune.R;
import com.cradle.neptune.dagger.MyApp;
import com.cradle.neptune.model.Patient;
import com.cradle.neptune.model.Reading;
import com.cradle.neptune.model.ReadingManager;
import com.cradle.neptune.model.Settings;
import com.cradle.neptune.model.UrineTestResult;
import com.cradle.neptune.utilitiles.DateUtil;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;


public class LoginActivity extends AppCompatActivity {

    public static final String LOGIN_EMAIL = "loginEmail";
    public static final String LOGIN_PASSWORD = "loginPassword";
    public static final String TOKEN = "token";
    public static final String AUTH = "Authorization";
    public static final String USER_ID = "userId";
    public static final String DEFAULT_EMAIL = "";
    public static final int DEFAULT_PASSWORD = -1;
    public static final String DEFAULT_TOKEN = null;
    public static final String AUTH_PREF = "authSharefPref";
    public static int loginBruteForceAttempts =3;

    @Inject
    ReadingManager readingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ((MyApp) getApplication()).getAppComponent().inject(this);

        checkSharedPrefForLogin();
        setupLogin();
    }

    private void checkSharedPrefForLogin() {
        SharedPreferences sharedPref = this.getSharedPreferences(AUTH_PREF, Context.MODE_PRIVATE);
        String email = sharedPref.getString(LOGIN_EMAIL, DEFAULT_EMAIL);
        int password = sharedPref.getInt(LOGIN_PASSWORD, DEFAULT_PASSWORD);
        if (!email.equals(DEFAULT_EMAIL) && password!=DEFAULT_PASSWORD) {
            startIntroActivity();
        }

    }

    private void startIntroActivity() {
        Intent intent = new Intent(LoginActivity.this, IntroActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupLogin() {
        EditText emailET = findViewById(R.id.emailEditText);
        EditText passwordET = findViewById(R.id.passwordEditText);
        TextView errorText = findViewById(R.id.invalidLoginText);
        Button loginbuttoon = findViewById(R.id.loginButton);

        loginbuttoon.setOnClickListener(v -> {
            if (loginBruteForceAttempts <= 0) {
                startIntroActivity();
                return;
            }
            loginBruteForceAttempts--;
            ProgressDialog progressDialog = getProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(MyApp.getInstance());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", emailET.getText());
                jsonObject.put("password", passwordET.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Settings.authServerUrl, jsonObject, response -> {
                progressDialog.cancel();
                //put it into sharedpress for offline login.
                //saveUserNamePasswordSharedPref(emailET.getText().toString(), passwordET.getText().toString());
                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_LONG).show();
                try {
                    SharedPreferences sharedPref = LoginActivity.this.getSharedPreferences(AUTH_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(TOKEN, response.getString(TOKEN));
                    editor.putString(USER_ID, response.getString("userId"));
                    editor.apply();
                    String token = response.get(TOKEN).toString();
                   getAllMyPatients(token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //get all patients by this person
                startIntroActivity();
            }, error -> {
                errorText.setVisibility(View.VISIBLE);
                progressDialog.cancel();
            });
            queue.add(jsonObjectRequest);
        });
    }

    private void getAllMyPatients(String token) {
        JsonRequest<JSONArray> jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Settings.DEFAULT_SERVER_URL+"/patient/allinfo",
                null, response -> {
            try {
                savePatientsAndReading(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("bugg","failed: "+ error);
            if (error!=null){
                Log.d("bugg", error.getMessage()+"     "+error.networkResponse.statusCode);

            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json");
                headers.put(LoginActivity.AUTH, "Bearer " + token);
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(MyApp.getInstance());
        queue.add(jsonArrayRequest);
    }

    /**
     * converts patient and reading from json into Reading/ patient class
     * @param response response from the web
     * @throws JSONException if the object doesnt exist
     */
    private void savePatientsAndReading(JSONArray response) throws JSONException {
        for (int i=0;i<response.length();i++){
            //get the main json object
            JSONObject jsonObject = response.getJSONObject(i);
            //build patient
            Patient patient = new Patient();
            patient.dob = jsonObject.getString("dob");
            patient.patientName=jsonObject.getString("patientName");
            patient.zone = jsonObject.getString("zone");
            patient.gestationalAgeUnit = Reading.GestationalAgeUnit.valueOf((String) jsonObject.get("gestationalAgeUnit"));
            patient.gestationalAgeValue = jsonObject.getString("gestationalAgeValue");
            patient.patientId = jsonObject.getString("patientId");
            patient.villageNumber = jsonObject.getString("villageNumber");
            patient.patientSex = Patient.PATIENTSEX.valueOf((String) jsonObject.get("patientSex"));
            patient.age = jsonObject.optInt("patientAge",-1);
            patient.isPregnant = jsonObject.optBoolean("isPregnant",false);
            patient.needAssessment = jsonObject.optBoolean("needsAssessment",false);

            patient.drugHistoryList = new ArrayList<>();
            patient.drugHistoryList.add(jsonObject.getString("drugHistory"));
            patient.medicalHistoryList = new ArrayList<>();
            patient.medicalHistoryList.add(jsonObject.getString("medicalHistory"));
            JSONArray readingArray = jsonObject.getJSONArray("readings");

            //get all the readings
            for (int j=0;j<readingArray.length();j++){
                JSONObject readingJson = readingArray.getJSONObject(i);
                Reading reading = new Reading();
                reading.bpDiastolic = readingJson.optInt("bpDiastolic",-1);
                reading.bpSystolic = readingJson.optInt("bpSystolic",-1);
                reading.userHasSelectedNoSymptoms = readingJson.optBoolean("userHasSelectedNoSymptoms",false);
                reading.dateLastSaved =ZonedDateTime.parse(readingJson.getString("dateLastSaved"));
                reading.heartRateBPM = readingJson.optInt("heartRateBPM",-1);
                reading.serverReadingId = readingJson.getString("userId");
                //
                JSONObject urineTest = readingJson.getJSONObject("urineTests");
                UrineTestResult urineTestResult = new UrineTestResult();
                //todo fill out the urine result
                //reading.retestOfPreviousReadingIds= (List<Long>) readingJson.get("retestOfPreviousReadingIds");
                reading.dateUploadedToServer = DateUtil.getZoneTimeFromString(readingJson.getString("dateUploadedToServer"));
                reading.dateTimeTaken = DateUtil.getZoneTimeFromString(readingJson.getString("dateTimeTaken"));
                reading.dateRecheckVitalsNeeded = DateUtil.getZoneTimeFromString(readingJson.getString("dateRecheckVitalsNeeded"));
                if (reading.dateUploadedToServer==null){
                    // cannot be null since we check this in order to upload reading to server
                    reading.dateUploadedToServer = ZonedDateTime.now();
                }
                // TODO: 05/03/20 offline db creates reading id automatically, figure out if thats ok
                reading.setAManualChangeOcrResultsFlags(readingJson.optInt("manuallyChangeOcrResults",-1));
                reading.totalOcrSeconds = readingJson.optInt("totalOcrSeconds",-1);
                reading.referralComment = readingJson.getString("referral");
                reading.symptoms.add(0,readingJson.getString("symptoms"));
                reading.setFlaggedForFollowup(readingJson.optBoolean("isFlaggedForFollowup",false));

                // because we decided to put patient inside reading --___--
                reading.patient = patient;
                //adding the reading to db
                Log.d("buggg","adding to the reading manager");
                readingManager.addNewReading(this,reading);

            }
            Log.d("bugg",patient.toString());
        }
    }

    private void saveUserNamePasswordSharedPref(String email, String password) {
        SharedPreferences sharedPref = LoginActivity.this.getSharedPreferences(AUTH_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LOGIN_EMAIL, email);
        editor.putInt(LOGIN_PASSWORD, password.hashCode());
        editor.apply();
    }

    private ProgressDialog getProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Logging In");
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }
}
