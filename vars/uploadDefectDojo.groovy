def call(Map config = [:]) {
    def defectdojoUrl        = config.defectdojoUrl        ?: error('uploadDefectDojo: defectdojoUrl is required')
    def defectdojoCredentialId = config.defectdojoCredentialId ?: error('uploadDefectDojo: defectdojoCredentialId is required')
    def reportPath           = config.reportPath           ?: '/home/enz/trivy/reports/trivy-report.json'
    def productTypeName      = config.productTypeName      ?: 'Web Applications'
    def productName          = config.productName          ?: error('uploadDefectDojo: productName is required')
    def engagementName       = config.engagementName       ?: 'Jenkins'
    def testTitle            = config.testTitle            ?: 'Trivy Image Scan'

    withCredentials([string(credentialsId: defectdojoCredentialId, variable: 'DEFECTDOJO_API_KEY')]) {
        sh """
            curl --fail -k -X POST "${defectdojoUrl}/api/v2/reimport-scan/" \
              -H "Authorization: Token \${DEFECTDOJO_API_KEY}" \
              -F "scan_type=Trivy Scan" \
              -F "file=@${reportPath}" \
              -F "auto_create_context=true" \
              -F "product_type_name=${productTypeName}" \
              -F "product_name=${productName}" \
              -F "engagement_name=${engagementName}" \
              -F "test_title=${testTitle}" \
              -F "active=true" \
              -F "verified=true" \
              -F "close_old_findings=true"
        """
    }
}