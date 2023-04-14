package com.example.firestorecomposeapp.ui.screens.signin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firestorecomposeapp.data.repo.auth.AuthRepository
import com.example.firestorecomposeapp.data.repo.auth.Resource
import com.example.firestorecomposeapp.ui.screens.signin.signup.SignUpState
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    val _signInState = Channel<SignInState>()
    val signInState = _signInState.receiveAsFlow()

    val _googleState = mutableStateOf(GoogleSignInState())
    val googleState: State<GoogleSignInState> = _googleState

    /* We use channel here to send information between two Coroutines */
    val _signUpState = Channel<SignUpState>() // sending some info inside this channel
    val signUpState = _signUpState.receiveAsFlow() // receiving something in this channel

    fun createUser(email: String, password: String) =
        viewModelScope.launch {
            repository.registerUser(email, password).collect { result ->
                // we need to collect all the states
                when (result) {
                    is Resource.Success -> {
                        _signUpState.send(SignUpState(isSuccess = "User Created Successful"))

                    }

                    is Resource.Loading -> {
                        _signUpState.send(SignUpState(isLoading = true))

                    }

                    is Resource.Error -> {
                        _signUpState.send(SignUpState(isError = result.message))

                    }
                }
            }
        }

    fun googleSignIn(credential: AuthCredential) = viewModelScope.launch {
        repository.googleSignIn(credential).collect { result ->
            when (result) {
                is Resource.Success -> {
                    _googleState.value = GoogleSignInState(success = result.data)
                }
                is Resource.Loading -> {
                    _googleState.value = GoogleSignInState(loading = true)
                }
                is Resource.Error -> {
                    _googleState.value = GoogleSignInState(error = result.message!!)
                }
            }


        }
    }


    fun loginUser(email: String, password: String) = viewModelScope.launch {
        repository.loginUser(email, password).collect { result ->
            when (result) {
                is Resource.Success -> {
                    _signInState.send(SignInState(isSuccess = "Sign In Success "))
                }
                is Resource.Loading -> {
                    _signInState.send(SignInState(isLoading = true))
                }
                is Resource.Error -> {

                    _signInState.send(SignInState(isError = result.message))
                }
            }

        }
    }

}