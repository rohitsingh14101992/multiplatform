package com.example.sharedcode

expect fun <T> runTest(block: suspend () -> T)