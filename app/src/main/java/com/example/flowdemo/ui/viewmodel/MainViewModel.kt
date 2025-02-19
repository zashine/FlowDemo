package com.example.flowdemo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val countDownFLow = flow<Int> {
        val startingValue = 10
        var currentValue = startingValue
        emit(startingValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }

    private val _stateFlow = MutableStateFlow(0) // mutable
    val stateFlow = _stateFlow.asStateFlow() // read only


    private val _sharedFlow = MutableSharedFlow<Int>() // mutable
    val sharedFlow = _sharedFlow.asSharedFlow() // read only

    init {
        collectFlow3()

        viewModelScope.launch {
            sharedFlow.collect {
                delay(3000L)

            }
        }
    }

    fun squareNumber(number: Int) {
        viewModelScope.launch {
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

        val flow1 = flow<Int> {
            emit(1)
            delay(500L)
            emit(2)
        }
        viewModelScope.launch {
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