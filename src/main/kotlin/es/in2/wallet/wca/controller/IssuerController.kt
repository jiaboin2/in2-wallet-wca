package es.in2.wallet.wca.controller

import es.in2.wallet.wca.exception.NoAuthorizationFoundException
import es.in2.wallet.wca.service.IssuerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*

@Tag(name = "Issuers", description = "Issuers management API")
@RestController
@RequestMapping("/api/issuers")
class IssuerController(
        private val issuerService: IssuerService
) {

    private val log: Logger = LoggerFactory.getLogger(IssuerController::class.java)

    // todo: change to IssuerResponseDTO
    @GetMapping
    @Operation(
            summary = "Get list of Issuers",
            description = "Retrieve a list of Issuers names.",
            tags = ["Issuer Management"]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List of Issuers retrieved successfully."),
        ApiResponse(responseCode = "500", description = "Internal server error.")
    ])
    fun getAllIssuers(@RequestHeader(HttpHeaders.AUTHORIZATION) authorizationHeader: String): List<String> {
        if (authorizationHeader.isEmpty() || !authorizationHeader.startsWith("Bearer ")) {
            val errorMessage = "No Bearer token found in Authorization header"
            throw NoAuthorizationFoundException(errorMessage)
        }
        log.debug("AppIssuerController.getAllIssuers()")
        return issuerService.getIssuers()
    }

}