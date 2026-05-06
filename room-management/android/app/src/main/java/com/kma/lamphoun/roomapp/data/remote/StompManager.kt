package com.kma.lamphoun.roomapp.data.remote

import com.google.gson.Gson
import com.kma.lamphoun.roomapp.data.remote.dto.WsPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StompManager @Inject constructor() {

    private var stompClient: StompClient? = null
    private val gson = Gson()

    private val _events = MutableSharedFlow<WsPayload>(extraBufferCapacity = 16)
    val events: SharedFlow<WsPayload> = _events.asSharedFlow()

    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5
    private var currentToken: String? = null

    fun connect(token: String) {
        if (stompClient?.isConnected == true) return
        currentToken = token

        val url = "ws://10.0.2.2:8080/ws/websocket"
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)

        val headers = listOf(StompHeader("Authorization", "Bearer $token"))

        stompClient?.connect(headers)

        stompClient?.topic("/user/queue/events")
            ?.subscribe { message ->
                try {
                    val payload = gson.fromJson(message.payload, WsPayload::class.java)
                    _events.tryEmit(payload)
                } catch (e: Exception) {
                    // ignore malformed messages
                }
            }

        stompClient?.lifecycle()?.subscribe { event ->
            when (event.type) {
                LifecycleEvent.Type.CLOSED,
                LifecycleEvent.Type.ERROR -> scheduleReconnect()
                LifecycleEvent.Type.OPENED -> reconnectAttempts = 0
                else -> {}
            }
        }
    }

    fun disconnect() {
        stompClient?.disconnect()
        stompClient = null
        reconnectAttempts = 0
        currentToken = null
    }

    private fun scheduleReconnect() {
        val token = currentToken ?: return
        if (reconnectAttempts >= maxReconnectAttempts) return
        val delayMs = minOf(2000L * (1L shl reconnectAttempts), 30_000L)
        reconnectAttempts++
        CoroutineScope(Dispatchers.IO).launch {
            delay(delayMs)
            connect(token)
        }
    }
}
