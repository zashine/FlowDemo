package com.example.flowdemo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flowdemo.DefaultDispatchers
import com.example.flowdemo.DispatcherProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel @JvmOverloads constructor(
    private val dispatchers: DispatcherProvider = DefaultDispatchers()
) : ViewModel() {

    val countDownFLow = flow {
        val startingValue = 5
        var currentValue = startingValue
        emit(startingValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }.flowOn(dispatchers.main)

    private val _stateFlow = MutableStateFlow(0) // mutable
    val stateFlow = _stateFlow.asStateFlow() // read only


    private val _sharedFlow = MutableSharedFlow<Int>(replay = 5) // mutable
    val sharedFlow = _sharedFlow.asSharedFlow() // read only

    init {
        squareNumber(3)
        // collectFlow3()
        viewModelScope.launch(dispatchers.main) {
            sharedFlow.collect {
                delay(2000L)
                println("FIRST FLOW: The received number is $it")
            }
        }
        viewModelScope.launch(dispatchers.main) {
            sharedFlow.collect {
                delay(3000L)
                println("SECOND FLOW: The received number is $it")
            }
        }
    }

    fun squareNumber(number: Int) {
        viewModelScope.launch(dispatchers.main) {
            _sharedFlow.emit(number * number)
        }
    }

    fun incrementCounter() {
        _stateFlow.value += 1
    }

    private fun collectFlow() {
        viewModelScope.launch {
            val count = countDownFLow
                .filter { time -> time % 2 == 0 }
                .map { time -> time * time }
                .onEach { time -> println(time) }
                .count {
                    it % 2 == 0
                }
            println("count is $count")
        }
    }

    private fun collectFlow2() {
        viewModelScope.launch {
            val reduceResult = countDownFLow
                .fold(100) { accumulator, value ->
                    accumulator + value
                }
            println("The count is $reduceResult")
        }
    }

    private fun collectFlow3() {
        val flow0 = (1..5).asFlow()

        val flow1 = flow {
            emit(1)
            delay(500L)
            emit(2)
        }
        viewModelScope.launch(dispatchers.main) {
            flow1.flatMapConcat { value ->
                flow {
                    emit(value + 1)
                    delay(500L)
                    emit(value + 2)
                }
            }.collect {
                println("The value is $it")
            }
        }
    }
}