package es.in2.wallet.controller

import es.in2.wallet.service.PersonalDataSpaceService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "Verifiable Credentials", description = "Verifiable Credential management API")
@RestController
@RequestMapping("/api/vc")
class VerifiableCredentialController(
    private val personalDataSpaceService: PersonalDataSpaceService
) {
    @PostMapping("/{userid}")
    @ResponseStatus(HttpStatus.CREATED)
    fun createVerifiableCredential(@RequestBody verifiableCredential: String, @PathVariable userid: String) {
        personalDataSpaceService.saveVC(verifiableCredential)
    }

    @GetMapping
    fun getVerifiableCredential(): MutableList<String> {
        return personalDataSpaceService.getAllVerifiableCredentialsByAppUser()
    }

    @GetMapping("/{userid}/format/{format}")
    fun getVerifiableCredentialByFormat(@PathVariable format: String, @PathVariable userid: String): String {
        return personalDataSpaceService.getVcListByFormat(format)
    }

    @GetMapping("/{userid}/{verifiableCredentialId}/format/{format}")
    fun getVerifiableCredentialById(
        @PathVariable userid: String, @PathVariable verifiableCredentialId: String,
        @PathVariable format: String
    ): String {
        return personalDataSpaceService.getVcByFormat(verifiableCredentialId, format)
    }

    @DeleteMapping("/{userid}/{verifiableCredentialId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteVerifiableCredential(@PathVariable userid: String, @PathVariable verifiableCredentialId: String) {
        personalDataSpaceService.deleteVC(verifiableCredentialId)
    }

}
