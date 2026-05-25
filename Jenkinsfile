pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Install Dependencies') {
            steps {
                bat 'pip install -r requirements.txt'
            }
        }

        stage('Run Tests') {
            steps {
                bat 'python -m robot --outputdir results Tests/'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'results/report.html, results/log.html, results/output.xml', allowEmptyArchive: true
        }
        success {
            echo 'Wszystkie testy przeszły!'
        }
        failure {
            echo 'Niektóre testy nie przeszły!'
        }
    }
}
