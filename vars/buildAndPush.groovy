def call(Map cfg) {
    def tag = cfg.tag ?: env.BUILD_NUMBER
    withCredentials([usernamePassword(
            credentialsId: cfg.credentialsId ?: 'dockerhub-creds',
            usernameVariable: 'DH_USER',
            passwordVariable: 'DH_PASS')]) {
        sh 'echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin'
        sh "docker build --platform linux/amd64 -t ${cfg.image}:${tag} -t ${cfg.image}:latest ."
        sh "docker push ${cfg.image}:${tag}"
        sh "docker push ${cfg.image}:latest"
    }
    return tag
}