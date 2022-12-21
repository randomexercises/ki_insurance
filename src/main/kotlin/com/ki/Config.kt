package com.ki

import java.math.BigDecimal

object Config {
    val paymentFeeRate: BigDecimal
        get() = BigDecimal("0.02")
}