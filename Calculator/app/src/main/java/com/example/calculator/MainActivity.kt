package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // Reference to the TextView that displays the result or input
    private var tvInput: TextView? = null

    // Flags to check if the last pressed button was a number or a dot
    private var lastNumeric: Boolean = false
    private var lastDot: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Make sure layout respects system bars (for modern full-screen design)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Link TextView by its ID
        tvInput = findViewById(R.id.tvInput)
    }

    // Called when a digit button (0–9) is pressed
    fun onDigit(view: View) {
        tvInput?.append((view as Button).text)
        lastNumeric = true
        lastDot = false
    }

    // Clears all input from the screen
    fun onClear(view: View) {
        tvInput?.text = ""
        lastNumeric = false
        lastDot = false
    }

    // Called when the decimal point button is pressed
    fun onDecimalPoint(view: View) {
        // Only allow a dot if the last character was numeric and there’s no existing dot
        if (lastNumeric && !lastDot) {
            tvInput?.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    // Checks if an operator (+, -, *, /) already exists in the expression
    private fun isOperatorAdded(value: String): Boolean {
        val startIndex = if (value.startsWith("-")) 1 else 0
        val sub = value.substring(startIndex)
        return sub.contains("/") || sub.contains("*") || sub.contains("+") || sub.contains("-")
    }

    // Called when an operator button is pressed (+, -, *, /)
    fun onOperator(view: View) {
        tvInput?.text?.let {
            val text = it.toString()
            val operator = (view as Button).text.toString()

            // Allow "-" if it's the very first character (for negative numbers)
            if (text.isEmpty() && operator == "-") {
                tvInput?.append(operator)
            }
            // Otherwise, only add operator if last input was numeric and there's no operator yet
            else if (lastNumeric && !isOperatorAdded(text)) {
                tvInput?.append(operator)
                lastNumeric = false
                lastDot = false
            }
        }
    }


    // Called when the equal button is pressed (=)
    fun onEqual(view: View) {
        val tvValue = tvInput?.text.toString()
        val operation = getOperation(tvValue)
        getOperationResult(tvValue, operation)
    }

    // Finds which operation exists in the input (+, -, *, /)
    fun getOperation(stValue: String): String {
        val operations = listOf("+", "-", "/", "*")
        for (operation in operations) {
            if (stValue.contains(operation)) {
                return operation // stop at the first operator found
            }
        }
        return ""
    }

    // Performs the actual mathematical operation
    fun result(one: Double, two: Double, operation: String): String {
        return when (operation) {
            "+" -> (one + two).toString()
            "-" -> (one - two).toString()
            "*" -> (one * two).toString()
            "/" -> if (two == 0.0) "Error" else (one / two).toString() // handle division by zero
            else -> ""
        }
    }

    // Removes ".0" from integer results for cleaner display
    fun formatResult(value: String): String {
        return if (value.endsWith(".0")) value.dropLast(2) else value
    }

    // Splits the expression into two operands and shows the result
    fun getOperationResult(tvValue: String, operation: String) {
        var value = tvValue
        if (lastNumeric && operation.isNotEmpty()) {
            var prefix = ""
            try {
                // Handle negative numbers at the start (like -5+3)
                if (value.startsWith("-")) {
                    prefix = "-"
                    value = value.substring(1)
                }

                // Split based on the operator
                if (value.contains(operation)) {
                    val splitValue = value.split(operation)
                    var one = splitValue[0]
                    var two = splitValue[1]

                    // Add back the negative sign if needed
                    if (prefix.isNotEmpty()) {
                        one = prefix + one
                    }

                    val resultValue = result(one.toDouble(), two.toDouble(), operation)
                    tvInput?.text = formatResult(resultValue)
                }
            } catch (e: ArithmeticException) {
                e.printStackTrace()
                tvInput?.text = "Error"
            } catch (e: Exception) {
                e.printStackTrace()
                tvInput?.text = "Error"
            }
        }
    }
}
