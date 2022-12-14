package com.quanticheart.security

object CppKeys {
    external fun user(): String?

    init {
        System.loadLibrary("security-lib")
    }
}