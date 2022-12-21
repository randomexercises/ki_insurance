package com.ki.services

import com.ki.models.Payment
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal

class ShareEngineTest {
    @Test
    fun testGenerateShareOrdersDifferentCustomers() {
        val shareEngine = ShareEngine()
        val payments = arrayOf<Payment>(
            createPayment(456, 900),
            createPayment(123, 4200)
        )
        val result = shareEngine.generateShareOrders(BigDecimal("1.2"), payments)
        Assert.assertEquals(2, result.size.toLong())
        Assert.assertEquals(123, result[0].customerId)
        Assert.assertEquals(3500, result[0].shares)
        Assert.assertEquals(456, result[1].customerId)
        Assert.assertEquals(750, result[1].shares)
    }

    @Test
    fun testGenerateShareOrdersSameCustomer() {
        val shareEngine = ShareEngine()
        val customerId = 456
        val payments = arrayOf<Payment>(
            createPayment(customerId, 900),
            createPayment(customerId, 4200)
        )
        val result = shareEngine.generateShareOrders(BigDecimal("1.2"), payments)
        Assert.assertEquals(1, result.size.toLong())
        Assert.assertEquals(456, result[0].customerId)
        Assert.assertEquals(4250, result[0].shares)
    }

    private fun createPayment(customerId: Int, amount: Int): Payment {
        val payment = Payment()
        payment.customerId = customerId
        payment.amount = amount
        return payment
    }
}