package com.example.firestorecomposeapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.firestorecomposeapp.navigation.dump.animatedComposable
import com.example.firestorecomposeapp.ui.screens.signin.SignInScreen
import com.example.firestorecomposeapp.ui.screens.signin.SignInScreenActions
import com.example.firestorecomposeapp.ui.screens.task.TaskScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

sealed class Screen(val name: String, val route: String) {
    object SignIn: Screen("signin","signin")
    object Task: Screen("task","task")
}

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FirestoreNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberAnimatedNavController(),
    actions: NavActions = remember(navController){
        NavActions(navController)
    }
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.SignIn.route,
        modifier = modifier
    ) {
        animatedComposable(Screen.SignIn.route) {
            SignInScreen(onAction = actions::navigateFromSignIn, navController = navController)
//            TaskScreen( navController = navController)

        }

        animatedComposable(Screen.Task.route) {
            TaskScreen( navController = navController)
        }
    }
}

class NavActions(private val navController: NavController) {

    fun navigateFromSignIn(actions: SignInScreenActions) {
        when (actions) {
            SignInScreenActions.LoadTask -> {
                navController.navigate(Screen.Task.name)
            }
        }
    }
}

