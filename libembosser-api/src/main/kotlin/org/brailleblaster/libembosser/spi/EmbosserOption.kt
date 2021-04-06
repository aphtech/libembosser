package org.brailleblaster.libembosser.spi

sealed class EmbosserOption {
    abstract val name: String
    data class BooleanOption(override val name: String, val value: Boolean) : EmbosserOption()
    data class MultipleChoiceOption<T : MultipleChoiceValue>(override val name: String, val value: T, val choices: List<T>) : EmbosserOption() {
        init {
            require(choices.isNotEmpty())
            require(choices.contains(value))
        }
    }

    data class StringOption(override val name: String, val value: String) : EmbosserOption()

    interface MultipleChoiceValue {
        val displayString: String
    }
}
