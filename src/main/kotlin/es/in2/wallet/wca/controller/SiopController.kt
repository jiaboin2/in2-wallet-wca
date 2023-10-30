package es.in2.wallet.wca.controller

import es.in2.wallet.api.model.dto.QrContentDTO
import es.in2.wallet.wca.model.dto.VcSelectorRequestDTO
import es.in2.wallet.wca.service.SiopService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.websocket.server.PathParam
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/siop")
class SiopController (
        private val siopService: SiopService
){

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Siop Authentication Request",
        description = "Get Siop Authentication Request"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Authentication data successfully saved."),
        ApiResponse(responseCode = "400", description = "Invalid request."),
        ApiResponse(responseCode = "403", description = "Access token has expired"),
        ApiResponse(responseCode = "500", description = "Internal server error.")
    ])
    fun getSiopAuthenticationRequest(@RequestBody qrContentDTO: QrContentDTO): VcSelectorRequestDTO {
       return siopService.getSiopAuthenticationRequest(qrContentDTO.content)
    }

    @PostMapping("/process")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Siop Authentication Request",
        description = "Process Siop Authentication Request"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Authentication data successfully saved."),
        ApiResponse(responseCode = "400", description = "Invalid request."),
        ApiResponse(responseCode = "403", description = "Access token has expired"),
        ApiResponse(responseCode = "500", description = "Internal server error.")
    ])
    fun processSiopAuthenticationRequest(@RequestBody qrContentDTO: QrContentDTO): VcSelectorRequestDTO {
        return siopService.processSiopAuthenticationRequest(qrContentDTO.content)
    }

}