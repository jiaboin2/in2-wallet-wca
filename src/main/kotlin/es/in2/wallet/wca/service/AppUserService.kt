package es.in2.wallet.wca.service

import es.in2.wallet.wca.model.entity.AppUser
import java.util.*

interface AppUserService {
    fun getUserWithContextAuthentication(): AppUser
    fun getUserByUsername(username: String): Optional<AppUser>

}

