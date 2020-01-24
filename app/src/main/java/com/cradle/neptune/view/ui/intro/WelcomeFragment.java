package com.cradle.neptune.view.ui.intro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cradle.neptune.R;
import com.cradle.neptune.view.ui.reading.PatientInfoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Confirm privacy policy with user
 */
public class WelcomeFragment extends IntroBaseFragment {
    private View mView;
    private List<CheckBox> checkBoxes = new ArrayList<>();

    public WelcomeFragment() {
        // Required empty public constructor
        TAG = PatientInfoFragment.class.getName();
    }

    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
    }

    @Override
    public void onMyBeingDisplayed() {
        // may not have created view yet.
        if (mView == null) {
            return;
        }
        hideKeyboard();

    }


    @Override
    public boolean onMyBeingHidden() {
        // may not have created view yet.
        if (mView == null) {
            return true;
        }

        return true;
    }


}
