mapOf(
    "coreKtxVersion" to "1.8.0", //1.8.0
    "appCompatVersion" to "1.4.2",
    "materialVersion" to "1.6.1",
    "junitVersion" to "4.13.2",
    "testJunitVersion" to "1.1.3",
    "espressoCoreVersion" to "3.4.0",
    "composeVersion" to "1.2.0-rc01",
    "activityCompose" to "1.4.0",
    "activityKtxVersion" to "1.4.0",
    "lifecycleRuntimeKtxVersion" to "2.4.1",
    "hiltVersion" to "2.42",
    "navVersion" to "2.4.2",
    "constraintLayoutVersion" to "2.1.4",
    "appAuthVersion" to "0.11.1",
    "androidXHiltVersion" to "1.0.0",
    "jwtDecodeVersion" to "2.0.0",
    "workerVersion" to "2.7.1",
    "roomVersion" to "2.4.2",
    "retrofitVersion" to "2.9.0",
    "okHTTP3LoggingVersion" to "4.10.0"
).forEach { (key, value) ->
    project.extra.set(key, value)
}