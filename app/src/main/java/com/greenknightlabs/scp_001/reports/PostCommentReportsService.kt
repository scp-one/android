package com.greenknightlabs.scp_001.reports

import com.greenknightlabs.scp_001.app.util.ApiErrorHandler
import com.greenknightlabs.scp_001.auth.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class PostCommentReportsService @Inject constructor(
    private val postCommentReportsServiceApi: PostCommentReportsServiceApi,
    private val authService: AuthService,
    private val apiErrorHandler: ApiErrorHandler
) {
    @Throws
    suspend fun createPostCommentReport(id: String) = withContext(Dispatchers.IO) {
        try {
            val accessToken = authService.getAccessTokenAsBearer()
            postCommentReportsServiceApi.createPostCommentReport(accessToken, id)
        } catch (e: Throwable) {
            Timber.e(e)
            apiErrorHandler.throwApiError(e)
        }
    }
}
