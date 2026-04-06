import { useState, useEffect } from 'react'
import { Row, Col, Card, Statistic, Table, Tag, Typography } from 'antd'
import {
  FileTextOutlined,
  CreditCardOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
} from '@ant-design/icons'
import { orderApi, paymentApi } from '../../services/api'
import type { Order, Payment } from '../../types'

const { Title } = Typography

// Mock user ID for demo purposes
const DEMO_USER_ID = 1001

export default function Dashboard() {
  const [stats, setStats] = useState({
    totalOrders: 0,
    totalPayments: 0,
    paidOrders: 0,
    pendingPayments: 0,
  })
  const [recentOrders, setRecentOrders] = useState<Order[]>([])
  const [recentPayments, setRecentPayments] = useState<Payment[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      setLoading(true)
      const [orderRes, paymentRes] = await Promise.all([
        orderApi.getList(DEMO_USER_ID, 1, 5),
        paymentApi.getList(DEMO_USER_ID, 1, 5),
      ])

      const orders = (orderRes as any).data?.records || []
      const payments = (paymentRes as any).data?.records || []

      setRecentOrders(orders)
      setRecentPayments(payments)
      setStats({
        totalOrders: (orderRes as any).data?.total || orders.length,
        totalPayments: (paymentRes as any).data?.total || payments.length,
        paidOrders: orders.filter((o: Order) => o.status === 'PAID').length,
        pendingPayments: payments.filter((p: Payment) => p.status === 'PENDING').length,
      })
    } catch (error) {
      console.error('Failed to load dashboard data:', error)
    } finally {
      setLoading(false)
    }
  }

  const getStatusColor = (status: string) => {
    const colorMap: Record<string, string> = {
      PAID: 'success',
      SUCCESS: 'success',
      CREATED: 'processing',
      PENDING: 'warning',
      PENDING_PAYMENT: 'default',
      CANCELLED: 'error',
      FAILED: 'error',
      REFUNDED: 'default',
    }
    return colorMap[status] || 'default'
  }

  const orderColumns = [
    {
      title: '订单号',
      dataIndex: 'orderNo',
      key: 'orderNo',
    },
    {
      title: '金额',
      dataIndex: 'amount',
      key: 'amount',
      render: (val: number, record: Order) => `${record.currency} ${val?.toFixed(2)}`,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => <Tag color={getStatusColor(status)}>{status}</Tag>,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
    },
  ]

  const paymentColumns = [
    {
      title: '支付单号',
      dataIndex: 'paymentNo',
      key: 'paymentNo',
    },
    {
      title: '金额',
      dataIndex: 'amount',
      key: 'amount',
      render: (val: number, record: Payment) => `${record.currency} ${val?.toFixed(2)}`,
    },
    {
      title: '支付方式',
      dataIndex: 'paymentMethod',
      key: 'paymentMethod',
      render: (method: string) => <Tag>{method}</Tag>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => <Tag color={getStatusColor(status)}>{status}</Tag>,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
    },
  ]

  return (
    <div>
      <Title level={3} style={{ marginBottom: 24 }}>
        控制台
      </Title>

      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card loading={loading}>
            <Statistic
              title="总订单数"
              value={stats.totalOrders}
              prefix={<FileTextOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card loading={loading}>
            <Statistic
              title="总支付数"
              value={stats.totalPayments}
              prefix={<CreditCardOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card loading={loading}>
            <Statistic
              title="已支付订单"
              value={stats.paidOrders}
              prefix={<CheckCircleOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card loading={loading}>
            <Statistic
              title="待处理支付"
              value={stats.pendingPayments}
              prefix={<ClockCircleOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        <Col xs={24} lg={12}>
          <Card title="最近订单" style={{ height: '100%' }}>
            <Table
              columns={orderColumns}
              dataSource={recentOrders}
              rowKey="orderNo"
              pagination={false}
              size="small"
              loading={loading}
            />
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card title="最近支付" style={{ height: '100%' }}>
            <Table
              columns={paymentColumns}
              dataSource={recentPayments}
              rowKey="paymentNo"
              pagination={false}
              size="small"
              loading={loading}
            />
          </Card>
        </Col>
      </Row>
    </div>
  )
}
