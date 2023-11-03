package es.in2.wallet.controller

import es.in2.wallet.wca.controller.SiopController
import es.in2.wallet.wca.model.dto.QrContentDTO
import es.in2.wallet.wca.model.dto.VcBasicDataDTO
import es.in2.wallet.wca.model.dto.VcSelectorRequestDTO
import es.in2.wallet.wca.service.SiopService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*
import org.mockito.Mockito.`when`
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@SpringJUnitConfig
@SpringBootTest
@AutoConfigureMockMvc
class SiopControllerTest {

    @Mock
    private lateinit var siopService: SiopService

    @InjectMocks
    private lateinit var siopController: SiopController

    private lateinit var mockMvc: MockMvc

    // TEST DATA
    private val vcSelectorRequestDTO = VcSelectorRequestDTO(
            redirectUri = "http://portal-api:8082/api/verifier/siop-sessions",
            state = "gFfLfeHuTouHjDwoe9vvQw",
            selectableVcList = listOf(
                    VcBasicDataDTO(
                            id = "urn:uuid:59dabec0-a6f1-4455-8f3f-c13955f27bba",
                            vcType = mutableListOf("VerifiableCredential", "VerifiableAttestation", "VerifiableId"),
                            credentialSubject = "default data"
                    )
            )
    )

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(SiopControllerTest::class.java)
        mockMvc = MockMvcBuilders.standaloneSetup(siopController).build()
    }

    @Test
    fun `getSiopAuthenticationRequest should return 200 OK`() {
        val qrContentDTO = QrContentDTO(content = "http://localhost:8082/api/login/vc/status?state=zI27IGgPSDKv91BHRzg9ag")

        `when`(siopService.processSiopAuthenticationRequest("{\"qr_content\":\"${qrContentDTO.content}\"}")).thenReturn(vcSelectorRequestDTO)

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/siop/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"qr_content\":\"$qrContentDTO.content\"}")
        )
                .andExpect(MockMvcResultMatchers.status().isOk)
                //.andExpect(MockMvcResultMatchers.jsonPath("$.redirectUri").value("http://portal-api:8082/api/verifier/siop-sessions"))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.state").value("gFfLfeHuTouHjDwoe9vvQw"))


    }

    @Test
    fun `processSiopAuthenticationRequest should return 201 OK`() {
        val qrContentDTO = QrContentDTO(content = "http://localhost:8082/api/login/vc/status?state=zI27IGgPSDKv91BHRzg9ag")

        `when`(siopService.processSiopAuthenticationRequest("{\"qr_content\":\"${qrContentDTO.content}\"}")).thenReturn(vcSelectorRequestDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/siop/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"qr_content\":\"$qrContentDTO.content\"}")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

    }
}