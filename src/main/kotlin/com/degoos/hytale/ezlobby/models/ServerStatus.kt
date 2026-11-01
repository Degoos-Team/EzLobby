package com.degoos.hytale.ezlobby.models

enum class ServerStatus {
    UNKNOWN,    // initial / never checked
    CHECKING,   // probe in-flight
    ONLINE,     // last probe succeeded
    OFFLINE     // last probe failed or timed out
}
