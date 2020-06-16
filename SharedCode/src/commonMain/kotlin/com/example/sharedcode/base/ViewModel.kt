package com.example.sharedcode.base

import com.example.sharedcode.uiDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.flow.*

abstract class ViewModel<A : Action, S : State, R : Result>
    (initialState: S, mainCoroutineDispatcher: CoroutineDispatcher) {

    val viewModelScope = CoroutineScope(SupervisorJob() + mainCoroutineDispatcher)
    private val actions: Channel<A> = Channel(Channel.UNLIMITED)

    @FlowPreview
    private val results: Flow<R> = actions.consumeAsFlow().flatMapLatest { actionToResults(it) }


    private val stateChannel: ConflatedBroadcastChannel<S> = ConflatedBroadcastChannel(initialState)


    fun sendEvent(action: A) = actions.offer(action)

    protected abstract suspend fun actionToResults(action: A): Flow<R>

    protected abstract suspend fun resultToState(result: R, state: S): S

    @ExperimentalCoroutinesApi
    val stateJob = results.scan(initialState) { state, result -> resultToState(result, state) }
        .distinctUntilChanged()
        .onEach { stateChannel.offer(it) }
        .launchIn(viewModelScope)

    fun observeState(): CFlow<S> = CFlow(stateChannel.asFlow())


     fun onCleared() {
         viewModelScope.cancel()
    }


}