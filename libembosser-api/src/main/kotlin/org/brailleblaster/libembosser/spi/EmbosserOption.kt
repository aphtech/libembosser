package org.brailleblaster.libembosser.spi

sealed class EmbosserOption {
    abstract val name: String
    data class BooleanOption(override val name: String, var value: Boolean) : EmbosserOption()
    data class MultipleChoiceOption<T : MultipleChoiceValue>(override val name: String, private var valueField: T, val choices: List<T>) : EmbosserOption() {
        init {
            require(choices.isNotEmpty())
        }
var value: T
get() = valueField
    set(value) {
        require(choices.contains(value))
        valueField = value
    }
    }

    data class StringOption(override val name: String, var value: String) : EmbosserOption()

    interface MultipleChoiceValue {
        val displayString: String
    }
}
