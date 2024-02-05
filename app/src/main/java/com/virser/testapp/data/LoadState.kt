package com.virser.testapp.data

sealed class LoadState<out T> {
    object Loading : LoadState<Nothing>()
    data class Success<out T>(val data: T) : LoadState<T>()
}
