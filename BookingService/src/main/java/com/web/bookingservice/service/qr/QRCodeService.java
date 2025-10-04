package com.web.bookingservice.service.qr;

public interface QRCodeService {
    String generateQRCode(Integer ticketId);
    byte[] generateQRCodeImage(String qrData);
    boolean validateQRCode(String qrCode);
}
