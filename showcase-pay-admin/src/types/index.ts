export interface Order {
  id: number
  orderNo: string
  userId: number
  amount: number
  currency: string
  status: string
  subject: string
  description: string
  payTime: string
  expireTime: string
  createTime: string
}

export interface Payment {
  id: number
  paymentNo: string
  orderId: number
  orderNo: string
  userId: number
  amount: number
  currency: string
  paymentMethod: string
  status: string
  transactionId: string
  channelCode: string
  subject: string
  body: string
  payTime: string
  expireTime: string
  errorCode: string
  errorMsg: string
  retryCount: number
  createTime: string
  updateTime: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface Result<T> {
  code: number
  message: string
  data: T
}

export interface ServiceInfo {
  name: string
  url: string
  status: 'UP' | 'DOWN' | 'UNKNOWN'
  details?: Record<string, unknown>
}
