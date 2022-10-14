package com.greenknightlabs.scp_001.reports

import com.greenknightlabs.scp_001.app.util.ApiErrorHandler
import com.greenknightlabs.scp_001.auth.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class PostReportsService @Inject constructor(
    private val postReportsServiceApi: PostReportsServiceApi,
    private val authService: AuthService,
    private val apiErrorHandler: ApiErrorHandler
) {
    @Throws
    suspend fun createPostReport(id: String) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            postReportsServiceApi.createPostReport(accessToken, id)
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }
}
