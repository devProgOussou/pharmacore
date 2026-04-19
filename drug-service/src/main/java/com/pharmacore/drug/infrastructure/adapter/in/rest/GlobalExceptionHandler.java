package com.pharmacore.drug.infrastructure.adapter.in.rest;

import com.pharmacore.drug.domain.exception.DrugNotFoundException;
import com.pharmacore.drug.domain.exception.DuplicateDciException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DrugNotFoundException.class)
    public ProblemDetail handleNotFound(DrugNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create("/errors/drug-not-found"));
        pd.setTitle("Médicament introuvable");
        return pd;
    }

    @ExceptionHandler(DuplicateDciException.class)
    public ProblemDetail handleDuplicate(DuplicateDciException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("/errors/duplicate-dci"));
        pd.setTitle("DCI déjà existante");
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + " : " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, details);
        pd.setType(URI.create("/errors/validation"));
        pd.setTitle("Données invalides");
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur inattendue est survenue");
        pd.setType(URI.create("/errors/internal"));
        pd.setTitle("Erreur interne");
        return pd;
    }
}