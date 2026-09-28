pipeline {
    agent any
    
    environment {
        // Tên image trên Docker Hub của bạn
        DOCKER_IMAGE = 'phantranxuanhuy/graduation-management'
        // ID của chứng chỉ bảo mật trên Jenkins
        DOCKER_CREDENTIALS_ID = 'dockerhub-cred'
    }

    stages {
        stage('Build Docker Image') {
            steps {
                // Dockerfile nằm ở thư mục gốc nên build trực tiếp từ đây
                bat 'docker build -t %DOCKER_IMAGE%:latest .'
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                dir('graduation-backend') {
                    // Gọi thông tin đăng nhập Docker Hub từ Jenkins
                    withCredentials([usernamePassword(credentialsId: env.DOCKER_CREDENTIALS_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        bat 'docker login -u %DOCKER_USER% -p %DOCKER_PASS%'
                        bat 'docker push %DOCKER_IMAGE%:latest'
                    }
                }
            }
        }
        
        stage('Deploy Locally') {
            steps {
                dir('graduation-backend') {
                    // Dừng container cũ nếu đang chạy
                    bat 'docker-compose down'
                    // Chạy container mới ngầm (chế độ -d)
                    bat 'docker-compose up -d'
                }
            }
        }
    }
    post {
        always {
            bat 'docker logout'
        }
    }
}