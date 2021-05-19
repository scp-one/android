package com.mirenzen.scp_001.app.view_models

class EmptyViewModel<T> : BaseViewModel<T>() {
    override suspend fun paginate(refresh: Boolean): Result<Nothing?> {
        return Result.success(null)
    }
}