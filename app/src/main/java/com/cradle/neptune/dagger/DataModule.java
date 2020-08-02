package com.cradle.neptune.dagger;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import androidx.room.Room;

import com.cradle.neptune.database.CradleDatabase;
import com.cradle.neptune.manager.*;

import javax.inject.Singleton;

import com.cradle.neptune.network.Api;
import com.cradle.neptune.network.VolleyRequestQueue;

import dagger.Module;
import dagger.Provides;

/**
 * Provide the singleton objects for data access
 * Source: https://github.com/codepath/android_guides/wiki/Dependency-Injection-with-Dagger-2#instantiating-the-component
 */
@Module
public class DataModule {

    @Provides
    @Singleton
    public CradleDatabase provideDatabase(Application application) {
        return Room.databaseBuilder(application, CradleDatabase.class, "room-readingDB").build();
    }

    @Provides
    @Singleton
    public ReadingManager provideReadingService(CradleDatabase database, MarshalManager marshalManager) {
        return new ReadingManager(database.readingDaoAccess());
    }

    @Provides
    @Singleton
    public HealthCentreManager provideHealthCentreService(CradleDatabase database) {
        return new HealthCentreManager(database);
    }

    @Provides
    @Singleton
    public PatientManager providePatientManager(CradleDatabase database){
        return new PatientManager(database.patientDaoAccess());
    }

    @Provides
    @Singleton
    public SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    public VolleyRequestQueue provideVolleyRequestQueue(Application application) {
        return new VolleyRequestQueue(application);
    }

    @Provides
    @Singleton
    public VolleyRequestManager provideVolleyRequestManager(Application application){
        return new VolleyRequestManager(application);
    }

    @Provides
    @Singleton
    public Api provideApi(
        SharedPreferences sharedPreferences,
        UrlManager urlManager,
        VolleyRequestQueue volleyRequestQueue
    ) {
        return new Api(sharedPreferences, urlManager, volleyRequestQueue);
    }
}
