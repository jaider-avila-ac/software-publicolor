package com.publicolor.receipt.service;

import com.publicolor.receipt.dto.ReciboPdfRequest;

public interface ReciboPdfService {
    byte[] generarPdf(ReciboPdfRequest req);
}
