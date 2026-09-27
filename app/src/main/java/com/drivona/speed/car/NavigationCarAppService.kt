package com.drivona.speed.car

import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

// NavigationCarAppService.kt - 应用入口
class NavigationCarAppService : CarAppService() {
    override fun onCreateSession(): Session {
        return NavSession()
    }

    override fun createHostValidator(): HostValidator {

           return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR

    }
}