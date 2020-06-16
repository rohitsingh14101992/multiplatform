package com.example.sharedcode

import kotlinx.coroutines.CoroutineDispatcher

expect fun uiDispatcher(): CoroutineDispatcher

expect fun ioDispatcher(): CoroutineDispatcher