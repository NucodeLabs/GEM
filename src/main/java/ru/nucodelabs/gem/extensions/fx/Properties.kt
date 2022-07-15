package ru.nucodelabs.gem.extensions.fx

/**
 * From TornadoFX sources
 */
import javafx.beans.Observable
import javafx.beans.binding.*
import javafx.beans.property.*
import javafx.beans.value.*
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import java.util.concurrent.Callable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> property(value: T? = null) = PropertyDelegate(SimpleObjectProperty<T>(value))
fun <T> property(init: () -> Property<T>) = PropertyDelegate(init())

class PropertyDelegate<T>(val fxProperty: Property<T>) : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>) = fxProperty.value

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        fxProperty.value = value
    }

}

/**
 * Binds this property to an observable, automatically unbinding it before if already bound.
 */
infix fun <T> Property<T>.rebindTo(observable: ObservableValue<T>) {
    unbind()
    bind(observable)
}

operator fun <T> ObservableValue<T>.getValue(thisRef: Any, property: KProperty<*>) = value
operator fun <T> Property<T>.setValue(thisRef: Any, property: KProperty<*>, value: T?) = setValue(value)

operator fun ObservableDoubleValue.getValue(thisRef: Any, property: KProperty<*>) = get()
operator fun DoubleProperty.setValue(thisRef: Any, property: KProperty<*>, value: Double) = set(value)

operator fun ObservableFloatValue.getValue(thisRef: Any, property: KProperty<*>) = get()
operator fun FloatProperty.setValue(thisRef: Any, property: KProperty<*>, value: Float) = set(value)

operator fun ObservableLongValue.getValue(thisRef: Any, property: KProperty<*>) = get()
operator fun LongProperty.setValue(thisRef: Any, property: KProperty<*>, value: Long) = set(value)

operator fun ObservableIntegerValue.getValue(thisRef: Any, property: KProperty<*>) = get()
operator fun IntegerProperty.setValue(thisRef: Any, property: KProperty<*>, value: Int) = set(value)

operator fun ObservableBooleanValue.getValue(thisRef: Any, property: KProperty<*>) = get()
operator fun BooleanProperty.setValue(thisRef: Any, property: KProperty<*>, value: Boolean) = set(value)


operator fun DoubleExpression.plus(other: Number): DoubleBinding = add(other.toDouble())
operator fun DoubleExpression.plus(other: ObservableNumberValue): DoubleBinding = add(other)

operator fun DoubleProperty.plusAssign(other: Number) {
    value += other.toDouble()
}

operator fun DoubleProperty.plusAssign(other: ObservableNumberValue) {
    value += other.doubleValue()
}

operator fun DoubleExpression.minus(other: Number): DoubleBinding = subtract(other.toDouble())
operator fun DoubleExpression.minus(other: ObservableNumberValue): DoubleBinding = subtract(other)

operator fun DoubleProperty.minusAssign(other: Number) {
    value -= other.toDouble()
}

operator fun DoubleProperty.minusAssign(other: ObservableNumberValue) {
    value -= other.doubleValue()
}

operator fun DoubleExpression.unaryMinus(): DoubleBinding = negate()

operator fun DoubleExpression.times(other: Number): DoubleBinding = multiply(other.toDouble())
operator fun DoubleExpression.times(other: ObservableNumberValue): DoubleBinding = multiply(other)

operator fun DoubleProperty.timesAssign(other: Number) {
    value *= other.toDouble()
}

operator fun DoubleProperty.timesAssign(other: ObservableNumberValue) {
    value *= other.doubleValue()
}

operator fun DoubleExpression.div(other: Number): DoubleBinding = divide(other.toDouble())
operator fun DoubleExpression.div(other: ObservableNumberValue): DoubleBinding = divide(other)

operator fun DoubleProperty.divAssign(other: Number) {
    value /= other.toDouble()
}

operator fun DoubleProperty.divAssign(other: ObservableNumberValue) {
    value /= other.doubleValue()
}


operator fun DoubleExpression.rem(other: Number): DoubleBinding = doubleBinding(this) { get() % other.toDouble() }
operator fun DoubleExpression.rem(other: ObservableNumberValue): DoubleBinding =
    doubleBinding(this, other) { get() % other.doubleValue() }

operator fun DoubleProperty.remAssign(other: Number) {
    value %= other.toDouble()
}

operator fun DoubleProperty.remAssign(other: ObservableNumberValue) {
    value %= other.doubleValue()
}

