pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'maven clean compile'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('package') {
            steps {
                echo 'mvn package'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}