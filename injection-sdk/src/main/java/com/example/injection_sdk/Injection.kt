package com.example.injection_sdk

import java.util.concurrent.ConcurrentHashMap

/**
 * Sets the Inversion Principal for the Injection
 */
object Injection : InjectionSdk {

    private val dependencies = ConcurrentHashMap<String, Any>()

    override fun <T> addDependency(dependency: T) {

        dependency?.let {
            // CanonicalName is the name of the class
            val name = it::class.java.canonicalName
            // check for null
            if (!name.isNullOrEmpty()) {
                if (!dependencies.containsKey(name)) {
                    dependencies[name] = it
                } else {
                    throw Exception()
                }
            } else {
                throw Exception()
            }

        }
    }

    override fun <T> getDependency(dependency: Class<T>?): T {
        dependency?.let {
            // CanonicalName is the name of the class
            val name = it::class.java.canonicalName
            // check for null
            if (!name.isNullOrEmpty()) {
                return dependencies[name] as T
            } else {
                throw Exception()
            }

        } ?: throw Exception()
    }
}

inline fun <reified T> getDependency(): T{
        return Injection.getDependency(T::class.java)
    }

