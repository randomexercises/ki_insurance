@file:JvmName("Main")

package com.ki

import com.ki.models.payment.PaymentTypes
import com.ki.services.PaymentProcessor
import com.ki.services.ShareEngine
import com.opencsv.CSVWriter
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.helper.HelpScreenException
import net.sourceforge.argparse4j.inf.ArgumentParserException
import java.io.IOException
import java.io.StringWriter
import java.math.BigDecimal
import java.util.*
import kotlin.system.exitProcess

object LocalRunner {

    fun simulatePlatform(csvPath: String, source: String, sharePrice: BigDecimal): String {
        val fieldNames = arrayOf(
            "customer_id",
            "shares"
        )
        val paymentProcessor = PaymentProcessor()
        // Extract CSV To Payment object
        val payments = paymentProcessor.getPayments(csvPath, source)
        // Filter only payments that have "processed" as status
        val filtered = paymentProcessor.verifyPayments(payments)
        val shareEngine = ShareEngine()
        val shareOrders = shareEngine.generateShareOrders(sharePrice, filtered)
        val data: MutableList<Array<String>> = ArrayList()
        for (shareOrder in shareOrders) {
            data.add(arrayOf(shareOrder.customerId.toString(), shareOrder.shares.toString()))
        }

        return generateCsv(fieldNames, data)
    }

    private fun generateCsv(fieldNames: Array<String>, data: List<Array<String>>): String {
        val output = StringWriter()
        val writer = CSVWriter(output)
        writer.writeNext(fieldNames)
        writer.writeAll(data)
        try {
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return output.toString()
    }
}

fun main(args: Array<String>) {
    val parser = ArgumentParsers.newFor("LocalRunner").build()
    parser.addArgument("csv_path")
        .help("Path to the payments CSV file")
    parser.addArgument("source").choices(
        PaymentTypes.values()
        .map { it.name.lowercase(Locale.getDefault()) })
        .help("The source of the payment")
    parser.addArgument("share_price")
        .type(BigDecimal::class.java)
        .help("Share price to generate share orders for e.g. '1.30'")

    parser.runCatching {
        parseArgs(args)
    }.onFailure { ex ->
        when (ex) {
            is HelpScreenException -> exitProcess(0)
            is ArgumentParserException -> parser.handleError(ex)
            else -> throw ex    // Do not handle other exceptions, same behaviour as the previous implementation
        }
        exitProcess(1)
    }.onSuccess { arguments ->
        val csvPath = arguments.getString("csv_path")
        val source = arguments.getString("source")

        check(source != null) { "Source not found" }

        val sharedPrice = BigDecimal(arguments.getString("share_price"))

        // This will fail as before, with a unhandled exception
        LocalRunner.simulatePlatform(
            csvPath, source, sharedPrice
        ).also(::print)
    }
}