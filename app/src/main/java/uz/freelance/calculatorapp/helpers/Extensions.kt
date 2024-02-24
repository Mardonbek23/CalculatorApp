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
inline fun <reified T> typeToken(): Class<T> = T::class.java