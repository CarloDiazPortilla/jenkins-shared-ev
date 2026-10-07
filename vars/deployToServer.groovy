def call(Map cfg) {
    withCredentials([string(credentialsId: cfg.dbPasswordId ?: 'db-password', variable: 'DB_PASSWORD')]) {
        sshagent(credentials: [cfg.sshCredentialsId ?: 'server-ssh-key']) {
            withEnv(["HOST=${cfg.host}", "IMAGE_TAG=${cfg.tag}", "COMPOSE_FILE=${cfg.composeFile ?: 'docker-compose.yml'}"]) {
                sh '''
                    set -e
                    SSH_OPTS="-o StrictHostKeyChecking=no"
                    ssh $SSH_OPTS ubuntu@$HOST "mkdir -p /home/ubuntu/app/db"
                    printf 'IMAGE_TAG=%s\\nDB_PASSWORD=%s\\n' "$IMAGE_TAG" "$DB_PASSWORD" > .env
                    chmod 600 .env
                    scp $SSH_OPTS "$COMPOSE_FILE" ubuntu@$HOST:/home/ubuntu/app/docker-compose.yml
                    scp $SSH_OPTS db/init.sql ubuntu@$HOST:/home/ubuntu/app/db/init.sql
                    scp $SSH_OPTS .env ubuntu@$HOST:/home/ubuntu/app/.env
                    rm -f .env
                    ssh $SSH_OPTS ubuntu@$HOST "cd /home/ubuntu/app && docker compose pull && docker compose --profile backup pull && docker compose up -d"
                '''
            }
        }
    }
}