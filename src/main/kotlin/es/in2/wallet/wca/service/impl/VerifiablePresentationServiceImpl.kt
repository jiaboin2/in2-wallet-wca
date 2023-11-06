package es.in2.wallet.wca.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jwt.SignedJWT
import es.in2.wallet.wca.model.dto.VcSelectorResponseDTO
import es.in2.wallet.wca.service.VerifiablePresentationService
import es.in2.wallet.wca.model.dto.VerifiableCredentialByIdAndFormatRequestDTO
import es.in2.wallet.wca.util.*
import id.walt.credentials.w3c.PresentableCredential
import id.walt.credentials.w3c.VerifiableCredential
import id.walt.custodian.Custodian
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class VerifiablePresentationServiceImpl(
    @Value("\${app.url.wallet-data-baseurl}") private val walletDataBaseUrl: String,
    @Value("\${app.url.wallet-crypto-baseurl}") private val walletCryptoBaseUrl: String,
) : VerifiablePresentationService {

    private val log: Logger = LogManager.getLogger(VerifiablePresentationServiceImpl::class.java)

    override fun createVerifiablePresentation(vcSelectorResponseDTO: VcSelectorResponseDTO, token: String): String {

        // Get vc_jwt list from the selected list of VCs received
        val verifiableCredentialsList = mutableListOf<PresentableCredential>()
        vcSelectorResponseDTO.selectedVcList.forEach {
            val url = walletCryptoBaseUrl + GET_VC_BY_ID_FORMAT
            val headers = listOf(
                CONTENT_TYPE to CONTENT_TYPE_APPLICATION_JSON,
                HEADER_AUTHORIZATION to "Bearer $token"
            )
            val vc = parserVerifiableCredentialIdAndFormatRequestToString(VerifiableCredentialByIdAndFormatRequestDTO(
                id = it.id,
                format = VC_JWT
            ))
            val response = ApplicationUtils.postRequest(url = url, headers = headers, body = vc)
            log.info("verifiable credential by Id and Format = {}", response)
            verifiableCredentialsList.add(
                PresentableCredential(
                    verifiableCredential = VerifiableCredential.fromString(response),
                    discloseAll = false
            ))
        }

        // Get Subject DID
        val subjectDid = getSubjectDidFromTheFirstVcOfTheList(verifiableCredentialsList)
        log.info("subject DID = {}", subjectDid)

        /*
            The holder DID MUST be received by the Wallet implementation, and it MUST match with the
            subject_id of, at least, one of the VCs attached.
            That VP MUST be signed using the PrivateKey related with the holderDID.
         */
        val url = walletCryptoBaseUrl + GET_DID_KEY
        val headers = listOf(
            CONTENT_TYPE to CONTENT_TYPE_APPLICATION_JSON,
            HEADER_AUTHORIZATION to "Bearer $token"
        )
        val holderDid = ApplicationUtils.postRequest(url = url, headers = headers, body = "")

        /*
            The holder SHOULD be able to modify the attribute 'expiration_date' by any of its
            Verifiable Presentation.
        */
        val secondsToAdd: Long = 60000

        return Custodian.getService().createPresentation(
            vcs = verifiableCredentialsList,
            holderDid = holderDid,
            expirationDate = Instant.now().plusSeconds(secondsToAdd)
        )
    }

    /**
     * This method parser the VerifiableCredentialByIdAndFormatRequestDTO to a String
     * @param verifiableCredentialByIdAndFormatRequest
     * @return String
     */
    private fun parserVerifiableCredentialIdAndFormatRequestToString(verifiableCredentialByIdAndFormatRequest : VerifiableCredentialByIdAndFormatRequestDTO): String {
        return ObjectMapper().writeValueAsString(verifiableCredentialByIdAndFormatRequest)
    }

    private fun getSubjectDidFromTheFirstVcOfTheList(verifiableCredentialsList: MutableList<PresentableCredential>): String {
        val verifiableCredential = verifiableCredentialsList[0].verifiableCredential.toString()
        val parsedVerifiableCredential = SignedJWT.parse(verifiableCredential)
        val payloadToJson = parsedVerifiableCredential.payload.toJSONObject()
        return payloadToJson["sub"].toString()
    }

}
