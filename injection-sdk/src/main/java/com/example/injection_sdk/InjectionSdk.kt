package com.example.injection_sdk

import androidx.constraintlayout.solver.widgets.analyzer.Dependency

internal interface  InjectionSdk {

    @Throws(Exception::class)
fun <T> addDependency(dependency: T)

    @Throws(Exception::class)
fun <T> getDependency(dependency: Class<T>?): T
}