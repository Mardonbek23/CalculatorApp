package uz.freelance.calculatorapp

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import uz.freelance.calculatorapp.databinding.ActivityMainBinding
import uz.freelance.calculatorapp.helpers.E
import uz.freelance.calculatorapp.helpers.LocalData
import uz.freelance.calculatorapp.helpers.PI
import uz.freelance.calculatorapp.helpers.isComplexOperation
import uz.freelance.calculatorapp.helpers.isNumber
import uz.freelance.calculatorapp.helpers.typeToken
import uz.freelance.calculatorapp.helpers.valueOfItem
import uz.freelance.calculatorapp.models.Expression
import uz.freelance.calculatorapp.models.HistoryItem
import uz.freelance.calculatorapp.models.Item
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tan

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var expresionsList = ArrayList<Item>()
    private var processList = ArrayList<Item>()
    lateinit var localData: LocalData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)
        setButtons()
        localData = LocalData(this)

    }

    private fun setButtons() {
        binding.apply {
            cvHistory.setOnClickListener {
                val intent = Intent(this@MainActivity, HistoryActivity::class.java)
                startActivity(intent)
            }
            cv0.setOnClickListener {
                buttonClicked("0", Expression.NUMBER)
            }
            cv1.setOnClickListener { buttonClicked("1", Expression.NUMBER) }
            cv2.setOnClickListener { buttonClicked("2", Expression.NUMBER) }
            cv3.setOnClickListener { buttonClicked("3", Expression.NUMBER) }
            cv4.setOnClickListener { buttonClicked("4", Expression.NUMBER) }
            cv5.setOnClickListener { buttonClicked("5", Expression.NUMBER) }
            cv6.setOnClickListener { buttonClicked("6", Expression.NUMBER) }
            cv7.setOnClickListener { buttonClicked("7", Expression.NUMBER) }
            cv8.setOnClickListener { buttonClicked("8", Expression.NUMBER) }
            cv9.setOnClickListener { buttonClicked("9", Expression.NUMBER) }
            cvDot.setOnClickListener { buttonClicked(".", Expression.DOT) }
            cvPlus.setOnClickListener { buttonClicked("+", Expression.PLUS) }
            cvMinus.setOnClickListener { buttonClicked("-", Expression.MINUS) }
            cvMultiply.setOnClickListener { buttonClicked("×", Expression.MULTIPLY) }
            cvDivide.setOnClickListener { buttonClicked("÷", Expression.DIVIDE) }
            cvPercentage.setOnClickListener { buttonClicked("%", Expression.PERCENTAGE) }
            cvOpenParenthesis?.setOnClickListener {
                buttonClicked(
                    "(",
                    Expression.OPEN_PARENTHESIS
                )
            }
            cvCloseParenthesis?.setOnClickListener {
                buttonClicked(
                    ")",
                    Expression.CLOSE_PARENTHESIS
                )
            }
            cvSqrt?.setOnClickListener { buttonClicked("√(", Expression.SQRT) }
            cvSin?.setOnClickListener { buttonClicked("sin(", Expression.SIN) }
            cvCos?.setOnClickListener { buttonClicked("cos(", Expression.COS) }
            cvTan?.setOnClickListener { buttonClicked("tan(", Expression.TAN) }
            cvLn?.setOnClickListener { buttonClicked("ln(", Expression.LN) }
            cvLog?.setOnClickListener { buttonClicked("log(", Expression.LOG) }
            cvDivideToX?.setOnClickListener { buttonClicked("^(-1)", Expression.DIV1_TO_X) }
            cvPowE?.setOnClickListener { buttonClicked("e^(", Expression.E_IN_DEGREE_X) }
            cvPowSquare?.setOnClickListener {
                buttonClicked("^(2)", Expression.SQUARE)
            }
            cvXPowY?.setOnClickListener { buttonClicked("^(", Expression.IN_DEGREE_X) }
            cvAbs?.setOnClickListener { buttonClicked("abs(", Expression.ABS) }
            cvPi?.setOnClickListener { buttonClicked("π", Expression.PI) }
            cvE?.setOnClickListener { buttonClicked("e", Expression.E) }
            cvRotate.setOnClickListener {
                rotateScreen()
            }
            cvClearAll.setOnClickListener {
                tvProcess.text = "0"
                tvResult.text = "=0"
                expresionsList.clear()
                binding.tvResult.visibility = View.GONE
            }
            cvEquality.setOnClickListener {
                calculate(true)
                val fromJson =
                    Gson().fromJson(localData.history(), typeToken<ArrayList<HistoryItem>?>())
                        ?: ArrayList()
                fromJson.add(HistoryItem(tvProcess.text.toString(), tvResult.text.toString()))
                localData.history(Gson().toJson(fromJson, typeToken<ArrayList<HistoryItem>>()))
            }
            cvClearOne.setOnClickListener {
                clearOne()
                binding.tvResult.visibility = View.GONE
            }
        }
    }

    private fun buttonClicked(string: String, expression: Expression) {
        binding.apply {
            if (expresionsList.isEmpty()) tvProcess.text = ""
            sortingFunction(string, expression)
            var value = ""
            expresionsList.map { it.name }.forEach { value += it }
            tvProcess.text = value
            tvResult.visibility = View.GONE
        }
    }

    private fun sortingFunction(string: String, expression: Expression) {
        val last = expresionsList.lastOrNull()
        when (expression) {
            Expression.NUMBER -> {
                if (last != null) {
                    if (last.expression == Expression.NUMBER) {
                        if (expresionsList[expresionsList.lastIndex].name.length < 15) {
                            expresionsList[expresionsList.lastIndex].name = last.name + string
                            expresionsList[expresionsList.lastIndex].value =
                                (last.value.toString() + string).toLong()
                        }
                    } else if (last.expression == Expression.DOUBLE) {
                        val indexOf = expresionsList[expresionsList.lastIndex].name.indexOf(".")
                        if (expresionsList[expresionsList.lastIndex].name.length - indexOf < 14) {
                            expresionsList[expresionsList.lastIndex].name = last.name + string
                            expresionsList[expresionsList.lastIndex].value =
                                (last.value.toString() + string).toDouble()
                        }
                    } else if (last.expression == Expression.STRANGE_NUMBER_DOT) {
                        expresionsList[expresionsList.lastIndex].name = last.name + string
                        expresionsList[expresionsList.lastIndex].value =
                            (last.value.toString() + string).toDouble()
                        expresionsList[expresionsList.lastIndex].expression = Expression.DOUBLE
                    } else expresionsList.add(Item(string, string.toLong(), Expression.NUMBER))
                } else expresionsList.add(Item(string, string.toLong(), Expression.NUMBER))
            }

            Expression.DOT -> {
                if (last != null) {
                    if (last.expression == Expression.NUMBER) {
                        expresionsList[expresionsList.lastIndex].name = last.name + "."
                        expresionsList[expresionsList.lastIndex].value = last.value.toString() + "."
                        expresionsList[expresionsList.lastIndex].expression =
                            Expression.STRANGE_NUMBER_DOT
                    } else if (last.expression == Expression.DOT || last.expression == Expression.STRANGE_NUMBER_DOT) {
                    } else expresionsList.add(Item(".", ".", Expression.DOT))
                } else {
                    expresionsList.add(Item("0.", "0.", Expression.STRANGE_NUMBER_DOT))
                }
            }

            Expression.PLUS -> {
                if (last != null) {
                    if (last.expression == Expression.PLUS) {
                    } else if (last.expression == Expression.MINUS || last.expression == Expression.DIVIDE || last.expression == Expression.MULTIPLY) {
                        last.name = string
                        last.value = string
                        last.expression = Expression.PLUS
                        expresionsList[expresionsList.lastIndex] = last
                    } else expresionsList.add(Item(string, string, expression))
                }
            }

            Expression.MINUS -> {
                if (last != null) {
                    if (last.expression == Expression.MINUS) {
                    } else if (last.expression == Expression.PLUS || last.expression == Expression.DIVIDE || last.expression == Expression.MULTIPLY) {
                        last.name = string
                        last.value = string
                        last.expression = Expression.MINUS
                        expresionsList[expresionsList.lastIndex] = last
                    } else expresionsList.add(Item(string, string, expression))
                } else expresionsList.add(Item(string, string, expression))
            }

            Expression.MULTIPLY -> {
                if (last != null) {
                    if (last.expression == Expression.MULTIPLY) {
                    } else if (last.expression == Expression.PLUS || last.expression == Expression.DIVIDE || last.expression == Expression.MINUS) {
                        last.name = string
                        last.value = string
                        last.expression = Expression.MULTIPLY
                        expresionsList[expresionsList.lastIndex] = last
                    } else expresionsList.add(Item(string, string, expression))
                }
            }

            Expression.DIVIDE -> {
                if (last != null) {
                    if (last.expression == Expression.DIVIDE) {
                    } else if (last.expression == Expression.PLUS || last.expression == Expression.MULTIPLY || last.expression == Expression.MINUS) {
                        last.name = string
                        last.value = string
                        last.expression = Expression.DIVIDE
                        expresionsList[expresionsList.lastIndex] = last
                    } else expresionsList.add(Item(string, string, expression))
                }
            }

            Expression.PERCENTAGE -> {
                if (last != null) {
                    if (last.expression == Expression.NUMBER) {
                        if (last.value.toString().endsWith("00")) {
                            expresionsList[expresionsList.lastIndex].value =
                                last.value.toString().toLong() / 100
                            expresionsList[expresionsList.lastIndex].name =
                                expresionsList[expresionsList.lastIndex].value.toString()
                        } else {
                            expresionsList[expresionsList.lastIndex].value =
                                last.value.toString().toDouble() / 100
                            expresionsList[expresionsList.lastIndex].expression = Expression.DOUBLE
                            expresionsList[expresionsList.lastIndex].name =
                                expresionsList[expresionsList.lastIndex].value.toString()
                        }
                        calculate(true)
                    } else if (last.expression == Expression.DOUBLE) {
                        expresionsList[expresionsList.lastIndex].value =
                            last.value.toString().toDouble() / 100
                        expresionsList[expresionsList.lastIndex].name =
                            expresionsList[expresionsList.lastIndex].value.toString()
                        calculate(true)
                    }
                }
            }

            Expression.SQUARE -> {
                expresionsList.add(Item("^(", "^(,", Expression.IN_DEGREE_X))
                expresionsList.add(Item("2", 2, Expression.NUMBER))
                expresionsList.add(Item(")", ")", Expression.CLOSE_PARENTHESIS))
            }

            else -> {
                expresionsList.add(Item(string, string, expression))
            }
        }
    }

    private fun clearOne() {
        val lastOrNull = expresionsList.lastOrNull()
        if (lastOrNull != null) {
            if (lastOrNull.expression == Expression.STRANGE_NUMBER_DOT) {
                expresionsList[expresionsList.lastIndex].name =
                    lastOrNull.name.substring(0, lastOrNull.name.length - 1)
                expresionsList[expresionsList.lastIndex].value =
                    lastOrNull.name.substring(0, lastOrNull.name.length - 1).toLong()
                expresionsList[expresionsList.lastIndex].expression = Expression.NUMBER
            } else if (lastOrNull.expression == Expression.DOUBLE) {
                expresionsList[expresionsList.lastIndex].name =
                    lastOrNull.name.substring(0, lastOrNull.name.length - 1)
                if (expresionsList[expresionsList.lastIndex].name.endsWith('.')) {
                    expresionsList[expresionsList.lastIndex].value =
                        expresionsList[expresionsList.lastIndex].name
                    expresionsList[expresionsList.lastIndex].expression =
                        Expression.STRANGE_NUMBER_DOT
                } else {
                    expresionsList[expresionsList.lastIndex].value =
                        expresionsList[expresionsList.lastIndex].name.toDouble()
                }
            } else if (lastOrNull.expression == Expression.NUMBER && lastOrNull.name.length > 1) {
                expresionsList[expresionsList.lastIndex].name =
                    lastOrNull.name.substring(0, lastOrNull.name.length - 1)
                expresionsList[expresionsList.lastIndex].value = lastOrNull.name.toLong()
            } else {
                expresionsList.removeLast()
            }
            var value = ""
            expresionsList.map { it.name }.forEach { value += it }
            binding.tvProcess.text = if (value == "") "0" else value
            binding.tvResult.text = "=${expresionsList.map { it.value }}"
        }
    }

    private fun calculate(withResult: Boolean = false) {
        try {
            if (expresionsList.any { it.expression == Expression.DOT || it.expression == Expression.STRANGE_NUMBER_DOT }) {
                binding.tvResult.text = "=Error"
                binding.tvResult.visibility = View.VISIBLE
                return
            }
            processList = ArrayList()
            processList.addAll(expresionsList)
            createNegativeNumbers()
            var result: String = calculatingProcess().toString()
            if (result.endsWith(".0")) result = result.replace(".0", "")
            binding.tvResult.text = if (result.startsWith("=")) result else "=$result"
            binding.tvResult.visibility = View.VISIBLE

        } catch (e: Exception) {
            if (withResult) {
                binding.tvResult.text = "=Error ${e.message}1"
            }
            binding.tvResult.visibility = View.VISIBLE
            e.printStackTrace()
        }
    }

    private fun createNegativeNumbers() {
        for (i in processList.indices) {
            if (processList[i].expression == Expression.MINUS) {
                if (i + 1 <= processList.lastIndex && (processList[i + 1].expression == Expression.NUMBER || processList[i + 1].expression == Expression.DOUBLE)) {
                    if (i == 0) {
                        if (processList[i + 1].expression == Expression.NUMBER) {
                            processList[i + 1].value =
                                processList[i + 1].value.toString().toLong() * (-1)
                            processList[i + 1].name = processList[i + 1].value.toString()
                            processList.removeAt(i)
                            return createNegativeNumbers()
                        } else if (processList[i + 1].expression == Expression.DOUBLE) {
                            processList[i + 1].value =
                                processList[i + 1].value.toString().toDouble() * (-1)
                            processList[i + 1].name = processList[i + 1].value.toString()
                            processList.removeAt(i)
                            return createNegativeNumbers()
                        }
                        Log.d("loooooooog", "create negative: ${processList.map { it.value }}")
                    } else {
                        if (processList[i - 1].expression != Expression.DOUBLE && processList[i - 1].expression != Expression.NUMBER && processList[i - 1].expression != Expression.PI && processList[i - 1].expression != Expression.E) {
                            if (processList[i + 1].expression == Expression.NUMBER) {
                                processList[i + 1].value =
                                    processList[i + 1].value.toString().toLong() * (-1)
                                processList[i + 1].name = processList[i + 1].value.toString()
                                processList.removeAt(i)
                                return createNegativeNumbers()
                            } else if (processList[i + 1].expression == Expression.DOUBLE) {
                                processList[i + 1].value =
                                    processList[i + 1].value.toString().toDouble() * (-1)
                                processList[i + 1].name = processList[i + 1].value.toString()
                                processList.removeAt(i)
                                return createNegativeNumbers()
                            }
                            Log.d("loooooooog", "create negative: ${processList.map { it.value }}")
                        }
                    }
                }
            }
        }
    }

    private fun calculatingProcess(): Any {
        val lastOrNull = processList.lastOrNull()
        if (lastOrNull != null && (lastOrNull.expression == Expression.PLUS || lastOrNull.expression == Expression.MINUS || lastOrNull.expression == Expression.MULTIPLY || lastOrNull.expression == Expression.DIVIDE)) {
            processList.removeLast()
        }
        if (processList.size > 2) {
            val findDivideX =
                processList.find { it.expression == Expression.DIV1_TO_X }
            val findPowX =
                processList.find { it.expression == Expression.IN_DEGREE_X }
            val findComplexOperation =
                processList.find { it.expression == Expression.SIN || it.expression == Expression.COS || it.expression == Expression.TAN || it.expression == Expression.LN || it.expression == Expression.LOG || it.expression == Expression.IN_DEGREE_X || it.expression == Expression.OPEN_PARENTHESIS || it.expression == Expression.E_IN_DEGREE_X || it.expression == Expression.DIV1_TO_X || it.expression == Expression.SQRT || it.expression == Expression.ABS }
            val findMultiplyDivide =
                processList.find { it.expression == Expression.MULTIPLY || it.expression == Expression.DIVIDE }
            val findPlusMinus =
                processList.find { it.expression == Expression.PLUS || it.expression == Expression.MINUS }
            val indexOfDivideX = processList.indexOf(findDivideX)
            val indexOfPowX = processList.indexOf(findPowX)
            if (indexOfDivideX == 0 || indexOfPowX == 0 || indexOfPowX == processList.lastIndex || (indexOfPowX + 1) == processList.lastIndex) return "=Error"
            if (findDivideX != null && (processList[indexOfDivideX - 1].expression == Expression.NUMBER || processList[indexOfDivideX - 1].expression == Expression.DOUBLE || processList[indexOfDivideX - 1].expression == Expression.PI || processList[indexOfDivideX - 1].expression == Expression.E)) {
                processList[indexOfDivideX - 1].value =
                    1 / valueOfItem(processList[indexOfDivideX - 1])
                processList[indexOfDivideX - 1].name =
                    processList[indexOfDivideX - 1].value.toString()
                processList[indexOfDivideX - 1].expression = Expression.DOUBLE
                processList.removeAt(indexOfDivideX)
                return calculatingProcess()
            } else if (findPowX != null && (processList[indexOfPowX - 1].expression == Expression.NUMBER || processList[indexOfPowX - 1].expression == Expression.DOUBLE || processList[indexOfPowX - 1].expression == Expression.E || processList[indexOfPowX - 1].expression == Expression.PI) &&
                (processList[indexOfPowX + 1].expression == Expression.DOUBLE || processList[indexOfPowX + 1].expression == Expression.NUMBER || processList[indexOfPowX + 1].expression == Expression.PI || processList[indexOfPowX + 1].expression == Expression.E) &&
                processList[indexOfPowX + 2].expression == Expression.CLOSE_PARENTHESIS
            ) {
                val pow = valueOfItem(processList[indexOfPowX - 1])
                    .pow(valueOfItem(processList[indexOfPowX + 1]))
                findPowX.name = pow.toString()
                findPowX.value = pow
                findPowX.expression = Expression.DOUBLE
                processList[indexOfPowX] = findPowX
                processList.removeAt(indexOfPowX + 1)
                processList.removeAt(indexOfPowX + 1)
                processList.removeAt(indexOfPowX - 1)
                return calculatingProcess()
            } else if (findComplexOperation != null) {
                val indexOf = processList.indexOf(findComplexOperation)
                if (indexOf == processList.lastIndex ||
                    processList[indexOf + 1].expression == Expression.CLOSE_PARENTHESIS
                ) return "=Error"
                Log.d("loooooooog", "entered in complex operation process: ")
                val itemAfter = processList[indexOf + 1]
                if ((itemAfter.expression == Expression.PI || itemAfter.expression == Expression.E || itemAfter.expression == Expression.DOUBLE || itemAfter.expression == Expression.NUMBER)
                    && ((indexOf + 1) == processList.lastIndex || (indexOf + 2 <= processList.lastIndex && processList[indexOf + 2].expression == Expression.CLOSE_PARENTHESIS))
                ) {
                    Log.d(
                        "loooooooog",
                        "calculatingProcess op: ${findComplexOperation.expression}--${processList[indexOf].expression}"
                    )
                    Log.d("loooooooog", "calculatingProcessX: ${processList.map { it.value }}")
                    val complexOperationResult = complexOperation(
                        valueOfItem(processList[indexOf + 1]),
                        findComplexOperation.expression
                    )
                    processList[indexOf].value = complexOperationResult
                    processList[indexOf].name = complexOperationResult.toString()
                    processList[indexOf].expression = Expression.DOUBLE
                    processList.removeAt(indexOf + 1)
                    if (processList[indexOf + 1].expression == Expression.CLOSE_PARENTHESIS) processList.removeAt(
                        indexOf + 1
                    )
                    createNegativeNumbers()
                    return calculatingProcess()
                } else {
                    Log.d("loooooooog", "calculatingProcessX: ${processList.map { it.value }}")
                    calculateInsideParenthesis() ?: return "=Error"
                    return calculatingProcess()
                }
            } else if (findMultiplyDivide != null) {
                val indexOf = processList.indexOf(findMultiplyDivide)
                if (indexOf == 0 || indexOf == processList.lastIndex) return "=Error"
                val itemBefore = processList[indexOf - 1]
                val itemAfter = processList[indexOf + 1]
                if (itemBefore.expression == Expression.NUMBER && itemAfter.expression == Expression.NUMBER) {
                    val toLong = plusMinusMultiplyDivide(
                        itemBefore.value.toString().toLong(),
                        itemAfter.value.toString().toLong(),
                        findMultiplyDivide.expression
                    )
                    processList[indexOf].value = toLong
                    if (toLong is Long) {
                        processList[indexOf].expression = Expression.NUMBER
                    } else {
                        processList[indexOf].expression = Expression.DOUBLE
                    }
                    processList[indexOf].name = toLong.toString()

                    processList.removeAt(indexOf - 1)
                    processList.removeAt(indexOf)
                    Log.d("loooooooog", "calculatingProcess:1 ${processList.map { it.value }}")
                    return calculatingProcess()
                } else if ((itemBefore.expression == Expression.NUMBER || itemBefore.expression == Expression.DOUBLE || itemBefore.expression == Expression.PI || itemBefore.expression == Expression.E) && (itemAfter.expression == Expression.NUMBER || itemAfter.expression == Expression.DOUBLE || itemAfter.expression == Expression.PI || itemAfter.expression == Expression.E)) {
                    val toDouble = plusMinusMultiplyDivide(
                        if (itemBefore.expression == Expression.PI) PI else if (itemBefore.expression == Expression.E) E else itemBefore.value.toString()
                            .toDouble(),
                        if (itemAfter.expression == Expression.PI) PI else if (itemAfter.expression == Expression.E) E else itemAfter.value.toString()
                            .toDouble(),
                        findMultiplyDivide.expression
                    ).toDouble()
                    processList[indexOf].value = toDouble
                    processList[indexOf].name = toDouble.toString()
                    processList[indexOf].expression = Expression.DOUBLE
                    processList.removeAt(indexOf - 1)
                    processList.removeAt(indexOf)
                    Log.d("loooooooog", "calculatingProcess:2 ${processList.map { it.value }}")
                    return calculatingProcess()
                } else /*if*/ {
                    //sin cos tan... here
                }
            } else if (findPlusMinus != null) {
                val indexOf = processList.indexOf(findPlusMinus)
                if (indexOf == 0 || indexOf == processList.lastIndex) return "=Error"
                val itemBefore = processList[indexOf - 1]
                val itemAfter = processList[indexOf + 1]
                if (itemBefore.expression == Expression.NUMBER && itemAfter.expression == Expression.NUMBER) {
                    val toLong = plusMinusMultiplyDivide(
                        itemBefore.value.toString().toLong(),
                        itemAfter.value.toString().toLong(),
                        findPlusMinus.expression
                    ).toLong()
                    processList[indexOf].value = toLong
                    processList[indexOf].name = toLong.toString()
                    processList[indexOf].expression = Expression.NUMBER
                    processList.removeAt(indexOf - 1)
                    processList.removeAt(indexOf)
                    Log.d("loooooooog", "calculatingProcess:3 ${processList.map { it.value }}")
                    return calculatingProcess()
                } else if ((itemBefore.expression == Expression.NUMBER || itemBefore.expression == Expression.DOUBLE || itemBefore.expression == Expression.PI || itemBefore.expression == Expression.E) && (itemAfter.expression == Expression.NUMBER || itemAfter.expression == Expression.DOUBLE || itemAfter.expression == Expression.PI || itemAfter.expression == Expression.E)) {
                    val toDouble = plusMinusMultiplyDivide(
                        if (itemBefore.expression == Expression.PI) PI else if (itemBefore.expression == Expression.E) E else itemBefore.value.toString()
                            .toDouble(),
                        if (itemAfter.expression == Expression.PI) PI else if (itemAfter.expression == Expression.E) E else itemAfter.value.toString()
                            .toDouble(),
                        findPlusMinus.expression
                    ).toDouble()
                    processList[indexOf].value = toDouble
                    processList[indexOf].name = toDouble.toString()
                    processList[indexOf].expression = Expression.DOUBLE
                    processList.removeAt(indexOf - 1)
                    processList.removeAt(indexOf)
                    Log.d("loooooooog", "calculatingProcess:4 ${processList.map { it.value }}")
                    return calculatingProcess()
                } else /*if*/ {
                    //sin cos tan... here
                }
            } else {
                Log.d("loooooooog", "calculatingProcess:5 ${processList.map { it.value }}")
                return "I cant find +-*/"
            }
        } else if (processList.size == 2) {
            createNegativeNumbers()
            if (processList.size == 1) {
                return valueOfItem(processList[0])
            }
            if (isComplexOperation(processList[0]) && isNumber(processList[1])) {
                val complexOperation = complexOperation(
                    processList[1].value.toString().toDouble(),
                    processList[0].expression
                )
                processList.removeAt(1)
                processList[0].value = complexOperation
                processList[0].name = complexOperation.toString()
                processList[0].expression = Expression.DOUBLE
                return complexOperation
            }
            if ((processList[0].expression == Expression.NUMBER || processList[0].expression == Expression.DOUBLE || processList[0].expression == Expression.E || processList[0].expression == Expression.PI) && processList[1].expression == Expression.DIV1_TO_X) {
                return 1 / valueOfItem(processList[0])
            }
            return "=Error"
        } else if (processList.size == 1) {
            Log.d("loooooooog", "calculatingProcess:7 ${processList.map { it.value }}")
            expresionsList.clear()
            expresionsList.addAll(processList)
            return valueOfItem(processList[0])
        } else {
            return "0"
        }
        return ""
    }

    private fun plusMinusMultiplyDivide(a: Number, b: Number, expression: Expression): Number {
        return if (a is Double || b is Double) {
            when (expression) {
                Expression.PLUS -> a.toDouble() + b.toDouble()
                Expression.MINUS -> a.toDouble() - b.toDouble()
                Expression.MULTIPLY -> a.toDouble() * b.toDouble()
                else -> a.toDouble() / b.toDouble()
            }
        } else {
            when (expression) {
                Expression.PLUS -> a.toLong() + b.toLong()
                Expression.MINUS -> a.toLong() - b.toLong()
                Expression.MULTIPLY -> a.toLong() * b.toLong()
                else -> a.toDouble() / b.toLong()
            }
        }
    }

    private fun complexOperation(a: Double, expression: Expression, b: Double = 1.0): Double {
        return when (expression) {
            Expression.SIN -> sin(Math.toRadians(a))
            Expression.COS -> {
                val d = (a + 36000000) % 360
                if (d == 90.0 || d == 270.0) 0.0
                else cos(Math.toRadians(d))
            }

            Expression.TAN -> complexOperation(a, Expression.SIN) / complexOperation(
                a,
                Expression.COS
            )

            Expression.LN -> ln(a)
            Expression.LOG -> log10(a)
            Expression.E_IN_DEGREE_X -> Math.E.pow(a)
            Expression.DIV1_TO_X -> 1 / a
            Expression.SQRT -> a.pow(0.5)
            Expression.ABS -> abs(a)
            else -> {
                Log.d("loooooooog", "complexOperation: ElsSEEEEE")
                a
            }
        }
    }

    private fun calculateInsideParenthesis(): Double? {
        val lastComplexItem = processList.findLast {
            it.expression == Expression.OPEN_PARENTHESIS ||
                    it.expression == Expression.SIN ||
                    it.expression == Expression.COS ||
                    it.expression == Expression.TAN ||
                    it.expression == Expression.LN ||
                    it.expression == Expression.LOG ||
                    it.expression == Expression.E_IN_DEGREE_X ||
                    it.expression == Expression.IN_DEGREE_X ||
                    it.expression == Expression.SQRT ||
                    it.expression == Expression.ABS
        } ?: return null
        val indexOfLastComplexItem = processList.indexOfLast {
            it.expression == Expression.OPEN_PARENTHESIS ||
                    it.expression == Expression.SIN ||
                    it.expression == Expression.COS ||
                    it.expression == Expression.TAN ||
                    it.expression == Expression.LN ||
                    it.expression == Expression.LOG ||
                    it.expression == Expression.E_IN_DEGREE_X ||
                    it.expression == Expression.IN_DEGREE_X ||
                    it.expression == Expression.SQRT ||
                    it.expression == Expression.ABS
        }
        Log.d("loooooooog", "calculateInsideParenthesisGo: ")
        val subList = processList.subList(indexOfLastComplexItem, processList.lastIndex + 1)
        val indexOfFirstClosing =
            indexOfLastComplexItem + subList.indexOfFirst { it.expression == Expression.CLOSE_PARENTHESIS }
        if (indexOfFirstClosing == -1) return null
        Log.d(
            "loooooooog",
            "calculateInsideParenthesisGo2:$indexOfFirstClosing $indexOfLastComplexItem "
        )
        if (indexOfLastComplexItem + 2 == indexOfFirstClosing) {
            if (lastComplexItem.expression == Expression.IN_DEGREE_X) return 1.0
            val complexOperation = complexOperation(
                valueOfItem(processList[indexOfLastComplexItem + 1]),
                lastComplexItem.expression
            )
            lastComplexItem.expression = Expression.DOUBLE
            lastComplexItem.name = complexOperation.toString()
            lastComplexItem.value = complexOperation
            processList[indexOfLastComplexItem] = lastComplexItem
            processList.removeAt(indexOfLastComplexItem + 1)
            processList.removeAt(indexOfLastComplexItem + 1)
            Log.d("loooooooog", "calculateInsideParenthesis1: $complexOperation")
            return complexOperation
        } else {
            val item1 = processList[indexOfLastComplexItem + 1]
            val item2 = processList[indexOfLastComplexItem + 2]
            val item3 = processList[indexOfLastComplexItem + 3]
            return if ((item1.expression == Expression.DOUBLE || item1.expression == Expression.NUMBER || item1.expression == Expression.E || item1.expression == Expression.PI) &&
                (item3.expression == Expression.DOUBLE || item3.expression == Expression.NUMBER || item3.expression == Expression.E || item3.expression == Expression.PI) &&
                (item2.expression == Expression.PLUS || item2.expression == Expression.MINUS || item2.expression == Expression.MULTIPLY || item2.expression == Expression.DIVIDE)
            ) {
                val plusMinusMultiplyDivide = plusMinusMultiplyDivide(
                    valueOfItem(item1),
                    valueOfItem(item3),
                    item2.expression
                ).toDouble()
                item1.value = plusMinusMultiplyDivide
                item1.name = plusMinusMultiplyDivide.toString()
                item1.expression = Expression.DOUBLE
                processList[indexOfLastComplexItem + 1] = item1
                processList.removeAt(indexOfLastComplexItem + 2)
                processList.removeAt(indexOfLastComplexItem + 2)
                Log.d("loooooooog", "calculateInsideParenthesis:2 ${processList.map { it.value }}")
                calculateInsideParenthesis()
            } else null
        }
    }

    private fun rotateScreen() {
        val orientation = resources.configuration.orientation
        requestedOrientation = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

}