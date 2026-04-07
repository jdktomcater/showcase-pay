import { useState, useEffect } from 'react'
import { Card, Row, Col, Tag, Space, Button, Typography, List, Badge, Statistic } from 'antd'
import { ReloadOutlined, CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons'
import { checkServiceHealth } from '../../services/health'
import type { HealthResponse } from '../../services/health'

const { Title, Text } = Typography

interface ServiceItem {
  name: string
  url: string
  icon: string
}

const services: ServiceItem[] = [
  {
    name: 'API Gateway',
    url: '/actuator/health',
    icon: '🌐',
  },
  {
    name: 'Order Service',
    url: '/api/orders/health',
    icon: '📋',
  },
  {
    name: 'Payment Service',
    url: '/api/payments/health',
    icon: '💳',
  },
  {
    name: 'MySQL',
    url: '/api/orders/health',
    icon: '🗄️',
  },
  {
    name: 'Redis',
    url: '/api/orders/health',
    icon: '⚡',
  },
  {
    name: 'Nacos',
    url: 'http://localhost:8848/nacos/',
    icon: '🔧',
  },
  {
    name: 'RocketMQ',
    url: 'http://localhost:8090',
    icon: '📨',
  },
  {
    name: 'Elasticsearch',
    url: 'http://localhost:9200',
    icon: '🔍',
  },
  {
    name: 'Kibana',
    url: 'http://localhost:5601',
    icon: '📊',
  },
  {
    name: 'SkyWalking',
    url: 'http://localhost:8085',
    icon: '️',
  },
]

export default function ServiceHealth() {
  const [healthData, setHealthData] = useState<Record<string, HealthResponse>>({})
  const [loading, setLoading] = useState(false)
  const [lastCheck, setLastCheck] = useState<string>('')

  const checkAllHealth = async () => {
    setLoading(true)
    const results: Record<string, HealthResponse> = {}

    // Check API services via proxy
    const apiChecks = [
      { key: 'gateway', url: '/actuator/health' },
      { key: 'order', url: '/api/orders/health' },
      { key: 'payment', url: '/api/payments/health' },
    ]

    for (const check of apiChecks) {
      try {
        const res = await checkServiceHealth(check.url)
        results[check.key] = res
      } catch {
        results[check.key] = { status: 'DOWN' }
      }
    }

    // Check infrastructure services by simple fetch
    const infraChecks = [
      { key: 'nacos', url: 'http://localhost:8848/nacos/' },
      { key: 'rocketmq', url: 'http://localhost:8090' },
      { key: 'es', url: 'http://localhost:9200' },
      { key: 'kibana', url: 'http://localhost:5601' },
      { key: 'skywalking', url: 'http://localhost:8085' },
    ]

    for (const check of infraChecks) {
      try {
        await fetch(check.url, { mode: 'no-cors' })
        results[check.key] = { status: 'UP' }
      } catch {
        results[check.key] = { status: 'DOWN' }
      }
    }

    setHealthData(results)
    setLastCheck(new Date().toLocaleString())
    setLoading(false)
  }

  useEffect(() => {
    checkAllHealth()
    // Auto refresh every 30 seconds
    const timer = setInterval(checkAllHealth, 30000)
    return () => clearInterval(timer)
  }, [])

  const getStatusIcon = (status: string) => {
    return status === 'UP' ? (
      <CheckCircleOutlined style={{ color: '#52c41a', fontSize: 24 }} />
    ) : (
      <CloseCircleOutlined style={{ color: '#ff4d4f', fontSize: 24 }} />
    )
  }

  const upCount = Object.values(healthData).filter((h) => h.status === 'UP').length
  const totalCount = services.length

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <Title level={3} style={{ margin: 0 }}>服务监控</Title>
        <Space>
          <Text type="secondary">最后检查: {lastCheck || '-'}</Text>
          <Button type="primary" icon={<ReloadOutlined />} onClick={checkAllHealth} loading={loading}>
            刷新
          </Button>
        </Space>
      </div>

      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="服务总数"
              value={totalCount}
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="正常运行"
              value={upCount}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="异常服务"
              value={totalCount - upCount}
              valueStyle={{ color: totalCount - upCount > 0 ? '#cf1322' : '#3f8600' }}
            />
          </Card>
        </Col>
      </Row>

      <Card title="服务状态">
        <List
          grid={{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 3, xl: 4 }}
          dataSource={services}
          loading={loading && Object.keys(healthData).length === 0}
          renderItem={(service) => {
            // Simple mapping for service keys
            let key = service.name.toLowerCase().replace(' service', '')
            if (key === 'api gateway') key = 'gateway'
            if (key === 'order service') key = 'order'
            if (key === 'payment service') key = 'payment'
            if (key === 'elasticsearch') key = 'es'

            const serviceHealth = healthData[key] || { status: 'UNKNOWN' }
            
            // MySQL and Redis health depends on order service DB/Redis connection
            const displayStatus = (key === 'mysql' || key === 'redis') 
              ? (healthData.order?.status || serviceHealth.status)
              : serviceHealth.status

            return (
              <List.Item>
                <Card
                  hoverable
                  style={{
                    textAlign: 'center',
                    borderColor: displayStatus === 'UP' ? '#52c41a' : displayStatus === 'DOWN' ? '#ff4d4f' : '#d9d9d9',
                  }}
                >
                  <div style={{ fontSize: 40, marginBottom: 12 }}>{service.icon}</div>
                  <div style={{ marginBottom: 8 }}>
                    <Text strong>{service.name}</Text>
                  </div>
                  <div style={{ marginBottom: 8 }}>
                    {getStatusIcon(displayStatus)}
                  </div>
                  <Tag color={displayStatus === 'UP' ? 'success' : displayStatus === 'DOWN' ? 'error' : 'default'}>
                    {displayStatus}
                  </Tag>
                </Card>
              </List.Item>
            )
          }}
        />
      </Card>

      <Card title="基础设施详情" style={{ marginTop: 16 }}>
        <Row gutter={[16, 16]}>
          <Col xs={24} md={12}>
            <List
              header={<Text strong>后端服务</Text>}
              dataSource={[
                { name: 'API Gateway', port: 8080 },
                { name: 'Order Service', port: 8082 },
                { name: 'Payment Service', port: 8083 },
              ]}
              renderItem={(item) => (
                <List.Item>
                  <Space>
                    <Badge status={healthData[item.name.toLowerCase().replace(' service', '') === 'api gateway' ? 'gateway' : item.name.toLowerCase().split(' ')[0]]?.status === 'UP' ? 'success' : 'error'} />
                    <Text>{item.name}</Text>
                    <Text type="secondary">:{item.port}</Text>
                  </Space>
                </List.Item>
              )}
            />
          </Col>
          <Col xs={24} md={12}>
            <List
              header={<Text strong>中间件</Text>}
              dataSource={[
                { name: 'MySQL', port: 3306 },
                { name: 'Redis', port: 6379 },
                { name: 'Nacos', port: 8848 },
                { name: 'RocketMQ', port: 9876 },
                { name: 'Elasticsearch', port: 9200 },
                { name: 'SkyWalking', port: 8085 },
              ]}
              renderItem={(item) => {
                const key = item.name.toLowerCase()
                const mappedKey = key === 'elasticsearch' ? 'es' : key
                // MySQL and Redis health depends on order service
                const effectiveKey = (mappedKey === 'mysql' || mappedKey === 'redis') ? 'order' : mappedKey
                const isUp = healthData[effectiveKey]?.status === 'UP'
                return (
                  <List.Item>
                    <Space>
                      <Badge status={isUp ? 'success' : 'error'} />
                      <Text>{item.name}</Text>
                      <Text type="secondary">:{item.port}</Text>
                    </Space>
                  </List.Item>
                )
              }}
            />
          </Col>
        </Row>
      </Card>
    </div>
  )
}
