// Jenkinsfile definiuje cały pipeline CI/CD dla projektu ATWP_Jenkins.
// Jenkins czyta ten plik z repozytorium i wykonuje zdefiniowane etapy.
pipeline {
    // agent any - Jenkins może użyć dowolnego dostępnego węzła/executora do uruchomienia pipeline
    agent any
    // Uruchom na nodzie z labelem "windows-tester"
    // agent { label 'windows-tester' }

    parameters {
        string(name: 'tag', defaultValue: 'all', description: 'Tag testów do uruchomienia')
    }

    // triggers - definiuje kiedy pipeline ma się automatycznie uruchamiać
    triggers {
        // Trigger 1: uruchamia pipeline co godzinę
        // Składnia cron: minuta godzina dzień_miesiąca miesiąc dzień_tygodnia
        //   H * * * *  - H (hash) = Jenkins sam dobiera minutę w każdej godzinie
        //                * * * *  - każda godzina, każdy dzień, każdy miesiąc, każdy dzień tygodnia
        // Przykłady innych wartości:
        //   H/15 * * * *   - co 15 minut
        //   H 8 * * 1-5    - raz dziennie o 8:xx, tylko w dni robocze (pon-pt)
        //   H 0 * * *      - raz dziennie o północy
        //   H 8,20 * * *   - dwa razy dziennie: o 8:xx i 20:xx
        //   H * * * 1      - co godzinę, ale tylko w poniedziałki
        // Uwaga: H zamiast konkretnej minuty rozkłada obciążenie wielu jobów w czasie
        cron('H 8 * * *')

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
                    options {
                        // timeout - przerywa stage jeśli trwa dłużej niż 5 minut
                        timeout(time: 5, unit: 'MINUTES')
                    }
                    steps {
                        bat "python -m robot --outputdir results/artur --include ${params.tag} Tests/PetStore_Test_Artur.robot"
                    }
                }

                // Testy napisane przez Tomka - wyniki trafiają do katalogu results/tomek
                stage('Run Tests Tomek') {
                    options {
                        // timeout - przerywa stage jeśli trwa dłużej niż 5 minut
                        timeout(time: 5, unit: 'MINUTES')
                    }
                    steps {
                        bat "python -m robot --outputdir results/tomek --include ${params.tag} Tests/PetStore_Test_Tomek.robot"
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
