package uz.freelance.calculatorapp.helpers

import uz.freelance.calculatorapp.models.Expression
import uz.freelance.calculatorapp.models.Item

fun valueOfItem(item: Item): Double {
    return when (item.expression) {
        Expression.E -> Math.E
        Expression.PI -> Math.PI
        else -> item.value.toString().toDouble()
    }
}

fun isNumber(item: Item): Boolean {
    return item.expression == Expression.E || item.expression == Expression.PI || item.expression == Expression.DOUBLE || item.expression == Expression.NUMBER
}

fun isComplexOperation(item: Item): Boolean {
    return item.expression == Expression.SIN || item.expression == Expression.COS || item.expression == Expression.TAN || item.expression == Expression.LN || item.expression == Expression.LOG || item.expression == Expression.IN_DEGREE_X || item.expression == Expression.OPEN_PARENTHESIS || item.expression == Expression.E_IN_DEGREE_X || item.expression == Expression.DIV1_TO_X || item.expression == Expression.SQRT
}

inline fun <reified T> typeToken(): Class<T> = T::class.java