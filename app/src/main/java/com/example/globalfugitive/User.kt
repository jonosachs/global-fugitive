package com.example.globalfugitive

data class User(
    val userId: String,
    val email: String?,
    var displayName: String?,
    val photoUrl: String?,
    val dateOfBirth: Long?,
    val gender: String?
)

