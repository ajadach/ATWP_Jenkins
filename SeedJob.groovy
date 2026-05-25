// SeedJob.groovy - skrypt Job DSL
// ============================================================
// Czym jest Seed Job?
// Seed Job to specjalny job w Jenkinsie, który uruchamia ten skrypt
// i automatycznie tworzy/aktualizuje inne joby na podstawie kodu poniżej.
// Dzięki temu konfiguracja jobów jest przechowywana w repozytorium Git
// zamiast ręcznie klikana w UI Jenkinsa.
//
// Jak uruchomić Seed Job?
//   1. W Jenkins: New Item -> nazwa np. "Seed_Job" -> typ "Freestyle project"
//   2. W sekcji "Source Code Management" ustaw to samo repo Git
//   3. W sekcji "Build Steps" dodaj krok "Process Job DSLs"
//   4. W polu "DSL Scripts" wpisz: SeedJob.groovy
//   5. Save -> Build Now
//
// Wymagane pluginy:
//   - Job DSL
//   - MultiJob Plugin
//   - Robot Framework Plugin
// ============================================================

// ============================================================
// Job 1: Testy Artura
// Zwykły Freestyle job - pobiera kod, instaluje zależności, uruchamia testy
// ============================================================
freeStyleJob('ATWP_Tests_Artur') {
    description('Testy Robot Framework - Artur')

    // Pobranie kodu z repozytorium GitHub
    scm {
        git {
            remote {
                url('https://github.com/ajadach/ATWP_Jenkins.git')
            }
            branch('*/main')
        }
    }

    // Kroki budowania wykonywane kolejno
    steps {
        // Instalacja zależności Pythona
        batchFile('pip install -r requirements.txt')
        // Uruchomienie testów Artura
        batchFile('python -m robot --outputdir results/artur Tests/PetStore_Test_Artur.robot')
    }

    // Publikacja wyników Robot Framework po zakończeniu kroków
    // Używamy bloku configure bo Robot Framework plugin nie eksponuje DSL API
    configure { project ->
        project / 'publishers' << 'hudson.plugins.robot.RobotPublisher' {
            outputPath('results/artur')
            outputFileName('output.xml')
            reportFileName('report.html')
            logFileName('log.html')
            passThreshold(100.0)
            unstableThreshold(75.0)
            onlyCritical(true)
            disableArchiveOutput(false)
            enableCache(false)
        }
    }
}

// ============================================================
// Job 2: Testy Tomka
// Zwykły Freestyle job - pobiera kod, instaluje zależności, uruchamia testy
// ============================================================
freeStyleJob('ATWP_Tests_Tomek') {
    description('Testy Robot Framework - Tomek')

    // Pobranie kodu z repozytorium GitHub
    scm {
        git {
            remote {
                url('https://github.com/ajadach/ATWP_Jenkins.git')
            }
            branch('*/main')
        }
    }

    // Kroki budowania wykonywane kolejno
    steps {
        // Instalacja zależności Pythona
        batchFile('pip install -r requirements.txt')
        // Uruchomienie testów Tomka
        batchFile('python -m robot --outputdir results/tomek Tests/PetStore_Test_Tomek.robot')
    }

    // Publikacja wyników Robot Framework po zakończeniu kroków
    // Używamy bloku configure bo Robot Framework plugin nie eksponuje DSL API
    configure { project ->
        project / 'publishers' << 'hudson.plugins.robot.RobotPublisher' {
            outputPath('results/tomek')
            outputFileName('output.xml')
            reportFileName('report.html')
            logFileName('log.html')
            passThreshold(100.0)
            unstableThreshold(75.0)
            onlyCritical(true)
            disableArchiveOutput(false)
            enableCache(false)
        }
    }
}

// ============================================================
// Job 3: Pipeline orkiestrator
// Uruchamia testy Artura i Tomka równolegle jako osobne joby.
// Używa 'build job:' zamiast MultiJob - działa z pipelineJob.
// ============================================================
pipelineJob('ATWP_Jenkins_MultiJob') {
    description('Orkiestrator - uruchamia testy Artura i Tomka równolegle')

    triggers {
        // Uruchamia co godzinę
        cron('H * * * *')

        // Odkomentuj gdy webhook jest gotowy:
        // githubPush()
    }

    definition {
        cps {
            script('''
                pipeline {
                    agent none

                    stages {
                        // Faza równoległa - uruchamia joby Artura i Tomka jednocześnie
                        stage('Run All Tests In Parallel') {
                            parallel {
                                // Uruchamia osobny job ATWP_Tests_Artur
                                stage('Tests Artur') {
                                    steps {
                                        build job: 'ATWP_Tests_Artur', wait: true
                                    }
                                }
                                // Uruchamia osobny job ATWP_Tests_Tomek
                                stage('Tests Tomek') {
                                    steps {
                                        build job: 'ATWP_Tests_Tomek', wait: true
                                    }
                                }
                            }
                        }
                    }
                }
            ''')
            sandbox(false)
        }
    }
}
