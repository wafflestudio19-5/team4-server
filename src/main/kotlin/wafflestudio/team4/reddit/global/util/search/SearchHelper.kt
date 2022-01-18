package wafflestudio.team4.reddit.global.util.search

object SearchHelper {
    fun makeAbbreviationPattern(keyword: String): String {
        val splitWord = keyword.split("").filterNot { it == "" }
        return splitWord.joinToString("%", "%", "%")
    }
}
