package com.example.todoapppractice.ui.util

/**
 * Presentation-layer utility for formatting monetary amounts.
 * Keeps formatting logic out of domain models (which expose raw paise values).
 */
object CurrencyFormatter {

    /**
     * Format a net balance in paise as a signed string.
     * e.g. 70332L → "+703.32", -35166L → "-351.66", 0L → "+0.00"
     */
    fun formatBalance(balancePaise: Long): String {
        val rupees = balancePaise / 100.0
        val sign = if (rupees >= 0) "+" else ""
        return "$sign%.2f".format(rupees)
    }

    /**
     * Format an absolute amount in paise (always positive, no sign).
     * e.g. 5000L → "50.00"
     */
    fun formatAmount(amountPaise: Long): String {
        return "%.2f".format(amountPaise / 100.0)
    }
}
