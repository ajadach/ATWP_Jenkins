// Jenkinsfile definiuje cały pipeline CI/CD dla projektu ATWP_Jenkins.
// Jenkins czyta ten plik z repozytorium i wykonuje zdefiniowane etapy.
pipeline {
    // agent any - Jenkins może użyć dowolnego dostępnego węzła/executora do uruchomienia pipeline
    agent any
    // Uruchom na nodzie z labelem "windows-tester"
    // agent { label 'windows-tester' }

    // triggers - definiuje kiedy pipeline ma się automatycznie uruchamiać
    triggers {
        // Trigger 1: uruchamia pipeline co godzinę
        cron('H * * * *')

        // Trigger 2: uruchamia pipeline po każdym commit/push do GitHub
        // Aby działał, wymagane jest:
        //   1. Zainstalowany plugin "GitHub" w Jenkins (Manage Jenkins -> Plugins)
        //   2. Webhook w repozytorium GitHub:
        //      - GitHub repo -> Settings -> Webhooks -> Add webhook
        //      - Payload URL: http://<PUBLICZNY_IP_LUB_NGROK>:8080/github-webhook/
        //      - Content type: application/json
        //      - Which events: Just the push event
        //   3. Jenkins musi być dostępny publicznie (np. przez ngrok: `ngrok http 8080`)
        //      bez tego GitHub nie dotrze do lokalnego Jenkins na localhost
        //
        // Gdy webhook jest skonfigurowany - odkomentuj poniższą linię:
        // githubPush()
    }

    /* ============================================================
       PRZYKŁAD: pełny blok triggers gdy webhook jest skonfigurowany
       (zamień aktualny blok triggers na poniższy)
    
    triggers {
        cron('H * * * *')
        githubPush()
    }
    ============================================================ */

    // stages - główna sekcja pipeline, zawiera kolejne etapy wykonywane jeden po drugim
    stages {

        // Etap 1: Pobieranie kodu źródłowego z repozytorium Git
        // checkout scm używa konfiguracji SCM zdefiniowanej w samym jobie Jenkins
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // Etap 2: Instalacja zależności Pythona z pliku requirements.txt
        // Dzięki temu środowisko zawsze ma aktualne biblioteki wymagane przez testy
        stage('Install Dependencies') {
            steps {
                bat 'pip install -r requirements.txt'
            }
        }

        // Etap 3: Uruchomienie testów Robot Framework
        // parallel - oba zestawy testów uruchamiają się równocześnie, co skraca czas wykonania
        stage('Run Tests') {
            parallel {

                // Testy napisane przez Artura - wyniki trafiają do katalogu results/artur
                stage('Run Tests Artur') {
                    steps {
                        bat 'python -m robot --outputdir results/artur Tests/PetStore_Test_Artur.robot'
                    }
                }

                // Testy napisane przez Tomka - wyniki trafiają do katalogu results/tomek
                stage('Run Tests Tomek') {
                    steps {
                        bat 'python -m robot --outputdir results/tomek Tests/PetStore_Test_Tomek.robot'
                    }
                }
            }
        }
    }

    // post - akcje wykonywane po zakończeniu wszystkich stages, niezależnie od wyniku
    post {
        // always - wykonuje się zawsze, nawet gdy testy nie przeszły
        always {
            // rebot scala wyniki z obu równoległych zestawów testów w jeden zbiorczy raport
            bat 'python -m robot.rebot --outputdir results --output output.xml --report report.html --log log.html results/artur/output.xml results/tomek/output.xml'

            // robot - plugin Robot Framework publikuje wyniki w UI Jenkinsa:
            // - zakładka "Robot Results" z wykresem trendu pass/fail
            // - linki do report.html i log.html
            // - passThreshold: build SUCCESS gdy 100% testów przeszło
            // - unstableThreshold: build UNSTABLE gdy mniej niż 75% testów przeszło
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
