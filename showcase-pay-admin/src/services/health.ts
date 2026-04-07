import axios from 'axios'

export interface HealthResponse {
  status: string
  components?: Record<string, { status: string; details?: Record<string, unknown> }>
}

// Handle both Spring Boot Actuator format and custom Result wrapper format
function normalizeHealthResponse(data: any): HealthResponse {
  // Spring Boot Actuator format: { status: 'UP', components: {...} }
  if (data.status) {
    return data as HealthResponse;
  }
  // Custom Result wrapper format: { code: 200, success: true, data: '...' }
  if (data.code === 200 || data.success === true) {
    return { status: 'UP' };
  }
  return { status: 'DOWN' };
}

export async function checkServiceHealth(url: string): Promise<HealthResponse> {
  try {
    const response = await axios.get<any>(url, { timeout: 5000 })
    return normalizeHealthResponse(response.data)
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
