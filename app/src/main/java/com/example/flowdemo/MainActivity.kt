package com.example.flowdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.flowdemo.ui.theme.FlowDemoTheme
import com.example.flowdemo.ui.viewmodel.MainViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collectLatest {
                    // do ui refresh
                }
            }
        }*/
        collectLatestLifecycleFlow(viewModel.stateFlow) {
            // do ui refresh
        }

        setContent {
            FlowDemoTheme {
                /*Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }*/
                val viewModel by viewModels<MainViewModel>()
                // val time = viewModel.countDownFLow.collectAsState(10)
                val count = viewModel.stateFlow.collectAsState(0)

                LaunchedEffect(key1 = true) {

                }

                Box(modifier = Modifier.fillMaxSize()) {
                    /*Text(
                        text = time.value.toString(),
                        fontSize = 30.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )*/
                    Button(onClick = { viewModel.incrementCounter() }) {
                        Text(text = "Click me: ${count.value}")
                    }
                }
            }
        }
    }
}

// Extension function for collectLatest for ComponentActivity
fun <T> ComponentActivity.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}