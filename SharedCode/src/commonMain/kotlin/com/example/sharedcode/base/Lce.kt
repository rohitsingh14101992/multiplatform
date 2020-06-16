package com.example.sharedcode.base

sealed class Lce<T> {
    class Loading<T> : Lce<T>()
    data class Content<T>(val packet: T) : Lce<T>()
    data class Error<T>(val throwable: Throwable) : Lce<T>()
}