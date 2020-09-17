package studio.codable.unpause.utilities

import studio.codable.unpause.model.Shift

typealias BaseClickListener<T> = ((T) -> Any)

typealias LambdaStringToUnit = (String) -> Unit
typealias LambdaNoArgumentsUnit = () -> Unit
typealias LambdaShiftIntToUnit = (Shift, Int) -> Unit
typealias LambdaDoubleIntToUnit = (Int, Int) -> Unit
typealias LambdaExceptionToUnit = (Exception) -> Unit
typealias LambdaShiftToBool = (Shift?) -> Boolean
