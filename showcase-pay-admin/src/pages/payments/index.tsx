import { useState, useEffect, useCallback } from 'react'
import {
  Table,
  Card,
  Input,
  Button,
  Tag,
  Space,
  Modal,
  Descriptions,
  message,
  Typography,
  Form,
} from 'antd'
import { SearchOutlined, EyeOutlined, StopOutlined, ReloadOutlined, RollbackOutlined } from '@ant-design/icons'
import { paymentApi } from '../../services/api'
import type { Payment } from '../../types'

const { Title } = Typography
const { Search } = Input

export default function Payments() {
  const [dataSource, setDataSource] = useState<Payment[]>([])
  const [loading, setLoading] = useState(false)
  const [total, setTotal] = useState(0)
  const [current, setCurrent] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [userId, setUserId] = useState<number>(1001)
  const [detailVisible, setDetailVisible] = useState(false)
  const [refundVisible, setRefundVisible] = useState(false)
  const [selectedPayment, setSelectedPayment] = useState<Payment | null>(null)
  const [form] = Form.useForm()

  const loadData = useCallback(async () => {
    setLoading(true)
    try {
      const res = await paymentApi.getList(userId, current, pageSize)
      const data = (res as any).data
      setDataSource(data?.records || [])
      setTotal(data?.total || 0)
    } catch (error) {
      message.error('加载支付列表失败')
      console.error(error)
    } finally {
      setLoading(false)
    }
  }, [userId, current, pageSize])

  useEffect(() => {
    loadData()
  }, [loadData])

  const handleSearch = (value: string) => {
    const id = parseInt(value, 10)
    if (id && id > 0) {
      setUserId(id)
      setCurrent(1)
    } else {
      message.warning('请输入有效的用户ID')
    }
  }

  const handleViewDetail = async (paymentNo: string) => {
    try {
      const res = await paymentApi.queryDetail(paymentNo)
      setSelectedPayment((res as any).data)
      setDetailVisible(true)
    } catch (error) {
      message.error('获取支付详情失败')
    }
  }

  const handleCancel = async (paymentNo: string) => {
    Modal.confirm({
      title: '确认取消',
      content: '确定要取消此支付吗？',
      onOk: async () => {
        try {
          await paymentApi.cancel(paymentNo)
          message.success('支付已取消')
          loadData()
        } catch (error) {
          message.error('取消支付失败')
        }
      },
    })
  }

  const handleRefund = (payment: Payment) => {
    setSelectedPayment(payment)
    form.setFieldsValue({
      refundAmount: payment.amount,
      reason: '',
    })
    setRefundVisible(true)
  }

  const handleRefundSubmit = async () => {
    try {
      const values = await form.validateFields()
      await paymentApi.refund(
        selectedPayment!.paymentNo,
        values.refundAmount,
        values.reason,
      )
      message.success('退款申请已提交')
      setRefundVisible(false)
      form.resetFields()
      loadData()
    } catch (error) {
      message.error('退款申请失败')
    }
  }

  const getStatusColor = (status: string) => {
    const colorMap: Record<string, string> = {
      SUCCESS: 'success',
      PENDING: 'warning',
      PROCESSING: 'processing',
      CANCELLED: 'error',
      FAILED: 'error',
      REFUNDED: 'default',
      TIMEOUT: 'default',
    }
    return colorMap[status] || 'default'
  }

  const getMethodColor = (method: string) => {
    const colorMap: Record<string, string> = {
      ALIPAY: '#1677ff',
      WECHAT: '#07c160',
      CARD: '#722ed1',
      BALANCE: '#fa8c16',
    }
    return colorMap[method] || 'default'
  }

  const columns = [
    {
      title: '支付单号',
      dataIndex: 'paymentNo',
      key: 'paymentNo',
      width: 200,
    },
    {
      title: '关联订单',
      dataIndex: 'orderNo',
      key: 'orderNo',
      width: 180,
    },
    {
      title: '金额',
      dataIndex: 'amount',
      key: 'amount',
      width: 120,
      render: (val: number, record: Payment) => `${record.currency} ${val?.toFixed(2)}`,
    },
    {
      title: '支付方式',
      dataIndex: 'paymentMethod',
      key: 'paymentMethod',
      width: 120,
      render: (method: string) => <Tag color={getMethodColor(method)}>{method}</Tag>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status: string) => <Tag color={getStatusColor(status)}>{status}</Tag>,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 180,
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_: unknown, record: Payment) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleViewDetail(record.paymentNo)}
          >
            详情
          </Button>
          {(record.status === 'PENDING' || record.status === 'PROCESSING') && (
            <Button
              type="link"
              size="small"
              danger
              icon={<StopOutlined />}
              onClick={() => handleCancel(record.paymentNo)}
            >
              取消
            </Button>
          )}
          {record.status === 'SUCCESS' && (
            <Button
              type="link"
              size="small"
              icon={<RollbackOutlined />}
              onClick={() => handleRefund(record)}
            >
              退款
            </Button>
          )}
        </Space>
      ),
    },
  ]

  return (
    <div>
      <Title level={3} style={{ marginBottom: 24 }}>
        支付管理
      </Title>

      <Card style={{ marginBottom: 16 }}>
        <Space>
          <Search
            placeholder="输入用户ID查询"
            allowClear
            enterButton={<SearchOutlined />}
            onSearch={handleSearch}
            style={{ width: 250 }}
          />
          <Button icon={<ReloadOutlined />} onClick={loadData}>
            刷新
          </Button>
        </Space>
      </Card>

      <Card>
        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="paymentNo"
          loading={loading}
          pagination={{
            current,
            pageSize,
            total,
            showSizeChanger: true,
            showTotal: (t) => `共 ${t} 条`,
            onChange: (page, size) => {
              setCurrent(page)
              setPageSize(size)
            },
          }}
        />
      </Card>

      {/* Payment Detail Modal */}
      <Modal
        title="支付详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={700}
      >
        {selectedPayment && (
          <Descriptions column={2} bordered>
            <Descriptions.Item label="支付单号" span={2}>{selectedPayment.paymentNo}</Descriptions.Item>
            <Descriptions.Item label="关联订单">{selectedPayment.orderNo}</Descriptions.Item>
            <Descriptions.Item label="用户ID">{selectedPayment.userId}</Descriptions.Item>
            <Descriptions.Item label="金额">
              {selectedPayment.currency} {selectedPayment.amount?.toFixed(2)}
            </Descriptions.Item>
            <Descriptions.Item label="支付方式">
              <Tag color={getMethodColor(selectedPayment.paymentMethod)}>
                {selectedPayment.paymentMethod}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="状态" span={2}>
              <Tag color={getStatusColor(selectedPayment.status)}>{selectedPayment.status}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="交易号">{selectedPayment.transactionId || '-'}</Descriptions.Item>
            <Descriptions.Item label="渠道">{selectedPayment.channelCode || '-'}</Descriptions.Item>
            <Descriptions.Item label="主题" span={2}>{selectedPayment.subject}</Descriptions.Item>
            <Descriptions.Item label="描述" span={2}>{selectedPayment.body}</Descriptions.Item>
            <Descriptions.Item label="支付时间">{selectedPayment.payTime || '-'}</Descriptions.Item>
            <Descriptions.Item label="过期时间">{selectedPayment.expireTime || '-'}</Descriptions.Item>
            {selectedPayment.errorCode && (
              <>
                <Descriptions.Item label="错误码">{selectedPayment.errorCode}</Descriptions.Item>
                <Descriptions.Item label="错误信息">{selectedPayment.errorMsg}</Descriptions.Item>
              </>
            )}
            <Descriptions.Item label="重试次数">{selectedPayment.retryCount}</Descriptions.Item>
            <Descriptions.Item label="创建时间">{selectedPayment.createTime}</Descriptions.Item>
            <Descriptions.Item label="更新时间" span={2}>{selectedPayment.updateTime}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>

      {/* Refund Modal */}
      <Modal
        title="申请退款"
        open={refundVisible}
        onOk={handleRefundSubmit}
        onCancel={() => setRefundVisible(false)}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="refundAmount"
            label="退款金额"
            rules={[{ required: true, message: '请输入退款金额' }]}
          >
            <Input type="number" prefix={selectedPayment?.currency} />
          </Form.Item>
          <Form.Item
            name="reason"
            label="退款原因"
            rules={[{ required: true, message: '请输入退款原因' }]}
          >
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
