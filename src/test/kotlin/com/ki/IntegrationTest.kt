package com.ki

import com.ki.LocalRunner.simulatePlatform
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal

class IntegrationTest {
    @Test
    fun testSimulatePlatform() {
        val fixturePath = Fixture.getPath("card_payments_mixed.csv")
        val expected = """
            "customer_id","shares"
            "123","735"
            "456","3430"
            
            """.trimIndent()
        val result = simulatePlatform(fixturePath, "card", BigDecimal("1.2"))
        Assert.assertEquals(expected, result)
    }
}