package com.algaworks.algafood.infrastructure.service.report;

public class ReportException extends RuntimeException {

    private static final long serialVersionUID = -5658433399665980597L;

    public ReportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportException(String message) {
        super(message);
    }

}