operator fun ObservableDoubleValue.compareTo(other: Number) = get().compareTo(other.toDouble())

operator fun ObservableDoubleValue.compareTo(other: ObservableNumberValue) = get().compareTo(other.doubleValue())

operator fun FloatExpression.plus(other: Number): FloatBinding = add(other.toFloat())
operator fun FloatExpression.plus(other: Double): DoubleBinding = add(other)
operator fun FloatExpression.plus(other: ObservableNumberValue): FloatBinding = add(other) as FloatBinding
operator fun FloatExpression.plus(other: ObservableDoubleValue): DoubleBinding = add(other) as DoubleBinding

operator fun FloatProperty.plusAssign(other: Number) {
    value += other.toFloat()
}

operator fun FloatProperty.plusAssign(other: ObservableNumberValue) {
    value += other.floatValue()
}

operator fun FloatExpression.minus(other: Number): FloatBinding = subtract(other.toFloat())
operator fun FloatExpression.minus(other: Double): DoubleBinding = subtract(other)
operator fun FloatExpression.minus(other: ObservableNumberValue): FloatBinding = subtract(other) as FloatBinding
operator fun FloatExpression.minus(other: ObservableDoubleValue): DoubleBinding = subtract(other) as DoubleBinding

operator fun FloatProperty.minusAssign(other: Number) {
    value -= other.toFloat()
}

operator fun FloatProperty.minusAssign(other: ObservableNumberValue) {
    value -= other.floatValue()
}

operator fun FloatExpression.unaryMinus(): FloatBinding = negate()

operator fun FloatExpression.times(other: Number): FloatBinding = multiply(other.toFloat())
operator fun FloatExpression.times(other: Double): DoubleBinding = multiply(other)
operator fun FloatExpression.times(other: ObservableNumberValue): FloatBinding = multiply(other) as FloatBinding
operator fun FloatExpression.times(other: ObservableDoubleValue): DoubleBinding = multiply(other) as DoubleBinding

operator fun FloatProperty.timesAssign(other: Number) {
    value *= other.toFloat()
}

operator fun FloatProperty.timesAssign(other: ObservableNumberValue) {
    value *= other.floatValue()
}


operator fun FloatExpression.div(other: Number): FloatBinding = divide(other.toFloat())
operator fun FloatExpression.div(other: Double): DoubleBinding = divide(other)
operator fun FloatExpression.div(other: ObservableNumberValue): FloatBinding = divide(other) as FloatBinding
operator fun FloatExpression.div(other: ObservableDoubleValue): DoubleBinding = divide(other) as DoubleBinding

operator fun FloatProperty.divAssign(other: Number) {
    value /= other.toFloat()
}

operator fun FloatProperty.divAssign(other: ObservableNumberValue) {
    value /= other.floatValue()
}


operator fun FloatExpression.rem(other: Number): FloatBinding = floatBinding(this) { get() % other.toFloat() }
operator fun FloatExpression.rem(other: Double): DoubleBinding = doubleBinding(this) { get() % other }
operator fun FloatExpression.rem(other: ObservableNumberValue): FloatBinding =
    floatBinding(this, other) { get() % other.floatValue() }

operator fun FloatExpression.rem(other: ObservableDoubleValue): DoubleBinding =
    doubleBinding(this, other) { get() % other.get() }

operator fun FloatProperty.remAssign(other: Number) {
    value %= other.toFloat()
}

operator fun FloatProperty.remAssign(other: ObservableNumberValue) {
    value %= other.floatValue()
}

operator fun ObservableFloatValue.compareTo(other: Number) = get().compareTo(other.toFloat())

operator fun ObservableFloatValue.compareTo(other: ObservableNumberValue) = get().compareTo(other.floatValue())


operator fun IntegerExpression.plus(other: Int): IntegerBinding = add(other)
operator fun IntegerExpression.plus(other: Long): LongBinding = add(other)
operator fun IntegerExpression.plus(other: Float): FloatBinding = add(other)
operator fun IntegerExpression.plus(other: Double): DoubleBinding = add(other)
operator fun IntegerExpression.plus(other: ObservableIntegerValue): IntegerBinding = add(other) as IntegerBinding
operator fun IntegerExpression.plus(other: ObservableLongValue): LongBinding = add(other) as LongBinding
operator fun IntegerExpression.plus(other: ObservableFloatValue): FloatBinding = add(other) as FloatBinding
operator fun IntegerExpression.plus(other: ObservableDoubleValue): DoubleBinding = add(other) as DoubleBinding

