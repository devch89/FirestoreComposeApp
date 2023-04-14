package com.example.firestorecomposeapp.ui.screens.signin

data class SignInState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)