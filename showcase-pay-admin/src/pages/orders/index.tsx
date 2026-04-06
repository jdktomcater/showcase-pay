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
} from 'antd'
import { SearchOutlined, EyeOutlined, StopOutlined, ReloadOutlined } from '@ant-design/icons'
import { orderApi } from '../../services/api'
import type { Order } from '../../types'

const { Title } = Typography
const { Search } = Input

export default function Orders() {
  const [dataSource, setDataSource] = useState<Order[]>([])
  const [loading, setLoading] = useState(false)
  const [total, setTotal] = useState(0)
  const [current, setCurrent] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [userId, setUserId] = useState<number>(1001)
  const [detailVisible, setDetailVisible] = useState(false)
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null)

  const loadData = useCallback(async () => {
    setLoading(true)
    try {
      const res = await orderApi.getList(userId, current, pageSize)
      const data = (res as any).data
      setDataSource(data?.records || [])
      setTotal(data?.total || 0)
    } catch (error) {
      message.error('加载订单列表失败')
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

  const handleViewDetail = async (orderNo: string) => {
    try {
      const res = await orderApi.getDetail(orderNo)
      setSelectedOrder((res as any).data)
      setDetailVisible(true)
    } catch (error) {
      message.error('获取订单详情失败')
    }
  }

  const handleCancel = async (orderNo: string) => {
    Modal.confirm({
      title: '确认取消',
      content: '确定要取消此订单吗？',
      onOk: async () => {
        try {
          await orderApi.cancel(orderNo)
          message.success('订单已取消')
          loadData()
        } catch (error) {
          message.error('取消订单失败')
        }
      },
    })
  }

  const getStatusColor = (status: string) => {
    const colorMap: Record<string, string> = {
      PAID: 'success',
      CREATED: 'processing',
      PENDING_PAYMENT: 'default',
      CANCELLED: 'error',
      REFUNDED: 'default',
      COMPLETED: 'success',
    }
    return colorMap[status] || 'default'
  }

  const columns = [
    {
      title: '订单号',
      dataIndex: 'orderNo',
      key: 'orderNo',
      width: 200,
    },
    {
      title: '标题',
      dataIndex: 'subject',
      key: 'subject',
    },
    {
      title: '金额',
      dataIndex: 'amount',
      key: 'amount',
      width: 120,
      render: (val: number, record: Order) => `${record.currency} ${val?.toFixed(2)}`,
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
      width: 150,
      render: (_: unknown, record: Order) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleViewDetail(record.orderNo)}
          >
            详情
          </Button>
          {record.status === 'CREATED' && (
            <Button
              type="link"
              size="small"
              danger
              icon={<StopOutlined />}
              onClick={() => handleCancel(record.orderNo)}
            >
              取消
            </Button>
          )}
        </Space>
      ),
    },
  ]

  return (
    <div>
      <Title level={3} style={{ marginBottom: 24 }}>
        订单管理
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
          rowKey="orderNo"
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

      <Modal
        title="订单详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={600}
      >
        {selectedOrder && (
          <Descriptions column={1} bordered>
            <Descriptions.Item label="订单号">{selectedOrder.orderNo}</Descriptions.Item>
            <Descriptions.Item label="用户ID">{selectedOrder.userId}</Descriptions.Item>
            <Descriptions.Item label="标题">{selectedOrder.subject}</Descriptions.Item>
            <Descriptions.Item label="描述">{selectedOrder.description}</Descriptions.Item>
            <Descriptions.Item label="金额">
              {selectedOrder.currency} {selectedOrder.amount?.toFixed(2)}
            </Descriptions.Item>
            <Descriptions.Item label="状态">
              <Tag color={getStatusColor(selectedOrder.status)}>{selectedOrder.status}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="支付时间">{selectedOrder.payTime || '-'}</Descriptions.Item>
            <Descriptions.Item label="过期时间">{selectedOrder.expireTime || '-'}</Descriptions.Item>
            <Descriptions.Item label="创建时间">{selectedOrder.createTime}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  )
}
