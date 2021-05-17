package com.mirenzen.scp_001.users.view_models

import com.mirenzen.scp_001.app.enums.PageState
import com.mirenzen.scp_001.app.objects.ListOptionSection
import com.mirenzen.scp_001.app.view_models.BaseViewModel
import com.mirenzen.scp_001.auth.util.AuthMan
import com.mirenzen.scp_001.users.UsersService
import com.mirenzen.scp_001.users.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(
    private val usersService: UsersService,
    private val authMan: AuthMan
) : BaseViewModel<ListOptionSection>() {
    var user: User? = null
        private set

    override suspend fun paginate(refresh: Boolean): Result<Nothing?> = withContext(Dispatchers.IO) {
        state = PageState.Fetching
        val result = usersService.getUserByUsername(authMan.payload?.username ?: "")
        state = PageState.Bottom

        when (result.isFailure) {
            true -> Result.failure(result.exceptionOrNull()!!)
            else -> {
                user = result.getOrNull()
                Result.success(null)
            }
        }
    }
}