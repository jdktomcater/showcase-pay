package com.showcase.pay.payment.strategy;

import com.showcase.pay.common.enums.PaymentMethod;
import com.showcase.pay.payment.dto.PaymentCallbackRequest;
import com.showcase.pay.payment.dto.PaymentRequest;
import com.showcase.pay.payment.dto.PaymentResponse;

/**
 * Strategy interface for different payment gateways.
 * Each payment method (Alipay, WeChat Pay, etc.) implements this interface.
 */
public interface PaymentStrategy {

    /**
     * Get the payment method type this strategy handles.
     *
     * @return the payment method
     */
    PaymentMethod getPaymentMethod();

    /**
     * Initiate a payment with the gateway.
     *
     * @param request the payment request
     * @return payment response with gateway-specific data (redirect URL, QR code, form data, etc.)
     */
    PaymentResponse pay(PaymentRequest request);

    /**
     * Query payment status from the gateway.
     *
     * @param paymentNo the payment number
     * @param extraParams extra parameters for the gateway
     * @return gateway query result as JSON string
     */
    String queryPaymentStatus(String paymentNo, String extraParams);

    /**
     * Process a refund through the gateway.
     *
     * @param paymentNo the payment number
     * @param refundAmount the refund amount
     * @param refundReason the refund reason
     * @return refund result as JSON string
     */
    String refund(String paymentNo, java.math.BigDecimal refundAmount, String refundReason);

    /**
     * Cancel a payment through the gateway.
     *
     * @param paymentNo the payment number
     * @return cancel result as JSON string
     */
    String cancel(String paymentNo);

    /**
     * Verify the callback signature from the gateway.
     *
     * @param callbackRequest the callback request
     * @return true if signature is valid, false otherwise
     */
    boolean verifyCallback(PaymentCallbackRequest callbackRequest);

    /**
     * Build the success response string to return to the gateway.
     *
     * @return the success response string
     */
    String buildSuccessCallbackResponse();

    /**
     * Build the failure response string to return to the gateway.
     *
     * @return the failure response string
     */
    String buildFailureCallbackResponse();
}
