package com.kma.lamphoun.roomapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kma.lamphoun.roomapp.ui.navigation.RoomNavGraph
import com.kma.lamphoun.roomapp.ui.theme.RoomAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoomAppTheme {
                RoomNavGraph()
            }
        }
    }
}

