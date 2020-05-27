package com.cradle.neptune.view.ui.reading;

import com.cradle.neptune.service.ReadingService;
import com.cradle.neptune.viewmodel.PatientReadingViewModel;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 */
public interface MyFragmentInteractionListener {
    PatientReadingViewModel getViewModel();

    ReadingService getReadingService();

    void advanceToNextPage();

    boolean saveCurrentReading();
}