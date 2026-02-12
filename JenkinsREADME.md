# Fullstack Docker CI/CD with Jenkins (Spring Boot + Streamlit)

Jenkins builds BOTH backend + frontend Docker images and runs them so you can open the Streamlit UI in the browser.

Assumptions:
- Jenkins already installed on an EC2 / VM (Ubuntu)
- Repository name: **Java-springboot-project-docker-jenkins**

---

## Architecture Overview

GitHub → Jenkins → Docker Build → Multi-Container → Docker Network → Live UI

Backend: Spring Boot (port 8084)  
Frontend: Streamlit (port 8501)

Frontend communicates with backend using Docker network hostname.

---

## STEP 0 — Fix Frontend (VERY IMPORTANT)

Open:

    frontend/Dockerfile

Change:

    ENV API_URL=http://172.31.27.229:8082

Replace with:

    ENV API_URL=http://backend:8084

Commit and push to GitHub.

Reason: Containers must communicate via container name inside Docker network, not EC2 IP.

---

## STEP 1 — Install Docker on Jenkins Server

SSH into Jenkins machine:

    sudo apt update
    sudo apt install docker.io -y

Give Jenkins permission:

    sudo usermod -aG docker jenkins
    sudo systemctl restart docker
    sudo systemctl restart jenkins

Verify:

    sudo su - jenkins
    docker ps

If no permission error → success.

---

## STEP 2 — Create Jenkins Pipeline Job

1. Open Jenkins UI
2. New Item
3. Name: fullstack-docker
4. Select: Pipeline
5. Click OK

---

## STEP 3 — Configure Git Repo

Pipeline → Definition → Pipeline script from SCM

SCM:

    Git

Repository URL:

    https://github.com/bhawnavishwakarma007/Java-springboot-project-docker-jenkins.git

Branch:

    */main

Script Path:

    Jenkinsfile

Save (do not build yet)

---

## STEP 4 — Create Jenkinsfile in Project Root

Create file:

    Jenkinsfile

Paste:

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

Commit and push.

---

## STEP 5 — Run Pipeline

Go to Jenkins:

    fullstack-docker → Build Now

First build may take longer because Maven dependencies download.

---

## STEP 6 — Verify Containers

SSH into server:

    docker ps

Expected:

    backend     0.0.0.0:8084->8084
    frontend    0.0.0.0:8501->8501

---

## STEP 7 — Open Application

Frontend UI:

    http://<EC2-PUBLIC-IP>:8501

Backend API:

    http://<EC2-PUBLIC-IP>:8084

---

## STEP 8 — If Page Not Loading (AWS Security Group)

Add inbound rules:

| Type        | Port |
|------------|----|
| Custom TCP | 8084 |
| Custom TCP | 8501 |

---

## What You Achieved

You built a real DevOps flow:

GitHub → Jenkins → Docker Build → Multi-Container → Network → Live UI

This represents CI/CD for microservices without Kubernetes.
