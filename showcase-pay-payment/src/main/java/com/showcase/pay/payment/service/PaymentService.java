package com.showcase.pay.payment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.showcase.pay.payment.dto.*;

/**
 * Service interface for payment operations.
 */
public interface PaymentService {

    /**
     * Create a new payment record and initiate payment with the gateway.
     *
     * @param request the payment request
     * @return payment response with gateway redirect info or QR code
     */
    PaymentResponse createPayment(PaymentRequest request);

    /**
     * Process a callback notification from a payment gateway.
     *
     * @param callbackRequest the callback data
     * @return processing result message
     */
    String processCallback(PaymentCallbackRequest callbackRequest);

    /**
     * Query payment record details.
     *
     * @param queryRequest the query criteria
     * @return payment detail
     */
    PaymentDetailDTO queryPayment(PaymentQueryRequest queryRequest);

    /**
     * Query payment records with pagination.
     *
     * @param userId   the user ID
     * @param pageNum  page number
     * @param pageSize page size
     * @return paginated payment records
     */
    Page<PaymentDetailDTO> queryPaymentList(Long userId, int pageNum, int pageSize);

    /**
     * Process a refund request.
     *
     * @param refundRequest the refund request
     * @return processing result message
     */
    String processRefund(RefundRequest refundRequest);

    /**
     * Query payment status from the third-party gateway.
     *
     * @param paymentNo the payment number
     * @return updated payment detail
     */
    PaymentDetailDTO queryPaymentStatus(String paymentNo);

    /**
     * Cancel a pending payment.
     *
     * @param paymentNo the payment number
     * @return processing result message
     */
    String cancelPayment(String paymentNo);
}
