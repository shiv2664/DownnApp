package com.shivam.downn.data.network

/*sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String?) : NetworkResult<String>()
    object Loading : NetworkResult<Nothing>()
}*/

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {

    class Success<T>(data: T) : NetworkResult<T>(data)

    class Error<T>(message: String?) : NetworkResult<T>( message=message)

    class Loading<T> : NetworkResult<T>()

}
