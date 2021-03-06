package com.cradle.neptune.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cradle.neptune.ext.Field
import com.cradle.neptune.ext.put
import com.cradle.neptune.ext.stringField
import java.util.UUID
import org.json.JSONObject

/**
 * A health facility database entity.
 *
 * @property id Identifier for this health facility; the entity's primary key.
 * @property name The name of the health facility.
 * @property location The location of the health facility.
 * @property phoneNumber The phone number associated with this health facility.
 * @property about A description of the health facility.
 * @property type The type of the health facility.
 * @property isUserSelected Whether the user wishes to see this health facility
 * in their drop down menu.
 */
@Entity
data class HealthFacility(
    @PrimaryKey var id: String = "",
    @ColumnInfo var name: String = "",
    @ColumnInfo var location: String = "",
    @ColumnInfo var phoneNumber: String = "",
    @ColumnInfo var about: String = "",
    @ColumnInfo var type: String = "",
    @ColumnInfo var isUserSelected: Boolean = false
) : Marshal<JSONObject> {

    /**
     * Constructs a [JSONObject] from this object.
     */
    override fun marshal(): JSONObject = with(JSONObject()) {
        put(HealthFacilityField.NAME, name)
        put(HealthFacilityField.ABOUT, about)
        put(HealthFacilityField.LOCATION, location)
        put(HealthFacilityField.ID, id)
        put(HealthFacilityField.TYPE, type)
    }

    companion object : Unmarshal<HealthFacility, JSONObject> {
        /**
         * Constructs a [HealthFacility] from a [JSONObject].
         */
        override fun unmarshal(data: JSONObject): HealthFacility = HealthFacility().apply {
            name = data.stringField(HealthFacilityField.NAME)
            about = data.stringField(HealthFacilityField.ABOUT)
            location = data.stringField(HealthFacilityField.LOCATION)
            phoneNumber = data.stringField(HealthFacilityField.PHONE_NUMBER)
            // server doesnt have an id yet.. so we are creating one for local DB
            id = UUID.randomUUID().toString()
        }
    }
}

/**
 * The collection of JSON fields which make up a [HealthFacility] object.
 *
 * These fields are defined here to ensure that the marshal and unmarshal
 * methods use the same field names.
 */
private enum class HealthFacilityField(override val text: String) : Field {
    TYPE("facilityType"),
    LOCATION("location"),
    ABOUT("about"),
    PHONE_NUMBER("healthFacilityPhoneNumber"),
    NAME("healthFacilityName"),
    ID("id");
}
