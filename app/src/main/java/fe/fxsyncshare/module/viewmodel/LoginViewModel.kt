package fe.fxsyncshare.module.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fe.fxsyncshare.module.fxa.FxaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mozilla.components.service.fxa.FxaAuthData
import mozilla.components.service.fxa.toAuthType

sealed interface LoginState {
    data object Idle : LoginState
    data class Login(val authUrl: String) : LoginState
    data class Finishing(val code: String, val state: String, val action: String) : LoginState
    data class Finished(val success: Boolean) : LoginState
}

class LoginViewModel(private val fxaService: FxaService) : ViewModel() {
    val redirectUrl = fxaService.config.redirectUrl

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state = _state.asStateFlow()

    fun start() = viewModelScope.launch {
        val authUrl = startLogin() ?: return@launch
        _state.emit(LoginState.Login(authUrl))
    }

    fun finish(code: String, state: String, action: String) = viewModelScope.launch {
        _state.emit(LoginState.Finishing(code, state, action))
        val success = finishLogin(code, state, action)
        _state.emit(LoginState.Finished(success))
    }

    private suspend fun startLogin() = withContext(Dispatchers.IO) {
        fxaService.accountManager.beginAuthentication(entrypoint = FxaService.entrypoint)
    }

    private suspend fun finishLogin(code: String, state: String, action: String) = withContext(Dispatchers.IO) {
        fxaService.accountManager.finishAuthentication(FxaAuthData(action.toAuthType(), code = code, state = state))
    }
}


