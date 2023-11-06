package es.in2.wallet.wca.controller

import es.in2.wallet.wca.exception.NoAuthorizationFoundException
import es.in2.wallet.wca.model.dto.CredentialRequestDTO
import es.in2.wallet.wca.model.dto.QrContentDTO
import es.in2.wallet.wca.service.VerifiableCredentialService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/credentials")
class VerifiableCredentialController(
        private val verifiableCredentialService: VerifiableCredentialService
){
    private val log: Logger = LogManager.getLogger(VerifiableCredentialController::class.java)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Get a verifiable credential",
            description = "Get a verifiable credential and save it in the personal data space."
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Verifiable credential successfully saved."),
        ApiResponse(responseCode = "400", description = "Invalid request."),
        ApiResponse(responseCode = "403", description = "Access token has expired"),
        ApiResponse(responseCode = "500", description = "Internal server error.")
    ])
    fun getVC(@RequestHeader(HttpHeaders.AUTHORIZATION) authorizationHeader: String, @RequestBody credentialRequestDTO: CredentialRequestDTO){
        if (authorizationHeader.isEmpty() || !authorizationHeader.startsWith("Bearer ")) {
            val errorMessage = "No Bearer token found in Authorization header"
            throw NoAuthorizationFoundException(errorMessage)
        }
        val token = authorizationHeader.substring(7)
        log.info("verifiableCredentialService.getVerifiableCredential()")
        verifiableCredentialService.getVerifiableCredential(credentialRequestDTO, token)
    }
    @PostMapping("/issuer-metadata")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Get Credential Issuer Metadata",
            description = "Get the Issuer Metadata of the credential"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Credential data successfully saved."),
        ApiResponse(responseCode = "400", description = "Invalid request."),
        ApiResponse(responseCode = "403", description = "Access token has expired"),
        ApiResponse(responseCode = "500", description = "Internal server error.")
    ])
    fun getCredentialIssuerMetadata(@RequestHeader(HttpHeaders.AUTHORIZATION) authorizationHeader: String, @RequestBody qrContentDTO: QrContentDTO){
        if (authorizationHeader.isEmpty() || !authorizationHeader.startsWith("Bearer ")) {
            val errorMessage = "No Bearer token found in Authorization header"
            throw NoAuthorizationFoundException(errorMessage)
        }
        val token = authorizationHeader.substring(7)
        log.info("verifiableCredentialService.getCredentialIssuerMetadata()")
        verifiableCredentialService.getCredentialIssuerMetadata(qrContentDTO.content, token)
    }
}