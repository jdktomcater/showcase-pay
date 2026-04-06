import { Routes, Route, Navigate } from 'react-router-dom'
import MainLayout from './components/MainLayout'
import Dashboard from './pages/dashboard'
import Orders from './pages/orders'
import Payments from './pages/payments'
import ServiceHealth from './pages/health'

function App() {
  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="orders" element={<Orders />} />
        <Route path="payments" element={<Payments />} />
        <Route path="health" element={<ServiceHealth />} />
      </Route>
    </Routes>
  )
}

export default App
