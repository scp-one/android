package com.greenknightlabs.scp_001.users.view_models

import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.objects.ListOptionSection
import com.greenknightlabs.scp_001.app.view_models.BaseViewModel
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.users.UsersService
import com.greenknightlabs.scp_001.users.models.User
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
        var result: Result<Nothing?>
        state = PageState.Fetching

        try {
            val username = authMan.payload?.username ?: ""
            val _user = usersService.getUserByUsername(username)
            user = _user
            result = Result.success(null)
        } catch (e: Throwable) {
            result = Result.failure(e)
        }

        state = PageState.Bottom
        result
    }
}