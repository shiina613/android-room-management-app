package com.kma.lamphoun.roomapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.kma.lamphoun.roomapp.data.local.TokenDataStore
import com.kma.lamphoun.roomapp.data.remote.StompManager
import com.kma.lamphoun.roomapp.ui.navigation.RoomNavGraph
import com.kma.lamphoun.roomapp.ui.theme.RoomAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var stompManager: StompManager
    @Inject lateinit var tokenDataStore: TokenDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Connect/disconnect WebSocket based on token presence
        lifecycleScope.launch {
            tokenDataStore.token.collect { token ->
                if (token != null) {
                    stompManager.connect(token)
                } else {
                    stompManager.disconnect()
                }
            }
        }

        setContent {
            RoomAppTheme {
                RoomNavGraph()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stompManager.disconnect()
    }
}
