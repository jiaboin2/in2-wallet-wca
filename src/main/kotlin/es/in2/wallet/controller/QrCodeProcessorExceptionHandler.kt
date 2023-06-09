package es.in2.wallet.controller

import es.in2.wallet.exception.NoSuchQrContentException
import es.in2.wallet.exception.NoSuchVerifiableCredentialException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class QrCodeProcessorExceptionHandler {

    private val log: Logger = LoggerFactory.getLogger(QrCodeProcessorExceptionHandler::class.java)

    @ExceptionHandler(NoSuchQrContentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleNoSuchQrContentException(e: Exception): ResponseEntity<Unit> {
        log.error(e.message)
        return ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NoSuchVerifiableCredentialException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoSuchVerifiableCredentialException(e: Exception): ResponseEntity<Unit> {
        log.error(e.message)
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

}