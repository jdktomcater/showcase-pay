import axios from 'axios'
import type { Result, PageResult, Order, Payment } from '../types'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// Request interceptor
api.interceptors.request.use(
  (config) => config,
  (error) => Promise.reject(error),
)

// Response interceptor
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    console.error('API Error:', error)
    return Promise.reject(error)
  },
)

export const orderApi = {
  // Get order list by user ID
  getList: (userId: number, pageNum = 1, pageSize = 10) =>
    api.get<Result<PageResult<Order>>>(`/orders/user/${userId}`, {
      params: { pageNum, pageSize },
    }),

  // Get order detail
  getDetail: (orderNo: string) =>
    api.get<Result<Order>>(`/orders/${orderNo}`),

  // Cancel order
  cancel: (orderNo: string) =>
    api.post<Result<void>>(`/orders/${orderNo}/cancel`),
}

export const paymentApi = {
  // Get payment list by user ID
  getList: (userId: number, pageNum = 1, pageSize = 10) =>
    api.get<Result<PageResult<Payment>>>(`/payments/list/${userId}`, {
      params: { pageNum, pageSize },
    }),

  // Query payment detail
  queryDetail: (paymentNo: string) =>
    api.post<Result<Payment>>('/payment/query', { paymentNo }),

  // Query payment status from gateway
  queryStatus: (paymentNo: string) =>
    api.get<Result<Payment>>(`/payment/status/${paymentNo}`),

  // Cancel payment
  cancel: (paymentNo: string) =>
    api.post<Result<string>>(`/payment/cancel/${paymentNo}`),

  // Process refund
  refund: (paymentNo: string, refundAmount: number, reason: string) =>
    api.post<Result<string>>('/payment/refund', {
      paymentNo,
      refundAmount,
      reason,
    }),
}

export default api
