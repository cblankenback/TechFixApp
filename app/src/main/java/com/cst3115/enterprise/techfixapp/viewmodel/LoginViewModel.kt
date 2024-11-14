package com.cst3115.enterprise.techfixapp.viewmodel

// viewmodel/LoginViewModel.kt



import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cst3115.enterprise.techfixapp.utils.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application.applicationContext)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    // Hardcoded credentials
    private val hardcodedUsername = "cst3115"
    private val hardcodedPassword = "3115"

    fun login(username: String, password: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            // Simulate processing delay
            kotlinx.coroutines.delay(500)
            if (username == hardcodedUsername && password == hardcodedPassword) {
                prefs.saveLoginState(true)
                _loginError.value = null
                onSuccess()
            } else {
                _loginError.value = "Invalid username or password"
            }
            _isLoading.value = false
        }
    }

    fun checkLoginState(): Boolean {
        return prefs.isLoggedIn()
    }

    fun logout() {
        prefs.clearLoginState()
    }
}
