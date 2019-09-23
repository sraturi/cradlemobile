package com.cradletrial.cradlevhtapp.model.Patient;

import android.util.Log;

import com.cradletrial.cradlevhtapp.model.Reading;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Patient {

    public static enum PATIENTSEX {M, F, I}

    // TODO: 22/09/19 encapsulate these class members
    // patient info
    public String patientId;
    public String patientName;
    public Integer ageYears;
    public List<String> symptoms = new ArrayList<>();
    public Reading.GestationalAgeUnit gestationalAgeUnit;
    public String gestationalAgeValue;
    public String villageNumber;
    public PATIENTSEX patientSex;

    public Patient() {}
    public Patient(String mPatientId, String mPatientName,Integer mAgeYears, Reading.GestationalAgeUnit mGestationalAgeUnit,
                   String mGestationalAgeValue, String mVillageNumber, PATIENTSEX mSex) {
        patientId = mPatientId ;
        patientName = mPatientName;
        ageYears = mAgeYears;
        gestationalAgeUnit = mGestationalAgeUnit;
        gestationalAgeValue = mGestationalAgeValue;
        villageNumber = mVillageNumber;
        patientSex = mSex;
    }

    public static String toJSon(Patient person) {
        try {
            JSONObject parent = new JSONObject();
            JSONObject patientInfoObject = new JSONObject();
            JSONObject referralObject = new JSONObject();
            JSONObject readingObject = new JSONObject();
            JSONObject filloutObject = new JSONObject();

            parent.put("personal-info", patientInfoObject);
            patientInfoObject.put("patientId", person.patientId.toString() );
            patientInfoObject.put("patientName", person.patientName.toString() );
            patientInfoObject.put("patientAge", person.ageYears.toString() );
            patientInfoObject.put("gestationalAgeUnit", person.gestationalAgeUnit.toString() );
            patientInfoObject.put("gestationalAgeValue", person.gestationalAgeValue.toString() );
            patientInfoObject.put("villageNumber", person.villageNumber.toString() );
            patientInfoObject.put("patientSex", person.patientSex.toString() );

            String symptomsString = "None";
            if (person.symptoms.size() > 0) {
                symptomsString = person.symptoms.get(0);
                if (person.symptoms.size() > 1) {
                    for (int i = 1; i < person.symptoms.size(); i++) {
                        symptomsString += ",";
                        symptomsString += person.symptoms.get(i);
                    }
                }
            }

            patientInfoObject.put("symptoms", symptomsString);

            parent.put("referral", referralObject);
            patientInfoObject.put("key", "string");

            parent.put("reading", readingObject);
            patientInfoObject.put("key", "string");

            parent.put("fillout", filloutObject);
            patientInfoObject.put("key", "string");

            Log.d("Json", parent.toString(2));

            return parent.toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJSon(Patient person, JSONObject referralObject) {
        try {
            JSONObject parent = new JSONObject();
            JSONObject patientInfoObject = new JSONObject();
            JSONObject readingObject = new JSONObject();
            JSONObject filloutObject = new JSONObject();

            parent.put("personal-info", patientInfoObject);
            patientInfoObject.put("patientId", person.patientId.toString() );
            patientInfoObject.put("patientName", person.patientName.toString() );
            patientInfoObject.put("patientAge", person.ageYears.toString() );
            patientInfoObject.put("gestationalAgeUnit", person.gestationalAgeUnit.toString() );
            patientInfoObject.put("gestationalAgeValue", person.gestationalAgeValue.toString() );
            patientInfoObject.put("villageNumber", person.villageNumber.toString() );
            patientInfoObject.put("patientSex", person.patientSex.toString() );

            String symptomsString = "None";
            if (person.symptoms.size() > 0) {
                symptomsString = person.symptoms.get(0);
                if (person.symptoms.size() > 1) {
                    for (int i = 1; i < person.symptoms.size(); i++) {
                        symptomsString += ",";
                        symptomsString += person.symptoms.get(i);
                    }
                }
            }

            patientInfoObject.put("symptoms", symptomsString);

            parent.put("referral", referralObject);
            patientInfoObject.put("key", "string");

            parent.put("reading", readingObject);
            patientInfoObject.put("key", "string");

            parent.put("fillout", filloutObject);
            patientInfoObject.put("key", "string");

            Log.d("Json", parent.toString(2));

            return parent.toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



}
