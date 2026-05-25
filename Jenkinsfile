pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'Pobieranie kodu...'
            }
        }

        stage('Build') {
            steps {
                echo 'Budowanie projektu...'
            }
        }

        stage('Test') {
            steps {
                echo 'Uruchamianie testów...'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Wdrażanie aplikacji...'
            }
        }
    }

    post {
        success {
            echo 'Pipeline zakończony sukcesem!'
        }
        failure {
            echo 'Pipeline zakończony błędem!'
        }
    }
}
