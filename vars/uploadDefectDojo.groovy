def call(Map config = [:]) {
    def defectdojoUrl          = config.defectdojoUrl ?: error('uploadDefectDojo: defectdojoUrl is required')
    def defectdojoCredentialId = config.defectdojoCredentialId ?: error('uploadDefectDojo: defectdojoCredentialId is required')
    def reportPath             = config.reportPath ?: '/home/istad/trivy/reports/trivy-report.json'
    def productTypeName        = config.productTypeName ?: 'Web Applications'
    def productName            = config.productName ?: error('uploadDefectDojo: productName is required')
    def engagementName         = config.engagementName ?: 'Jenkins'
    def testTitle              = config.testTitle ?: 'Trivy Image Scan'

    withCredentials([string(credentialsId: defectdojoCredentialId, variable: 'DEFECTDOJO_API_KEY')]) {
        sh """
            set -e
            response_file=\$(mktemp)
            http_code=\$(curl -sS -k -o "\$response_file" -w "%{http_code}" -X POST "${defectdojoUrl}/api/v2/reimport-scan/" \
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
              -F "close_old_findings=true")

            echo "[defectdojo] HTTP \$http_code"
            echo "[defectdojo] response:"
            cat "\$response_file"

            if [ "\$http_code" -lt 200 ] || [ "\$http_code" -ge 300 ]; then
              rm -f "\$response_file"
              exit 22
            fi

            rm -f "\$response_file"
        """
    }
}
