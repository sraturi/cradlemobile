package com.cradle.neptune.net

import com.cradle.neptune.model.Unmarshal

/**
 * Sum type representing the result of a network request.
 */
sealed class NetworkResult<T> {
    /**
     * Unwraps this network result into an optional value.
     *
     * @return the result value or null depending on whether the result is a
     *  [Success], [Failure], [NetworkException] variant
     */
    val unwrapped: T?
        get() = when (this) {
            is Success -> value
            is Failure -> null
            is NetworkException -> null
        }

    /**
     * Applies a closure [f] to transform the value field of a [Success] result.
     *
     * In the case of [Failure] and [NetworkException] variants, this method
     * does nothing.
     *
     * @param f transformation to apply to the result value
     * @return a new [NetworkResult] with the transformed value
     */
    fun <U> map(f: (T) -> U): NetworkResult<U> = when (this) {
        is Success -> Success(f(value), statusCode)
        is Failure -> Failure(body, statusCode)
        is NetworkException -> NetworkException(cause)
    }
}

/**
 * The result of a successful network request.
 *
 * A request is considered successful if the response has a status code in the
 * 200..<300 range.
 *
 * @property value The result value
 * @property statusCode Status code of the response which generated this result
 */
data class Success<T>(val value: T, val statusCode: Int) : NetworkResult<T>()

/**
 * The result of a network request which made it to the server but the status
 * code of the response indicated a failure (e.g., 404, 500, etc.).
 *
 * Contains the response status code along with the response body as a byte
 * array. Note that the body is not of type [T] like in [Success] since the
 * response for a failed request may not be the same type as the response for
 * a successful request.
 *
 * @property body The body of the response
 * @property statusCode The status code of the response
 */
data class Failure<T>(val body: ByteArray, val statusCode: Int) : NetworkResult<T>() {

    /**
     * Converts the response body of this failure result to some other type.
     *
     * @param unmarshaller an object used to unmarshall the byte array body
     *  into a different type
     * @return a new object which was constructed from the response body
     */
    fun <R, U> marshal(unmarshaller: U)
        where U : Unmarshal<R, ByteArray> =
        unmarshaller.unmarshal(body)

    /**
     * Converts the response body of this failure result to JSON.
     *
     * Whether a [JsonObject] or [JsonArray] is returned depends on the content
     * of the response body.
     *
     * @return a [Json] object
     * @throws org.json.JSONException if the response body cannot be converted
     *  into JSON.
     */
    fun toJson() = marshal(Json.Companion)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Failure<*>

        if (!body.contentEquals(other.body)) return false
        if (statusCode != other.statusCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = body.contentHashCode()
        result = 31 * result + statusCode
        return result
    }
}

/**
 * Represents an exception that occurred whilst making a network request.
 *
 * @property cause the exception which caused the failure
 */
data class NetworkException<T>(val cause: Exception) : NetworkResult<T>()