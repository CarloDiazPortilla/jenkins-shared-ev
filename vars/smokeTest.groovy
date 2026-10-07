def call(Map cfg) {
    retry(10) {
        sleep 6
        sh "curl -sf http://${cfg.host}:${cfg.port ?: 3000}${cfg.path ?: '/'} > /dev/null"
    }
}