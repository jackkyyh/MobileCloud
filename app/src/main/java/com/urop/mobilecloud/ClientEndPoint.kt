package com.urop.mobilecloud

interface ClientEndPoint {
    fun connect(): Boolean
    fun disconnect()
    fun send(obj: Any)
}
