package es.in2.wallet.wca.service.impl

import es.in2.wallet.wca.model.entity.AppUser
import es.in2.wallet.wca.model.repository.AppUserRepository
import es.in2.wallet.wca.service.AppUserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AppUserServiceImpl(
    private val appUserRepository: AppUserRepository
) : AppUserService {

    private val log: Logger = LoggerFactory.getLogger(AppUserServiceImpl::class.java)

    override fun getUserWithContextAuthentication(): AppUser {
        log.info("AppUserServiceImpl.getUserWithContextAuthentication()")
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        return getUserByUsername(authentication.name).get()
    }

    override fun getUserByUsername(username: String): Optional<AppUser> {
        log.info("AppUserServiceImpl.getUserByUsername()")
        return appUserRepository.findAppUserByUsername(username)
    }


}