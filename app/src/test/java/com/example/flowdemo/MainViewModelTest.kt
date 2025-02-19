package com.example.flowdemo

import app.cash.turbine.test
import com.example.flowdemo.ui.viewmodel.MainViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var testDispatchers: TestDispatcherProvider

    @Before
    fun setUp() {
        testDispatchers = TestDispatcherProvider()
        viewModel = MainViewModel(testDispatchers)
    }

    @Test
    fun `countDownFlow, properly count down from 5 to 0`() = runBlocking {
        viewModel.countDownFLow.test {
            for (i in 5 downTo 0) {
                testDispatchers.dispatcher.scheduler.apply { advanceTimeBy(1000L); runCurrent() }
                val emission = awaitItem()
                assertThat(emission).isEqualTo(i)
            }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `squareNumber, number properly squared`() = runBlocking {
        // viewModel.squareNumber(3)
        val job = launch {
            viewModel.sharedFlow.test {
                val emission = awaitItem()
                assertThat(emission).isEqualTo(9)
                cancelAndConsumeRemainingEvents()
            }
        }
        viewModel.squareNumber(3)
        job.join()
        job.cancel()
    }
}