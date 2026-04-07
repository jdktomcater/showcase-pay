import axios from 'axios'

export interface HealthResponse {
  status: string
  components?: Record<string, { status: string; details?: Record<string, unknown> }>
}

export async function checkServiceHealth(url: string): Promise<HealthResponse> {
  try {
    const response = await axios.get<HealthResponse>(url, { timeout: 5000 })
    return response.data
  } catch {
    return { status: 'DOWN' }
  }
}

export const healthApi = {
  // Check gateway health
  checkGateway: () => checkServiceHealth('/actuator/health'),

  // Check order service health (via gateway)
  checkOrder: () => checkServiceHealth('/api/orders/health'),

  // Check payment service health (via gateway)
  checkPayment: () => checkServiceHealth('/api/payments/health'),
}

export default healthApi
