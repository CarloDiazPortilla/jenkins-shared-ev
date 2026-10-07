def call(Map cfg) {
    withCredentials([string(credentialsId: cfg.dbPasswordId ?: 'db-password', variable: 'DB_PASSWORD')]) {
        sshagent(credentials: [cfg.sshCredentialsId ?: 'server-ssh-key']) {
            withEnv(["HOST=${cfg.host}", "IMAGE_TAG=${cfg.tag}"]) {
                sh '''
                    set -e
                    SSH_OPTS="-o StrictHostKeyChecking=no"
                    ssh $SSH_OPTS ubuntu@$HOST "mkdir -p /home/ubuntu/app"
                    printf 'IMAGE_TAG=%s\\nDB_PASSWORD=%s\\n' "$IMAGE_TAG" "$DB_PASSWORD" > .env
                    chmod 600 .env
                    scp $SSH_OPTS docker-compose.yml .env ubuntu@$HOST:/home/ubuntu/app/
                    rm -f .env
                    ssh $SSH_OPTS ubuntu@$HOST "cd /home/ubuntu/app && docker compose --profile backup pull && docker compose up -d"
                '''
            }
        }
    }
}