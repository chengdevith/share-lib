def call(Map config = [:]) {
    pipeline {
        agent {
            label config.agentLabel ?: 'trivy'
        }

        environment {
            IMAGE_NAME = config.imageName ?: 'jobservice'
            IMAGE_TAG  = config.imageTag ?: "${env.BUILD_NUMBER}"
            FULL_IMAGE = "${IMAGE_NAME}:${IMAGE_TAG}"

            TRIVY_PATH = config.trivyPath ?: '/home/enz/trivy/docker-compose.yml'
            REPORT_PATH = config.reportPath ?: '/home/enz/trivy/reports/trivy-report.json'

            DEFECTDOJO_URL = config.defectdojoUrl ?: 'https://defectdojo.devith.it.com'
            DEFECTDOJO_API_KEY = credentials(config.defectdojoCredentialId ?: 'DEFECTDOJO')

            GIT_URL = config.gitUrl ?: ''
            GIT_BRANCH = config.gitBranch ?: 'main'

            PRODUCT_TYPE_NAME = config.productTypeName ?: 'Web Applications'
            PRODUCT_NAME = config.productName ?: 'JobFinder'
            ENGAGEMENT_NAME = config.engagementName ?: 'Jenkins-CI'
            TEST_TITLE = config.testTitle ?: 'Trivy Image Scan'
        }

        stages {
            stage('Checkout') {
                steps {
                    git branch: "${GIT_BRANCH}", url: "${GIT_URL}"
                }
            }

            stage('Build Docker Image') {
                steps {
                    buildDocker()
                }
            }

            stage('Trivy Scan') {
                steps {
                    trivyScan()
                }
            }

            stage('Upload to DefectDojo') {
                steps {
                    uploadDefectDojo()
                }
            }

            stage('Security Gate') {
                steps {
                    securityGate(
                        severity: config.gateSeverity ?: 'HIGH,CRITICAL'
                    )
                }
            }
        }

        post {
            always {
                sh '''
                    cp ${REPORT_PATH} ./trivy-report.json || true
                '''
                archiveArtifacts artifacts: 'trivy-report.json', fingerprint: true, allowEmptyArchive: true
            }
        }
    }
}