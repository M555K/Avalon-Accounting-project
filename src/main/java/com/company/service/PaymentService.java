package com.company.service;

import com.company.dto.CompanyDto;
import com.company.dto.PaymentDto;
import com.company.entity.ChargeRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

import java.util.List;

public interface PaymentService {
    PaymentDto getPaymentById(Long id);
    List<PaymentDto> getAllPaymentsForSpecificYear(Integer year);
    public Charge charge(ChargeRequest chargeRequest) throws StripeException;

    PaymentDto confirmPayment(Long paymentId, String stripeId);
}
