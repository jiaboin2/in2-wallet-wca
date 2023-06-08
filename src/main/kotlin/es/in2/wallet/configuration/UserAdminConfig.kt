package es.in2.wallet.configuration

import es.in2.wallet.model.AppUser
import es.in2.wallet.repository.AppUserRepository
import es.in2.wallet.service.PersonalDataSpaceService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

@Configuration
class UserAdminConfig(
    private val appUserRepository: AppUserRepository,
    private val personalDataSpaceService: PersonalDataSpaceService
) {

    private val log: Logger = LoggerFactory.getLogger(UserAdminConfig::class.java)
    private val defaultUUID = "3c56da39-d9c7-40e9-8555-d77400a26211"
    private val defaultUsername = "in2admin"
    private val defaultEmail = "in2admin@example.com"
    private val defaultCredential = "in2pass"
    private val defaultVcId = "urn:uuid:59dabec0-a6f1-4455-8f3f-c13955f27bba"
    private val defaultVc =
        "eyJraWQiOiJkaWQ6a2V5OnpRM3NocjU2WldLQmg2MXV4WjRUS0d2ODNSUHlMOHV2NnhjR3RTUkpRVmN3QXJhaTQjelEzc2hyNTZaV0tCaDYxdXhaNFRLR3Y4M1JQeUw4dXY2eGNHdFNSSlFWY3dBcmFpNCIsInR5cCI6IkpXVCIsImFsZyI6IkVTMjU2SyJ9.eyJzdWIiOiJkaWQ6a2V5OnpRM3NoV2J4aFhrcVJGSFNDVDR1Q2lyRU51NnhMdkhjZWZIMTRoWXpSQnlBd0NGNGYiLCJuYmYiOjE2ODU2MTE2NjUsImlzcyI6ImRpZDprZXk6elEzc2hyNTZaV0tCaDYxdXhaNFRLR3Y4M1JQeUw4dXY2eGNHdFNSSlFWY3dBcmFpNCIsImV4cCI6MTY4ODIwMzY2NSwiaWF0IjoxNjg1NjExNjY1LCJ2YyI6eyJ0eXBlIjpbIlZlcmlmaWFibGVDcmVkZW50aWFsIiwiVmVyaWZpYWJsZUF0dGVzdGF0aW9uIiwiVmVyaWZpYWJsZUlkIl0sIkBjb250ZXh0IjpbImh0dHBzOi8vd3d3LnczLm9yZy8yMDE4L2NyZWRlbnRpYWxzL3YxIl0sImlkIjoidXJuOnV1aWQ6NTlkYWJlYzAtYTZmMS00NDU1LThmM2YtYzEzOTU1ZjI3YmJhIiwiaXNzdWVyIjoiZGlkOmtleTp6UTNzaHI1NlpXS0JoNjF1eFo0VEtHdjgzUlB5TDh1djZ4Y0d0U1JKUVZjd0FyYWk0IiwiaXNzdWFuY2VEYXRlIjoiMjAyMy0wNi0wMVQwOToyNzo0NVoiLCJpc3N1ZWQiOiIyMDIzLTA2LTAxVDA5OjI3OjQ1WiIsInZhbGlkRnJvbSI6IjIwMjMtMDYtMDFUMDk6Mjc6NDVaIiwiZXhwaXJhdGlvbkRhdGUiOiIyMDIzLTA3LTAxVDA5OjI3OjQ1WiIsImNyZWRlbnRpYWxTY2hlbWEiOnsiaWQiOiJodHRwczovL3Jhdy5naXRodWJ1c2VyY29udGVudC5jb20vd2FsdC1pZC93YWx0aWQtc3Npa2l0LXZjbGliL21hc3Rlci9zcmMvdGVzdC9yZXNvdXJjZXMvc2NoZW1hcy9WZXJpZmlhYmxlSWQuanNvbiIsInR5cGUiOiJGdWxsSnNvblNjaGVtYVZhbGlkYXRvcjIwMjEifSwiY3JlZGVudGlhbFN1YmplY3QiOnsiaWQiOiJkaWQ6a2V5OnpRM3NoV2J4aFhrcVJGSFNDVDR1Q2lyRU51NnhMdkhjZWZIMTRoWXpSQnlBd0NGNGYiLCJjdXJyZW50QWRkcmVzcyI6WyIxIEJvdWxldmFyZCBkZSBsYSBMaWJlcnTDqSwgNTk4MDAgTGlsbGUiXSwiZGF0ZU9mQmlydGgiOiIxOTkzLTA0LTA4IiwiZmFtaWx5TmFtZSI6IkRPRSIsImZpcnN0TmFtZSI6IkphbmUiLCJnZW5kZXIiOiJGRU1BTEUiLCJuYW1lQW5kRmFtaWx5TmFtZUF0QmlydGgiOiJKYW5lIERPRSIsInBlcnNvbmFsSWRlbnRpZmllciI6IjA5MDQwMDgwODRIIiwicGxhY2VPZkJpcnRoIjoiTElMTEUsIEZSQU5DRSIsInByb3BlcnRpZXMiOnsiY3VycmVudEFkZHJlc3MiOlsiMSBCb3VsZXZhcmQgZGUgbGEgTGliZXJ0w6ksIDU5ODAwIExpbGxlIl0sImRhdGVPZkJpcnRoIjoiMTk5My0wNC0wOCIsImZhbWlseU5hbWUiOiJET0UiLCJmaXJzdE5hbWUiOiJKYW5lIiwiZ2VuZGVyIjoiRkVNQUxFIiwibmFtZUFuZEZhbWlseU5hbWVBdEJpcnRoIjoiSmFuZSBET0UiLCJwZXJzb25hbElkZW50aWZpZXIiOiIwOTA0MDA4MDg0SCIsInBsYWNlT2ZCaXJ0aCI6IkxJTExFLCBGUkFOQ0UifX0sImV2aWRlbmNlIjpbeyJkb2N1bWVudFByZXNlbmNlIjpbIlBoeXNpY2FsIl0sImV2aWRlbmNlRG9jdW1lbnQiOlsiUGFzc3BvcnQiXSwic3ViamVjdFByZXNlbmNlIjoiUGh5c2ljYWwiLCJ0eXBlIjpbIkRvY3VtZW50VmVyaWZpY2F0aW9uIl0sInZlcmlmaWVyIjoiZGlkOmVic2k6MkE5Qlo5U1VlNkJhdGFjU3B2czFWNUNkakh2THBRN2JFc2kySmI2TGRIS25ReGFOIn1dfSwianRpIjoidXJuOnV1aWQ6NTlkYWJlYzAtYTZmMS00NDU1LThmM2YtYzEzOTU1ZjI3YmJhIn0.qSrtO57bDOt8wCV9uU3Xn0Y6g2ou0Y7n5tZQXpHjdxG4BSN_anqyoN_jeKx-cy1lvOsuL-8UUaJhbTibITDt-Q"


    @Bean
    fun defaultUserAdmin(): AppUser {
        log.info("UserAdminConfiguration.defaultUserAdmin()")
        // check if user is already persisted in past instances
        val existingUser = appUserRepository.findAppUserByEmail(defaultEmail)
        return if (existingUser.isPresent) {
            setSession()
            setContextBrokerDataSet(personalDataSpaceService)
            existingUser.get()
        } else {
            val adminUser = setAppUser()
            appUserRepository.save(adminUser)
            setSession()
            setContextBrokerDataSet(personalDataSpaceService)
            adminUser
        }
    }

    private fun setAppUser(): AppUser {
        return AppUser(
            id = UUID.fromString(defaultUUID),
            username = defaultUsername,
            email = defaultEmail,
            password = BCryptPasswordEncoder().encode(defaultCredential)
        )
    }

    private fun setSession() {
        val authentication = UsernamePasswordAuthenticationToken(defaultUsername, defaultCredential, ArrayList())
        val securityContext = SecurityContextImpl()
        securityContext.authentication = authentication
        SecurityContextHolder.setContext(securityContext)
    }

    private fun setContextBrokerDataSet(personalDataSpaceService: PersonalDataSpaceService) {
        val response = personalDataSpaceService.getAllVerifiableCredentialsByAppUser()
        log.info("response = $response")
        if(response.isEmpty()) {
            personalDataSpaceService.saveVC(defaultVc)
        } else {
            personalDataSpaceService.deleteVC(defaultVcId)
            personalDataSpaceService.saveVC(defaultVc)
        }
    }

}
