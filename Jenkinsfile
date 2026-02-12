pipeline {
    agent any

    environment {
        BACKEND_IMAGE = "springboot-backend"
        FRONTEND_IMAGE = "streamlit-frontend"
        NETWORK = "app-network"
    }

    stages {

        stage('Build Backend Image') {
            steps {
                dir('backend') {
                    sh 'docker build -t $BACKEND_IMAGE .'
                }
            }
        }

        stage('Build Frontend Image') {
            steps {
                dir('frontend') {
                    sh 'docker build -t $FRONTEND_IMAGE .'
                }
            }
        }

        stage('Create Network') {
            steps {
                sh 'docker network create $NETWORK || true'
            }
        }

        stage('Run Backend Container') {
            steps {
                sh '''
                docker stop backend || true
                docker rm backend || true

                docker run -d \
                --name backend \
                --network $NETWORK \
                -p 8084:8084 \
                $BACKEND_IMAGE
                '''
            }
        }

        stage('Run Frontend Container') {
            steps {
                sh '''
                docker stop frontend || true
                docker rm frontend || true

                docker run -d \
                --name frontend \
                --network $NETWORK \
                -p 8501:8501 \
                $FRONTEND_IMAGE
                '''
            }
        }
    }
}
