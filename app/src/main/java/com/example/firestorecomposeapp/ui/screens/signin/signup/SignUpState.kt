package com.example.firestorecomposeapp.ui.screens.signin.signup

data class SignUpState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)