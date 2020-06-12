package studio.codable.unpause.utilities

import studio.codable.unpause.model.Shift

typealias BaseClickListener<T> = ((T) -> Any)

typealias StringToUnitLambda = (String) -> Unit
typealias NoArgumentsUnitLambda = () -> Unit
typealias ShiftWithPositionLambda = (Shift, Int) -> Unit
typealias DoubleIntToUnitLambda = (Int, Int) -> Unit
