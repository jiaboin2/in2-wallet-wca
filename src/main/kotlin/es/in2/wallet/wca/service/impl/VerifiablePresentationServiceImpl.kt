package es.in2.wallet.wca.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jwt.SignedJWT
import es.in2.wallet.api.util.ApplicationUtils
import es.in2.wallet.api.util.CONTENT_TYPE
import es.in2.wallet.api.util.CONTENT_TYPE_APPLICATION_JSON
import es.in2.wallet.wca.model.dto.VcSelectorResponseDTO
import es.in2.wallet.integration.orion.service.OrionService
import es.in2.wallet.wca.service.SiopService
import es.in2.wallet.wca.service.VerifiablePresentationService
import es.in2.wallet.api.util.VC_JWT
import es.in2.wallet.wca.model.dto.VcBasicDataDTO
import es.in2.wallet.wca.model.dto.VerifiableCredentialByIdAndFormatRequestDTO
import es.in2.wallet.wca.service.WalletDidService
import es.in2.wallet.wca.util.GET_DID_KEY
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
    @Value("\${app.url.orion-service-baseurl}") private val orionServiceBaseUrl: String,
    @Value("\${app.url.didKey-service-baseurl}") private val didKeyServiceBaseUrl: String
) : VerifiablePresentationService {

    private val log: Logger = LogManager.getLogger(VerifiablePresentationServiceImpl::class.java)

    override fun createVerifiablePresentation(vcSelectorResponseDTO: VcSelectorResponseDTO): String {

        // Get vc_jwt list from the selected list of VCs received
        val verifiableCredentialsList = mutableListOf<PresentableCredential>()
        vcSelectorResponseDTO.selectedVcList.forEach {
            val headers = listOf( CONTENT_TYPE to CONTENT_TYPE_APPLICATION_JSON)
            val vc = parserVerifiableCredentialIdAndFormatRequestToString(VerifiableCredentialByIdAndFormatRequestDTO(
                id = it.id,
                format = VC_JWT
            ))
            val response = ApplicationUtils.postRequest(url = orionServiceBaseUrl, headers = headers, body = vc)
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
        val url = didKeyServiceBaseUrl + GET_DID_KEY
        val headers = listOf( CONTENT_TYPE to CONTENT_TYPE_APPLICATION_JSON)
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
