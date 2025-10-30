// Jenkinsfile (Monitoring tizimi alohida ishlab turganda, TO'G'RI PORTLAR bilan)

pipeline {
    agent any

    tools {
        maven 'maven' // Jenkins sozlamalaridagi Maven nomi
        jdk 'JDK21'   // Jenkins sozlamalaridagi JDK nomi
    }

    environment {
        IMAGE_NAME = "kundalik/app:${env.BUILD_NUMBER}"
        LATEST_IMAGE = "kundalik/app:latest"
        CONTAINER_NAME = 'kundalik-container'
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

        stage('2. Build Application Image') {
            steps {
                echo "Ilova uchun Docker image qurilmoqda: ${IMAGE_NAME}"
                sh "docker build -t ${IMAGE_NAME} -t ${LATEST_IMAGE} ."
                echo "Docker image muvaffaqiyatli qurildi: ${IMAGE_NAME} va ${LATEST_IMAGE}"
            }
        }

        stage('3. Deploy Application') {
            steps {
                echo "Eski ilova konteyneri o'chirilmoqda (agar mavjud bo'lsa)..."
                sh "docker rm -f ${CONTAINER_NAME} || true"

                echo "Ilova konteyneri ishga tushirilmoqda..."
                withCredentials([
                    // ... sizning barcha credentials'laringiz ...
                ]) {
                   // Uchlik qo'shtirnoq (""") o'rniga uchlik bittalik tirnoq (''') ishlatamiz
                   sh '''
                       docker run -d \\
                         --name "''' + CONTAINER_NAME + '''" \\
                         -p 9999:8080 \\
                         -p 4000:4000 \\
                         --network app-network \\
                         --restart unless-stopped \\
                         -e DB_USERNAME="''' + DB_USERNAME + '''" \\
                         -e DB_PASSWORD="''' + DB_PASSWORD + '''" \\
                         -e KUNDALIK_SERVER_IP=redis \\
                         -e KUNDALIK_REDIS_PORT=6379 \\
                         -e KUNDALIK_REDIS_PASSWORD="''' + KUNDALIK_REDIS_PASSWORD + '''" \\
                         -e KUNDALIK_MAIL_USERNAME="''' + KUNDALIK_MAIL_USERNAME + '''" \\
                         -e KUNDALIK_MAIL_PASSWORD="''' + KUNDALIK_MAIL_PASSWORD + '''" \\
                         -e KUNDALIK_JWT_SECRET_KEY="''' + KUNDALIK_JWT_SECRET_KEY + '''" \\
                         -e KUNDALIK_ACCESS_TOKEN_EXPIRATION=86400000 \\
                         -e KUNDALIK_REFRESH_TOKEN_EXPIRATION=604800000 \\
                         -e KUNDALIK_MINION_ENDPOINT=http://minio:9000 \\
                         -e KUNDALIK_MINIO_ACCESS_KEY="''' + KUNDALIK_MINIO_ACCESS_KEY + '''" \\
                         -e KUNDALIK_MINIO_SECRET_KEY="''' + KUNDALIK_MINIO_SECRET_KEY + '''" \\
                         -e KUNDALIK_TELEGRAM_BOT_TOKEN="''' + KUNDALIK_TELEGRAM_BOT_TOKEN + '''" \\
                         -e KUNDALIK_TELEGRAM_BOT_USERNAME=my_kundalik_bot \\
                         -e KUNDALIK_TELEGRAM_BOT_PATH=https://abdulbosit.uz \\
                         -e KUNDALIK_TELEGRAM_CHAT_ID=5902268851 \\
                         -e KUNDALIK_TELEGRAM_BOT_CHANNEL=-1002895768675 \\
                         -e KUNDALIK_ESKIZ_URL=https://notify.eskiz.uz/api \\
                         -e KUNDALIK_ESKIZ_EMAIL="''' + KUNDALIK_ESKIZ_EMAIL + '''" \\
                         -e KUNDALIK_ESKIZ_TOKEN="''' + KUNDALIK_ESKIZ_TOKEN + '''" \\
                         -e "KUNDALIK_ESKIZ_SMS_TEXT=Tasdiqlash kodi: " \\
                         -e KUNDALIK_WEATHER_API_KEY_1="''' + KUNDALIK_WEATHER_API_KEY_1 + '''" \\
                         -e KUNDALIK_WEATHER_API_KEY_2="''' + KUNDALIK_WEATHER_API_KEY_2 + '''" \\
                         -e KUNDALIK_WEATHER_DEFAULT_DAYS=7 \\
                         -e KUNDALIK_WEATHER_DEFAULT_CITY=Tashkent \\
                         -e KUNDALIK_PRAYER_DEFAULT_CITY=Toshkent \\
                         -e KUNDALIK_BACKEND_BASE_URL=http://213.199.55.47:9999 \\
                         -e KUNDALIK_FRONTEND_BASE_URL=http://213.199.55.47:3000 \\
                         "''' + LATEST_IMAGE + '''"
                   '''
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