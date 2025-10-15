package com.web.bookingservice.service.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.web.bookingservice.exception.AppException;
import com.web.bookingservice.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class QRCodeServiceImpl implements QRCodeService {

    private static final String SECRET_KEY = "TICKET_SECRET";
    private static final int QR_CODE_SIZE = 300;

    @Override
    public String generateQRCode(Integer ticketId) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String data = ticketId + "|" + timestamp + "|" + SECRET_KEY;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            String hashString = Base64.getEncoder().encodeToString(hash);

            return ticketId + "|" + timestamp + "|" + hashString.substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @Override
    public byte[] generateQRCodeImage(String qrData) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @Override
    public boolean validateQRCode(String qrCode) {
        try {
            String[] parts = qrCode.split("\\|");
            if (parts.length != 3) {
                return false;
            }

            String ticketId = parts[0];
            String timestamp = parts[1];
            String providedHash = parts[2];

            String data = ticketId + "|" + timestamp + "|" + SECRET_KEY;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            String expectedHash = Base64.getEncoder().encodeToString(hash).substring(0, 16);

            return expectedHash.equals(providedHash);
        } catch (Exception e) {
            return false;
        }
    }
}
