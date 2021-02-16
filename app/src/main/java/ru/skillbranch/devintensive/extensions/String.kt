package ru.skillbranch.devintensive.extensions

fun String.truncate(countSymbols: Int = 16): String {
    var result = this.trim()
    if (result.length > countSymbols) {
        result = "${result.substring(0, countSymbols).trimEnd()}..."
    }
    return result
}

fun String.stripHtml(): String {
    var result = this.trim()

    // удаление html-тегов
    result = Regex("""<.*?>""").replace(result, "")

    // удаление html escape последовательностей типа &nbsp;
    result = Regex("""&[a-zA-Z]+;""").replace(result, "")

    // удаление html escape последовательностей типа &#39;
    result = Regex("""&#[0-9]+;""").replace(result, "")

    // удаление html escape последовательностей типа &#xB2
    result = Regex("""&#[xX][0-9a-zA-Z]+;""").replace(result, "")

    // удаление повторяющихся пробелов
    result = result.trim().replace(Regex(" +"), " ")
    return result
}