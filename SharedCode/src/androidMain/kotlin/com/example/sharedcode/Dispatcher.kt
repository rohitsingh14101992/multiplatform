package com.example.sharedcode

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

 actual fun uiDispatcher() : CoroutineDispatcher = Dispatchers.Main

 actual fun ioDispatcher() : CoroutineDispatcher = Dispatchers.IO