operator fun IntegerProperty.plusAssign(other: Number) {
    value += other.toInt()
}

operator fun IntegerProperty.plusAssign(other: ObservableNumberValue) {
    value += other.intValue()
}

operator fun IntegerExpression.minus(other: Int): IntegerBinding = subtract(other)
operator fun IntegerExpression.minus(other: Long): LongBinding = subtract(other)
operator fun IntegerExpression.minus(other: Float): FloatBinding = subtract(other)
operator fun IntegerExpression.minus(other: Double): DoubleBinding = subtract(other)
operator fun IntegerExpression.minus(other: ObservableIntegerValue): IntegerBinding = subtract(other) as IntegerBinding
operator fun IntegerExpression.minus(other: ObservableLongValue): LongBinding = subtract(other) as LongBinding
operator fun IntegerExpression.minus(other: ObservableFloatValue): FloatBinding = subtract(other) as FloatBinding
operator fun IntegerExpression.minus(other: ObservableDoubleValue): DoubleBinding = subtract(other) as DoubleBinding

operator fun IntegerProperty.minusAssign(other: Number) {
    value -= other.toInt()
}

operator fun IntegerProperty.minusAssign(other: ObservableNumberValue) {
    value -= other.intValue()
}

operator fun IntegerExpression.unaryMinus(): IntegerBinding = negate()

operator fun IntegerExpression.times(other: Int): IntegerBinding = multiply(other)
operator fun IntegerExpression.times(other: Long): LongBinding = multiply(other)
operator fun IntegerExpression.times(other: Float): FloatBinding = multiply(other)
operator fun IntegerExpression.times(other: Double): DoubleBinding = multiply(other)
operator fun IntegerExpression.times(other: ObservableIntegerValue): IntegerBinding = multiply(other) as IntegerBinding
operator fun IntegerExpression.times(other: ObservableLongValue): LongBinding = multiply(other) as LongBinding
operator fun IntegerExpression.times(other: ObservableFloatValue): FloatBinding = multiply(other) as FloatBinding
operator fun IntegerExpression.times(other: ObservableDoubleValue): DoubleBinding = multiply(other) as DoubleBinding

operator fun IntegerProperty.timesAssign(other: Number) {
    value *= other.toInt()
}

operator fun IntegerProperty.timesAssign(other: ObservableNumberValue) {
    value *= other.intValue()
}

operator fun IntegerExpression.div(other: Int): IntegerBinding = divide(other)
operator fun IntegerExpression.div(other: Long): LongBinding = divide(other)
operator fun IntegerExpression.div(other: Float): FloatBinding = divide(other)
operator fun IntegerExpression.div(other: Double): DoubleBinding = divide(other)
operator fun IntegerExpression.div(other: ObservableIntegerValue): IntegerBinding = divide(other) as IntegerBinding
operator fun IntegerExpression.div(other: ObservableLongValue): LongBinding = divide(other) as LongBinding
operator fun IntegerExpression.div(other: ObservableFloatValue): FloatBinding = divide(other) as FloatBinding
operator fun IntegerExpression.div(other: ObservableDoubleValue): DoubleBinding = divide(other) as DoubleBinding

operator fun IntegerProperty.divAssign(other: Number) {
    value /= other.toInt()
}

operator fun IntegerProperty.divAssign(other: ObservableNumberValue) {
    value /= other.intValue()
}

operator fun IntegerExpression.rem(other: Int): IntegerBinding = integerBinding(this) { get() % other }
operator fun IntegerExpression.rem(other: Long): LongBinding = longBinding(this) { get() % other }
operator fun IntegerExpression.rem(other: Float): FloatBinding = floatBinding(this) { get() % other }
operator fun IntegerExpression.rem(other: Double): DoubleBinding = doubleBinding(this) { get() % other }
operator fun IntegerExpression.rem(other: ObservableIntegerValue): IntegerBinding =
    integerBinding(this, other) { get() % other.get() }

operator fun IntegerExpression.rem(other: ObservableLongValue): LongBinding =
    longBinding(this, other) { get() % other.get() }

operator fun IntegerExpression.rem(other: ObservableFloatValue): FloatBinding =
    floatBinding(this, other) { get() % other.get() }

operator fun IntegerExpression.rem(other: ObservableDoubleValue): DoubleBinding =
    doubleBinding(this, other) { get() % other.get() }

operator fun IntegerProperty.remAssign(other: Number) {
    value %= other.toInt()
}

