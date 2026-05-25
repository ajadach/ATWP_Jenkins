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
            parallel {
                stage('Run Tests Artur') {
                    steps {
                        bat 'python -m robot --outputdir results/artur Tests/PetStore_Test_Artur.robot'
                    }
                }
                stage('Run Tests Tomek') {
                    steps {
                        bat 'python -m robot --outputdir results/tomek Tests/PetStore_Test_Tomek.robot'
                    }
                }
            }
        }
    }

    post {
        always {
            bat 'python -m robot.rebot --outputdir results --output output.xml --report report.html --log log.html results/artur/output.xml results/tomek/output.xml'
            robot(
                outputPath: 'results',
                outputFileName: 'output.xml',
                reportFileName: 'report.html',
                logFileName: 'log.html',
                passThreshold: 100,
                unstableThreshold: 75
            )
        }
    }
}
