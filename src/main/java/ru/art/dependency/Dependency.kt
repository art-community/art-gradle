package ru.art.dependency

data class Dependency(val group: String,
                      val artifact: String,
                      val version: String? = null,
                      var exclusions: Set<Dependency> = setOf()) {
    fun inGradleNotation(): String {
        version ?: return "$group:$artifact"
        return if (version.isEmpty()) "$group:$artifact" else "$group:$artifact:$version"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Dependency

        if (group != other.group) return false
        if (artifact != other.artifact) return false

        return true
    }

    override fun hashCode(): Int {
        var result = group.hashCode()
        result = 31 * result + artifact.hashCode()
        return result
    }


}