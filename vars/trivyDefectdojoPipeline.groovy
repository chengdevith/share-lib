def call(Map config = [:]) {

    def imageName = config.imageName ?: 'jobservice'
    def imageTag  = config.imageTag ?: "${env.BUILD_NUMBER}"
    def fullImage = "${imageName}:${imageTag}"

    def trivyPath = config.trivyPath ?: '/home/istad/trivy/docker-compose.yml'
    def reportPath = config.reportPath ?: '/home/istad/trivy/reports/trivy-report.json'

    def defectdojoUrl = config.defectdojoUrl ?: 'https://defetchdojo.anajak-khmer.site'
    def defectdojoCred = config.defectdojoCredentialId ?: 'DEFECTDOJO'

    def gitUrl = config.gitUrl ?: ''
    def gitBranch = config.gitBranch ?: 'main'

    def productType = config.productTypeName ?: 'Web Applications'
    def productName = config.productName ?: 'JobFinder'
    def engagementName = config.engagementName ?: 'Jenkins-CI'
    def testTitle = config.testTitle ?: 'Trivy Image Scan'

    def gateSeverity = config.gateSeverity ?: 'HIGH,CRITICAL'

    pipeline {
        agent {
            label config.agentLabel ?: 'trivy'
        }

        environment {
            IMAGE_NAME = "${imageName}"
            IMAGE_TAG  = "${imageTag}"
            FULL_IMAGE = "${fullImage}"

            TRIVY_PATH = "${trivyPath}"
            REPORT_PATH = "${reportPath}"

            DEFECTDOJO_URL = "${defectdojoUrl}"
            DEFECTDOJO_API_KEY = credentials("${defectdojoCred}")

            GIT_URL = "${gitUrl}"
            GIT_BRANCH = "${gitBranch}"

            PRODUCT_TYPE_NAME = "${productType}"
            PRODUCT_NAME = "${productName}"
            ENGAGEMENT_NAME = "${engagementName}"
            TEST_TITLE = "${testTitle}"
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
                    securityGate(severity: gateSeverity)
                }
            }
        }

        post {
            always {
                sh 'cp ${REPORT_PATH} ./trivy-report.json || true'
                archiveArtifacts artifacts: 'trivy-report.json', fingerprint: true, allowEmptyArchive: true
            }
        }
    }
}