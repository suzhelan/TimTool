dependencyResolutionManagement {
    // 引入Catalogs
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}