operator fun IntegerProperty.remAssign(other: ObservableNumberValue) {
    value %= other.intValue()
}

operator fun ObservableIntegerValue.compareTo(other: Number) = get().compareTo(other.toDouble())
operator fun ObservableIntegerValue.compareTo(other: ObservableNumberValue) = get().compareTo(other.doubleValue())


operator fun LongExpression.plus(other: Number): LongBinding = add(other.toLong())
operator fun LongExpression.plus(other: Float): FloatBinding = add(other)
operator fun LongExpression.plus(other: Double): DoubleBinding = add(other)
operator fun LongExpression.plus(other: ObservableNumberValue): LongBinding = add(other) as LongBinding
operator fun LongExpression.plus(other: ObservableFloatValue): FloatBinding = add(other) as FloatBinding
operator fun LongExpression.plus(other: ObservableDoubleValue): DoubleBinding = add(other) as DoubleBinding

operator fun LongProperty.plusAssign(other: Number) {
    value += other.toLong()
}

operator fun LongProperty.plusAssign(other: ObservableNumberValue) {
    value += other.longValue()
}

operator fun LongExpression.minus(other: Number): LongBinding = subtract(other.toLong())
operator fun LongExpression.minus(other: Float): FloatBinding = subtract(other)
operator fun LongExpression.minus(other: Double): DoubleBinding = subtract(other)
operator fun LongExpression.minus(other: ObservableNumberValue): LongBinding = subtract(other) as LongBinding
operator fun LongExpression.minus(other: ObservableFloatValue): FloatBinding = subtract(other) as FloatBinding
operator fun LongExpression.minus(other: ObservableDoubleValue): DoubleBinding = subtract(other) as DoubleBinding

operator fun LongProperty.minusAssign(other: Number) {
    value -= other.toLong()
}

operator fun LongProperty.minusAssign(other: ObservableNumberValue) {
    value -= other.longValue()
}

operator fun LongExpression.unaryMinus(): LongBinding = negate()


operator fun LongExpression.times(other: Number): LongBinding = multiply(other.toLong())
operator fun LongExpression.times(other: Float): FloatBinding = multiply(other)
operator fun LongExpression.times(other: Double): DoubleBinding = multiply(other)
operator fun LongExpression.times(other: ObservableNumberValue): LongBinding = multiply(other) as LongBinding
operator fun LongExpression.times(other: ObservableFloatValue): FloatBinding = multiply(other) as FloatBinding
operator fun LongExpression.times(other: ObservableDoubleValue): DoubleBinding = multiply(other) as DoubleBinding

operator fun LongProperty.timesAssign(other: Number) {
    value *= other.toLong()
}

operator fun LongProperty.timesAssign(other: ObservableNumberValue) {
    value *= other.longValue()
}

operator fun LongExpression.div(other: Number): LongBinding = divide(other.toLong())
operator fun LongExpression.div(other: Float): FloatBinding = divide(other)
operator fun LongExpression.div(other: Double): DoubleBinding = divide(other)
operator fun LongExpression.div(other: ObservableNumberValue): LongBinding = divide(other) as LongBinding
operator fun LongExpression.div(other: ObservableFloatValue): FloatBinding = divide(other) as FloatBinding
operator fun LongExpression.div(other: ObservableDoubleValue): DoubleBinding = divide(other) as DoubleBinding

operator fun LongProperty.divAssign(other: Number) {
    value /= other.toLong()
}

operator fun LongProperty.divAssign(other: ObservableNumberValue) {
    value /= other.longValue()
}

operator fun LongExpression.rem(other: Number): LongBinding = longBinding(this) { get() % other.toLong() }
operator fun LongExpression.rem(other: Float): FloatBinding = floatBinding(this) { get() % other }
operator fun LongExpression.rem(other: Double): DoubleBinding = doubleBinding(this) { get() % other }

operator fun LongExpression.rem(other: ObservableNumberValue): LongBinding =
    longBinding(this, other) { this.get() % other.longValue() }

operator fun LongExpression.rem(other: ObservableFloatValue): FloatBinding =
    floatBinding(this, other) { this.get() % other.get() }

operator fun LongExpression.rem(other: ObservableDoubleValue): DoubleBinding =
    doubleBinding(this, other) { this.get() % other.get() }

operator fun LongProperty.remAssign(other: Number) {
    value %= other.toLong()
}

operator fun LongProperty.remAssign(other: ObservableNumberValue) {
    value %= other.longValue()
}

