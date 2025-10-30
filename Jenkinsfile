// Jenkinsfile (Yakuniy va To'g'ri Versiya)

pipeline {
    agent any

    tools {
        maven 'maven'
        jdk 'JDK21'
    }

    environment {
        IMAGE_NAME = "kundalik/app:${env.BUILD_NUMBER}"
        LATEST_IMAGE = "kundalik/app:latest"
        APP_CONTAINER_NAME = 'kundalik-container'
        // Monitoring servislarining umumiy loyiha nomi
        COMPOSE_PROJECT_NAME = 'kundalik_monitoring'
    }

    stages {
        stage('1. Clone Repository') {
            steps {
                cleanWs()
                echo 'Git repozitoriydan kod olinmoqda...'
                git url: 'https://github.com/AbdulbositAbdurahimovDeveloper/KUNDALIK.git', branch: 'main'
                echo 'Kod muvaffaqiyatli olindi.'
            }
        }

        // === 2-BOSQICH: Monitoringni Ishga Tushirish/Yangilash ===
       stage('2. Start/Update Monitoring Services') {
           steps {
               echo "Monitoring servislarini (Prometheus, Grafana, Loki) ishga tushirish..."
               // -p o'rniga COMPOSE_PROJECT_NAME muhit o'zgaruvchisini ishlatamiz
               sh "COMPOSE_PROJECT_NAME=${COMPOSE_PROJECT_NAME} docker compose up --build -d --remove-orphans"
               echo "Monitoring servislar muvaffaqiyatli ishga tushirildi."
           }
       }

        stage('3. Build Application Image') {
            steps {
                echo "Ilova uchun Docker image qurilmoqda: ${IMAGE_NAME}"
                sh "docker build -t ${IMAGE_NAME} -t ${LATEST_IMAGE} ."
                echo "Docker image muvaffaqiyatli qurildi."
            }
        }

        stage('4. Deploy Application') {
            steps {
                echo "Eski ilova konteyneri o'chirilmoqda..."
                sh "docker rm -f ${APP_CONTAINER_NAME} || true"

                echo "Ilova konteyneri ishga tushirilmoqda..."
                withCredentials([
                    string(credentialsId: 'KUNDALIK_DB_USERNAME', variable: 'DB_USERNAME'),
                    string(credentialsId: 'KUNDALIK_DB_PASSWORD', variable: 'DB_PASSWORD'),
                    string(credentialsId: 'KUNDALIK_REDIS_PASSWORD', variable: 'KUNDALIK_REDIS_PASSWORD'),
                    string(credentialsId: 'KUNDALIK_MAIL_USERNAME', variable: 'KUNDALIK_MAIL_USERNAME'),
                    string(credentialsId: 'KUNDALIK_MAIL_PASSWORD', variable: 'KUNDALIK_MAIL_PASSWORD'),
                    string(credentialsId: 'KUNDALIK_JWT_SECRET_KEY', variable: 'KUNDALIK_JWT_SECRET_KEY'),
                    string(credentialsId: 'KUNDALIK_MINIO_ACCESS_KEY', variable: 'KUNDALIK_MINIO_ACCESS_KEY'),
                    string(credentialsId: 'KUNDALIK_MINIO_SECRET_KEY', variable: 'KUNDALIK_MINIO_SECRET_KEY'),
                    string(credentialsId: 'KUNDALIK_TELEGRAM_BOT_TOKEN', variable: 'KUNDALIK_TELEGRAM_BOT_TOKEN'),
                    string(credentialsId: 'KUNDALIK_ESKIZ_TOKEN', variable: 'KUNDALIK_ESKIZ_TOKEN'),
                    string(credentialsId: 'KUNDALIK_WEATHER_API_KEY_1', variable: 'KUNDALIK_WEATHER_API_KEY_1'),
                    string(credentialsId: 'KUNDALIK_WEATHER_API_KEY_2', variable: 'KUNDALIK_WEATHER_API_KEY_2'),
                    string(credentialsId: 'KUNDALIK_ESKIZ_EMAIL', variable: 'KUNDALIK_ESKIZ_EMAIL')
                ]) {
                   sh """
                       docker run -d \\
                         --name "${APP_CONTAINER_NAME}" \\
                         -p 9999:8080 \\
                         -p 9095:9095 \\
                         --network app-network \\
                         --restart unless-stopped \\
                         -e DB_USERNAME="${DB_USERNAME}" \\
                         -e DB_PASSWORD="${DB_PASSWORD}" \\
                         -e KUNDALIK_SERVER_IP=redis \\
                         -e KUNDALIK_REDIS_PORT=6379 \\
                         -e KUNDALIK_REDIS_PASSWORD="${KUNDALIK_REDIS_PASSWORD}" \\
                         -e KUNDALIK_MAIL_USERNAME="${KUNDALIK_MAIL_USERNAME}" \\
                         -e KUNDALIK_MAIL_PASSWORD="${KUNDALIK_MAIL_PASSWORD}" \\
                         -e KUNDALIK_JWT_SECRET_KEY="${KUNDALIK_JWT_SECRET_KEY}" \\
                         -e KUNDALIK_ACCESS_TOKEN_EXPIRATION=86400000 \\
                         -e KUNDALIK_REFRESH_TOKEN_EXPIRATION=604800000 \\
                         -e KUNDALIK_MINION_ENDPOINT=http://minio:9000 \\
                         -e KUNDALIK_MINIO_ACCESS_KEY="${KUNDALIK_MINIO_ACCESS_KEY}" \\
                         -e KUNDALIK_MINIO_SECRET_KEY="${KUNDALIK_MINIO_SECRET_KEY}" \\
                         -e KUNDALIK_TELEGRAM_BOT_TOKEN="${KUNDALIK_TELEGRAM_BOT_TOKEN}" \\
                         -e KUNDALIK_TELEGRAM_BOT_USERNAME=my_kundalik_bot \\
                         -e KUNDALIK_TELEGRAM_BOT_PATH=https://abdulbosit.uz \\
                         -e KUNDALIK_TELEGRAM_CHAT_ID=5902268851 \\
                         -e KUNDALIK_TELEGRAM_BOT_CHANNEL=-1002895768675 \\
                         -e KUNDALIK_ESKIZ_URL=https://notify.eskiz.uz/api \\
                         -e KUNDALIK_ESKIZ_EMAIL="${KUNDALIK_ESKIZ_EMAIL}" \\
                         -e KUNDALIK_ESKIZ_TOKEN="${KUNDALIK_ESKIZ_TOKEN}" \\
                         -e "KUNDALIK_ESKIZ_SMS_TEXT=Tasdiqlash kodi: " \\
                         -e KUNDALIK_WEATHER_API_KEY_1="${KUNDALIK_WEATHER_API_KEY_1}" \\
                         -e KUNDALIK_WEATHER_API_KEY_2="${KUNDALIK_WEATHER_API_KEY_2}" \\
                         -e KUNDALIK_WEATHER_DEFAULT_DAYS=7 \\
                         -e KUNDALIK_WEATHER_DEFAULT_CITY=Tashkent \\
                         -e KUNDALIK_PRAYER_DEFAULT_CITY=Toshkent \\
                         -e KUNDALIK_BACKEND_BASE_URL=http://213.199.55.47:9999 \\
                         -e KUNDALIK_FRONTEND_BASE_URL=http://213.199.55.47:3000 \\
                         "${LATEST_IMAGE}"
                   """
                }
                echo "Ilova http://213.199.55.47:9999 manzilida muvaffaqiyatli ishga tushirildi."
            }
        }
    }

    post {
        always {
            echo 'Pipeline tugadi. Ish joyi tozalanmoqda...'
            cleanWs()
        }
        success {
            echo 'Pipeline muvaffaqiyatli yakunlandi!'
        }
        failure {
            echo 'Pipeline xatolik bilan yakunlandi!'
        }
    }
}