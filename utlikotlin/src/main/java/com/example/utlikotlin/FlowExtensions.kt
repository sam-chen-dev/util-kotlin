package com.example.utlikotlin

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

fun <T> Flow<T>.collect(coroutineScope: CoroutineScope, action: suspend (T) -> Unit) = coroutineScope.launch {
    collect { action(it) }
}

fun <T> Flow<T>.collectOnCreated(lifecycleOwner: LifecycleOwner, action: suspend (T) -> Unit) = lifecycleOwner.lifecycleScope.launch {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
        collect { action(it) }
    }
}

fun <T> Flow<T>.collectOnStarted(lifecycleOwner: LifecycleOwner, action: suspend (T) -> Unit) = lifecycleOwner.lifecycleScope.launch {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        collect { action(it) }
    }
}

fun <T> Flow<T>.collectLatestOnStarted(lifecycleOwner: LifecycleOwner, action: suspend (T) -> Unit) = lifecycleOwner.lifecycleScope.launch {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        collectLatest { action(it) }
    }
}

fun <T> Flow<T>.collectOnResumed(lifecycleOwner: LifecycleOwner, action: suspend (T) -> Unit) = lifecycleOwner.lifecycleScope.launch {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
        collect { action(it) }
    }
}

fun <R> Flow<R>.toStateFlow(coroutineScope: CoroutineScope, initialValue: R): StateFlow<R> {
    return stateIn(coroutineScope, SharingStarted.Lazily, initialValue)
}

fun <R> Flow<R>.toSharedFlow(coroutineScope: CoroutineScope): SharedFlow<R> {
    return shareIn(coroutineScope, SharingStarted.Lazily)
}