operator fun ObservableLongValue.compareTo(other: Number) = get().compareTo(other.toDouble())
operator fun ObservableLongValue.compareTo(other: ObservableNumberValue) = get().compareTo(other.doubleValue())

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
operator fun BooleanExpression.not(): BooleanBinding = not()

infix fun BooleanExpression.and(other: Boolean): BooleanBinding = and(SimpleBooleanProperty(other))
infix fun BooleanExpression.and(other: ObservableBooleanValue): BooleanBinding = and(other)

infix fun BooleanExpression.or(other: Boolean): BooleanBinding = or(SimpleBooleanProperty(other))
infix fun BooleanExpression.or(other: ObservableBooleanValue): BooleanBinding = or(other)

infix fun BooleanExpression.xor(other: Boolean): BooleanBinding = booleanBinding(this) { get() xor other }
infix fun BooleanExpression.xor(other: ObservableBooleanValue): BooleanBinding =
    booleanBinding(this, other) { get() xor other.get() }

infix fun BooleanExpression.eq(other: Boolean): BooleanBinding = isEqualTo(SimpleBooleanProperty(other))
infix fun BooleanExpression.eq(other: ObservableBooleanValue): BooleanBinding = isEqualTo(other)


operator fun StringExpression.plus(other: Any): StringExpression = concat(other)
operator fun StringProperty.plusAssign(other: Any) {
    value += other
}

operator fun StringExpression.get(index: Int): Binding<Char?> = objectBinding(this) {
    if (index < get().length)
        get()[index]
    else
        null
}

operator fun StringExpression.get(index: ObservableIntegerValue): Binding<Char?> = objectBinding(this, index) {
    if (index < get().length)
        get()[index.get()]
    else
        null
}

operator fun StringExpression.get(start: Int, end: Int): StringBinding =
    stringBinding(this) { get().subSequence(start, end).toString() }

operator fun StringExpression.get(start: ObservableIntegerValue, end: Int): StringBinding =
    stringBinding(this, start) { get().subSequence(start.get(), end).toString() }

operator fun StringExpression.get(start: Int, end: ObservableIntegerValue): StringBinding =
    stringBinding(this, end) { get().subSequence(start, end.get()).toString() }

operator fun StringExpression.get(start: ObservableIntegerValue, end: ObservableIntegerValue): StringBinding =
    stringBinding(this, start, end) { get().subSequence(start.get(), end.get()).toString() }

operator fun StringExpression.unaryMinus(): StringBinding = stringBinding(this) { get().reversed() }

operator fun StringExpression.compareTo(other: String): Int = get().compareTo(other)
operator fun StringExpression.compareTo(other: ObservableStringValue): Int = get().compareTo(other.get())

infix fun StringExpression.eqIgnoreCase(other: String): BooleanBinding = isEqualToIgnoreCase(other)
infix fun StringExpression.eqIgnoreCase(other: ObservableStringValue): BooleanBinding = isEqualToIgnoreCase(other)

fun <T> ObservableValue<T>.integerBinding(vararg dependencies: Observable, op: (T?) -> Int): IntegerBinding =
    Bindings.createIntegerBinding(Callable { op(value) }, this, *dependencies)

fun <T : Any> integerBinding(receiver: T, vararg dependencies: Observable, op: T.() -> Int): IntegerBinding =
    Bindings.createIntegerBinding(Callable { receiver.op() }, *createObservableArray(receiver, *dependencies))

fun <T> ObservableValue<T>.longBinding(vararg dependencies: Observable, op: (T?) -> Long): LongBinding =
    Bindings.createLongBinding(Callable { op(value) }, this, *dependencies)

fun <T : Any> longBinding(receiver: T, vararg dependencies: Observable, op: T.() -> Long): LongBinding =
    Bindings.createLongBinding(Callable { receiver.op() }, *createObservableArray(receiver, *dependencies))

fun <T> ObservableValue<T>.doubleBinding(vararg dependencies: Observable, op: (T?) -> Double): DoubleBinding =
    Bindings.createDoubleBinding(Callable { op(value) }, this, *dependencies)

fun <T : Any> doubleBinding(receiver: T, vararg dependencies: Observable, op: T.() -> Double): DoubleBinding =
    Bindings.createDoubleBinding(Callable { receiver.op() }, *createObservableArray(receiver, *dependencies))

fun <T> ObservableValue<T>.floatBinding(vararg dependencies: Observable, op: (T?) -> Float): FloatBinding =
    Bindings.createFloatBinding(Callable { op(value) }, this, *dependencies)

