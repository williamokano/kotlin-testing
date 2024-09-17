package dev.okano.kotlintesting.helpers

import java.security.MessageDigest

fun String.sha512(): String {
    val digest = MessageDigest.getInstance("SHA-512")
    val bytes = toByteArray(Charsets.UTF_8)
    digest.update(bytes)

    return digest.digest().joinToString("") { "%02x".format(it) }
}