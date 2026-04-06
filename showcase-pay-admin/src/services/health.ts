import axios from 'axios'

const gatewayBase = '/api'

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
  checkGateway: () => checkServiceHealth(`${gatewayBase}/orders/health`),

  // Check order service health (via gateway)
  checkOrder: () => checkServiceHealth(`${gatewayBase}/orders/health`),

  // Check payment service health (via gateway)
  checkPayment: () => checkServiceHealth(`${gatewayBase}/payment/health`),
}

export default healthApi
