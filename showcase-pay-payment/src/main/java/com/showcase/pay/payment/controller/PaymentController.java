package com.showcase.pay.payment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.showcase.pay.common.result.Result;
import com.showcase.pay.payment.dto.*;
import com.showcase.pay.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for payment operations.
 * Provides endpoints for payment creation, query, callback, refund, and cancellation.
 */
@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment processing API")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Create a new payment.
     *
     * @param request the payment request
     * @return payment response with gateway redirect info or QR code
     */
    @PostMapping("/create")
    @Operation(summary = "Create payment", description = "Create a new payment and initiate with the payment gateway")
    public Result<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        log.info("Received payment creation request: orderNo={}, method={}",
                request.getOrderNo(), request.getPaymentMethod());
        PaymentResponse response = paymentService.createPayment(request);
        return Result.success("Payment created successfully", response);
    }

    /**
     * Query payment details.
     *
     * @param queryRequest the query criteria
     * @return payment detail
     */
    @PostMapping("/query")
    @Operation(summary = "Query payment", description = "Query payment record details")
    public Result<PaymentDetailDTO> queryPayment(@RequestBody PaymentQueryRequest queryRequest) {
        log.info("Received payment query request: {}", queryRequest);
        PaymentDetailDTO detail = paymentService.queryPayment(queryRequest);
        return Result.success(detail);
    }

    /**
     * Query payment status from the gateway.
     *
     * @param paymentNo the payment number
     * @return updated payment detail
     */
    @GetMapping("/status/{paymentNo}")
    @Operation(summary = "Query payment status", description = "Query payment status from the third-party gateway")
    public Result<PaymentDetailDTO> queryPaymentStatus(
            @Parameter(description = "Payment number") @PathVariable String paymentNo) {
        log.info("Received payment status query: paymentNo={}", paymentNo);
        PaymentDetailDTO detail = paymentService.queryPaymentStatus(paymentNo);
        return Result.success(detail);
    }

    /**
     * Query payment list for a user with pagination.
     *
     * @param userId   the user ID
     * @param pageNum  page number
     * @param pageSize page size
     * @return paginated payment records
     */
    @GetMapping("/list/{userId}")
    @Operation(summary = "Query payment list", description = "Query payment records for a user with pagination")
    public Result<Page<PaymentDetailDTO>> queryPaymentList(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Received payment list query: userId={}, page={}, size={}", userId, pageNum, pageSize);
        Page<PaymentDetailDTO> page = paymentService.queryPaymentList(userId, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * Process payment callback from gateway.
     *
     * @param callbackRequest the callback data
     * @return callback response string
     */
    @PostMapping("/callback")
    @Operation(summary = "Payment callback", description = "Process callback notification from payment gateway")
    public String processCallback(@RequestBody PaymentCallbackRequest callbackRequest) {
        log.info("Received payment callback: paymentNo={}", callbackRequest.getPaymentNo());
        return paymentService.processCallback(callbackRequest);
    }

    /**
     * Cancel a pending payment.
     *
     * @param paymentNo the payment number
     * @return processing result
     */
    @PostMapping("/cancel/{paymentNo}")
    @Operation(summary = "Cancel payment", description = "Cancel a pending or processing payment")
    public Result<String> cancelPayment(
            @Parameter(description = "Payment number") @PathVariable String paymentNo) {
        log.info("Received payment cancel request: paymentNo={}", paymentNo);
        String result = paymentService.cancelPayment(paymentNo);
        return Result.success("Payment cancelled successfully", result);
    }

    /**
     * Process a refund request.
     *
     * @param refundRequest the refund request
     * @return processing result
     */
    @PostMapping("/refund")
    @Operation(summary = "Process refund", description = "Process a refund for a successful payment")
    public Result<String> processRefund(@Valid @RequestBody RefundRequest refundRequest) {
        log.info("Received refund request: paymentNo={}", refundRequest.getPaymentNo());
        String result = paymentService.processRefund(refundRequest);
        return Result.success("Refund processed successfully", result);
    }

    /**
     * Health check endpoint.
     *
     * @return health status
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check payment service health")
    public Result<String> health() {
        return Result.success("Payment service is running");
    }
}
