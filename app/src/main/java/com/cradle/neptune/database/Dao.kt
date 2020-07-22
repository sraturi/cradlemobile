package com.cradle.neptune.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cradle.neptune.model.Patient
import com.cradle.neptune.model.Reading

/**
 * Data Access Object (DAO) for [Reading] entities.
 *
 * Provides methods for adding, updating, and removing entities from a database
 * along with a series of query methods.
 */
@Dao
interface ReadingDaoAccess {
    /**
     * Inserts a new reading into the database.
     *
     * If a conflicting element already exists in the database it will be
     * replaced with the new one.
     *
     * @param Reading The entity to insert into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insertReading(reading: Reading)

    /**
     * Inserts each reading in the supplied list into the database.
     *
     * If a conflicting element already exists in the database it will be
     * replaced with the new one.
     *
     * @param readingEntities A list of entities to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insertAll(readingEntities: List<Reading>)

    /**
     * Updates an existing reading in the database.
     *
     * @param Reading An entity containing updated data.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  update(reading: Reading)

    /**
     * Removes an entity from the database.
     *
     * @param Reading The entity to remove.
     */
    @Delete
    suspend fun  delete(reading: Reading?)

    /**
     * All of the readings in the database.
     */
    @get:Query("SELECT * FROM Reading")
    val allReadingEntities: List<Reading>

    /**
     * Returns the first reading who's id matches a given pattern.
     *
     * Note that this method does not perform an exact match on the reading id
     * and instead performs an SQL `LIKE` operation limiting the result to 1.
     *
     * @param id The reading id to search for.
     */
    @Query("SELECT * FROM Reading WHERE id LIKE :id LIMIT 1")
    suspend fun  getReadingById(id: String): Reading?

    /**
     * Returns all of the readings associated with a specified patient.
     *
     * @param id The id of the patient to find readings for.
     */
    @Query("SELECT * FROM Reading WHERE patientId LIKE :id")
    suspend fun  getAllReadingByPatientId(id: String): List<Reading>

    /**
     * All readings which have not yet been uploaded to the server.
     */
    @get:Query("SELECT * FROM Reading WHERE isUploadedToServer = 0")
    val allUnUploadedReading: List<Reading>

    /**
     * Newest reading of a perticular patient
     */
    @Query("SELECT *,MAX(dateTimeTaken) FROM READING WHERE patientId LIKE :id")
    suspend fun  getNewestReadingByPatientId(id: String): Reading?
    /**
     * Deletes all readings from the database.
     */
    @Query("DELETE FROM Reading")
    suspend fun  deleteAllReading()
}

@Dao
interface PatientDaoAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insert(patient: Patient)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insertAll(patients: List<Patient>)

    @Delete
    suspend fun  delete(patient: Patient)

    @get:Query("SELECT * FROM Patient")
    val allPatients: List<Patient>

    @Query("SELECT * FROM Patient WHERE id LIKE :id LIMIT 1")
    suspend fun  getPatientById(id: String): Patient?

    @Query("DELETE FROM Patient")
    suspend fun  deleteAllPatients()
}

/**
 * Data Access Object (DAO) for [HealthFacilityEntity] entities.
 *
 * Provides methods for adding, updating, and removing entities from a database
 * along with a series of query methods.
 */
@Dao
interface HealthFacilityDaoAccess {
    /**
     * Inserts a new health facility into the database.
     *
     * If a conflicting element already exists in the database it will be
     * replaced with the new one.
     *
     * @param healthFacilityEntity The health facility to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insert(healthFacilityEntity: HealthFacilityEntity)

    /**
     * Inserts each health facility in the supplied list into the database.
     *
     * @param healthFacilityEntities The list of facilities to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insertAll(healthFacilityEntities: List<HealthFacilityEntity>)

    /**
     * Updates an existing health facility.
     *
     * @param healthFacilityEntity An entity containing updated data.
     */
    @Update
    suspend fun  update(healthFacilityEntity: HealthFacilityEntity)

    /**
     * Removes an entity from the database.
     *
     * @param healthFacilityEntity The entity to remove.
     */
    @Delete
    suspend fun  delete(healthFacilityEntity: HealthFacilityEntity)

    /**
     * Deletes all health centres in the database.
     */
    @Query("DELETE FROM HealthFacilityEntity")
    suspend fun  deleteAll()

    /**
     * All health facilities stored in the database.
     */
    @get:Query("SELECT * FROM HealthFacilityEntity")
    val allHealthFacilities: List<HealthFacilityEntity>

    /**
     * Returns the first health facility from the database who's id matches
     * the supplied pattern.
     *
     * @param id The id of the health facility to retrieve.
     */
    @Query("SELECT * FROM HealthFacilityEntity WHERE id LIKE :id LIMIT 1")
    suspend fun  getHealthFacilityById(id: String): HealthFacilityEntity?

    /**
     * All health facilities which the user has selected to be visible.
     */
    @get:Query("SELECT * FROM HealthFacilityEntity WHERE isUserSelected = 1")
    val allUserSelectedHealthFacilities: List<HealthFacilityEntity>
}
