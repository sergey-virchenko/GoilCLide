package com.virser.testapp.data

sealed class ApiResult<out R> {

    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val exception: Throwable) : ApiResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success, data = $data"
            is Error -> "Error, exception = $exception"
        }
    }
}
