package es.in2.wallet.wca.controller

import es.in2.wallet.wca.model.dto.VcSelectorRequestDTO
import es.in2.wallet.wca.service.SiopService
import jakarta.websocket.server.PathParam
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/siop")
class SiopController (
        private val siopService: SiopService
){

    @GetMapping()
    fun getSiopAuthenticationRequest(@PathParam("uri") uri: String): VcSelectorRequestDTO {
       return siopService.getSiopAuthenticationRequest(uri)
    }

    @GetMapping("/process")
    fun processSiopAuthenticationRequest(@PathParam("request") request: String): VcSelectorRequestDTO {
        return siopService.processSiopAuthenticationRequest(request)
    }

}