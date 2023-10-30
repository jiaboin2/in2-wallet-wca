package es.in2.wallet.wca.model.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class VerifiableCredentialByIdAndFormatRequestDTO(
    @JsonProperty("id") val id: String,
    @JsonProperty("format") val format: String
)
