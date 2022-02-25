package com.quizappbackend.extensions

import org.mindrot.jbcrypt.BCrypt
import java.security.SecureRandom
import kotlin.random.Random

fun String.generateHash(saltLength: Int = 10): String = BCrypt.hashpw(this, BCrypt.gensalt(saltLength))

fun String.matchesHash(pwToken: String) = BCrypt.checkpw(this, pwToken)

fun generateRandomHexString(size: Int = 32): String = Random.nextBytes(size).let { bytes ->
    SecureRandom().nextBytes(bytes)
    bytes.joinToString("") { "%02x".format(it) }
}.also { hex ->
    println("Generated HEX: $hex")
}