fun <T : Any> floatBinding(receiver: T, vararg dependencies: Observable, op: T.() -> Float): FloatBinding =
    Bindings.createFloatBinding(Callable { receiver.op() }, *createObservableArray(receiver, *dependencies))

fun <T> ObservableValue<T>.booleanBinding(vararg dependencies: Observable, op: (T?) -> Boolean): BooleanBinding =
    Bindings.createBooleanBinding(Callable { op(value) }, this, *dependencies)

fun <T : Any> booleanBinding(receiver: T, vararg dependencies: Observable, op: T.() -> Boolean): BooleanBinding =
    Bindings.createBooleanBinding(Callable { receiver.op() }, *createObservableArray(receiver, *dependencies))

fun <T> ObservableValue<T>.stringBinding(vararg dependencies: Observable, op: (T?) -> String?): StringBinding =
    Bindings.createStringBinding(Callable { op(value) }, this, *dependencies)

fun <T : Any> stringBinding(receiver: T, vararg dependencies: Observable, op: T.() -> String?): StringBinding =
    Bindings.createStringBinding(Callable { receiver.op() }, *createObservableArray(receiver, *dependencies))

fun <T, R> ObservableValue<T>.objectBinding(vararg dependencies: Observable, op: (T?) -> R?): Binding<R?> =
    Bindings.createObjectBinding(Callable { op(value) }, this, *dependencies)

fun <T : Any, R> objectBinding(receiver: T, vararg dependencies: Observable, op: T.() -> R?): ObjectBinding<R?> =
    Bindings.createObjectBinding(Callable { receiver.op() }, *createObservableArray(receiver, *dependencies))

fun <T : Any, R> nonNullObjectBinding(receiver: T, vararg dependencies: Observable, op: T.() -> R): ObjectBinding<R> =
    Bindings.createObjectBinding(Callable { receiver.op() }, *createObservableArray(receiver, *dependencies))

private fun <T> createObservableArray(receiver: T, vararg dependencies: Observable): Array<out Observable> =
    if (receiver is Observable) arrayOf(receiver, *dependencies) else dependencies

/**
 * Assign the value from the creator to this WritableValue if and only if it is currently null
 */
fun <T> WritableValue<T>.assignIfNull(creator: () -> T) {
    if (value == null) value = creator()
}

fun Double.toProperty(): DoubleProperty = SimpleDoubleProperty(this)
fun Float.toProperty(): FloatProperty = SimpleFloatProperty(this)
fun Long.toProperty(): LongProperty = SimpleLongProperty(this)
fun Int.toProperty(): IntegerProperty = SimpleIntegerProperty(this)
fun Boolean.toProperty(): BooleanProperty = SimpleBooleanProperty(this)
fun String.toProperty(): StringProperty = SimpleStringProperty(this)

fun String?.toProperty() = SimpleStringProperty(this ?: "")
fun Double?.toProperty() = SimpleDoubleProperty(this ?: 0.0)
fun Float?.toProperty() = SimpleFloatProperty(this ?: 0.0F)
fun Long?.toProperty() = SimpleLongProperty(this ?: 0L)
fun Boolean?.toProperty() = SimpleBooleanProperty(this ?: false)
fun <T : Any> T?.toProperty() = SimpleObjectProperty<T>(this)

fun booleanProperty(value: Boolean = false): BooleanProperty = SimpleBooleanProperty(value)
fun doubleProperty(value: Double = 0.0): DoubleProperty = SimpleDoubleProperty(value)
fun floatProperty(value: Float = 0F): FloatProperty = SimpleFloatProperty(value)
fun intProperty(value: Int = 0): IntegerProperty = SimpleIntegerProperty(value)
fun <V> listProperty(value: ObservableList<V>? = null): ListProperty<V> = SimpleListProperty(value)
fun <V> listProperty(vararg values: V): ListProperty<V> = SimpleListProperty(values.toMutableList().toObservableList())
fun longProperty(value: Long): LongProperty = SimpleLongProperty(value)
fun <K, V> mapProperty(value: ObservableMap<K, V>? = null): MapProperty<K, V> = SimpleMapProperty(value)
fun <T> objectProperty(value: T? = null): ObjectProperty<T> = SimpleObjectProperty(value)
fun <V> setProperty(value: ObservableSet<V>? = null): SetProperty<V> = SimpleSetProperty(value)
fun stringProperty(value: String? = null): StringProperty = SimpleStringProperty(value)