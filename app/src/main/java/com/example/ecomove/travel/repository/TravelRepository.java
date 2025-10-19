package com.example.ecomove.travel.repository;

import com.example.ecomove.api.ApiService;
import com.example.ecomove.travel.model.TravelVerificationRequest;
import com.example.ecomove.travel.qr.QRDecoder;

import retrofit2.Call;

public class TravelRepository {
    private final QRDecoder qrDecoder;
    private final ApiService apiService;

    public TravelRepository(QRDecoder qrDecoder, ApiService apiService) {
        this.qrDecoder = qrDecoder;
        this.apiService = apiService;
    }

    public TravelVerificationRequest getTravelInfoFromQr(String content) {
        return qrDecoder.decode(content);
    }

    public Call<Void> submitTravelVerification(TravelVerificationRequest request) {
        return apiService.verifyTravel(request);
    }
}