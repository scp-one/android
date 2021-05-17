package com.mirenzen.scp_001.app.interfaces

interface BindableView<T> {
    suspend fun bind(item: T)
}