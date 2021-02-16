package ru.skillbranch.devintensive.utils


object Utils {

    fun parseFullName(fullName: String?): Pair<String?, String?> {
        val fullNameTrim = fullName?.replace("  ", " ")?.trim()
        if (fullNameTrim.isNullOrBlank())
            return null to null
        val parts: List<String>? = fullNameTrim.trim().split(" ")
        val firstName = parts?.getOrNull(0)
        val lastName = parts?.getOrNull(1)

        return firstName to lastName // Pair(firstName, lastName)
    }

    fun transliteration(payload: String, divider: String = " "): String {
        val rules: Map<String, String> = mapOf(
            "а" to "a", "б" to "b", "в" to "v", "г" to "g", "д" to "d", "е" to "e", "ё" to "e",
            "ж" to "zh", "з" to "z", "и" to "i", "й" to "i", "к" to "k", "л" to "l", "м" to "m",
            "н" to "n", "о" to "o", "п" to "p", "р" to "r", "с" to "s", "т" to "t", "у" to "u",
            "ф" to "f", "х" to "h", "ц" to "c", "ч" to "ch", "ш" to "sh", "щ" to "sh'", "ъ" to "",
            "ы" to "i", "ь" to "", "э" to "e", "ю" to "yu", "я" to "ya"
        )

        val payloadTrim = payload.trim()
        var result = ""
        for (letter in payloadTrim) {
            if (rules.contains(letter.toLowerCase().toString())) {
                if (letter.isUpperCase()) {
                    // make first letter is upper case
                    val firstUpperCase =
                        rules[letter.toLowerCase().toString()]?.getOrNull(0)?.toUpperCase()
                    if (firstUpperCase !== null) {
                        val value = rules[letter.toLowerCase().toString()]?.substring(1).toString()
                        result += firstUpperCase + value
                    }
                } else {
                    result += rules[letter.toString()]
                }
            } else {
                result += letter
            }
        }

        if (divider != " ") {
            result = result.replace(" ", divider)
        }
        return result
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        if (firstName.isNullOrBlank() && lastName.isNullOrBlank())
            return null

        val firstNameTrim = firstName?.trim()
        val lastNameTrim = lastName?.trim()
        var initials = ""
        if (!firstNameTrim.isNullOrBlank())
            initials = firstNameTrim.getOrNull(0)?.toUpperCase().toString()
        if (!lastNameTrim.isNullOrBlank())
            initials += lastNameTrim.getOrNull(0)?.toUpperCase().toString()

        return initials
    }

    fun isValidRepositoryUrl(url: String): Boolean {
        if (url.isEmpty()) return true

        var urlCheck = url
        val regexDomain =
            Regex("""(github\.com/|https://github\.com/|www\.github\.com/|https://www\.github\.com/)""")
        val isDomain = regexDomain.containsMatchIn(urlCheck)
        if (!isDomain) return false

        urlCheck = regexDomain.replaceFirst(urlCheck, "")
        if (urlCheck.isEmpty()) return false

        val isUserName = Regex("""^([a-zA-Z0-9]+-?[a-zA-Z0-9]+)""").containsMatchIn(urlCheck)
        if (!isUserName) return false

        val isException =
            Regex("""(enterprise|features|topics|collections|trending|events|marketplace|pricing|nonprofit|customer-stories|security|login|join)""").matches(
                urlCheck
            )
        if (isException) return false

        urlCheck = Regex("""[a-zA-Z0-9]+-?[a-zA-Z0-9]+""").replaceFirst(urlCheck, "")

        return Regex("""/""").replaceFirst(urlCheck, "").isEmpty()
    }
}
