plugins {
    base
}

private val _group = properties["group"]!!
private val _version = properties["version"]!!.toString()

allprojects {
    group = _group
    version = _version
}
