package ru.nucodelabs.gem.config

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import ru.nucodelabs.gem.app.snapshot.StateHolder
import javax.inject.Provider

class SaveStateInterceptor(
    stateHolderProvider: Provider<StateHolder<String, Any>>,
) : MethodInterceptor {
    private val stateHolder = stateHolderProvider.get()

    override fun invoke(invocation: MethodInvocation?): Any {
        invocation!!
        if (invocation.method.isAnnotationPresent(SaveState::class.java)) {
            val params = invocation.method.parameters
            for (pIdx in params.indices) {
                if (params[pIdx].isAnnotationPresent(State::class.java)) {
                    val stateId = params[pIdx].getAnnotation(State::class.java).value
                    val state = invocation.arguments[pIdx]
                    saveState(stateId, state)
                }
            }
            if (invocation.method.isAnnotationPresent(State::class.java)) {
                val stateId = invocation.method.getAnnotation(State::class.java).value
                val state = invocation.proceed()
                saveState(stateId, state)
                return state
            }
        }
        return invocation.proceed()
    }

    private fun saveState(identifier: String, state: Any) {
        stateHolder.putState(identifier = identifier, state = state)
    }
}