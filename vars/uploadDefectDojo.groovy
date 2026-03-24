def call(Map config = [:]) {
    sh '''
        curl --fail -k -X POST "${DEFECTDOJO_URL}/api/v2/reimport-scan/" \
          -H "Authorization: Token ${DEFECTDOJO_API_KEY}" \
          -F "scan_type=Trivy Scan" \
          -F "file=@${REPORT_PATH}" \
          -F "auto_create_context=true" \
          -F "product_type_name=${PRODUCT_TYPE_NAME}" \
          -F "product_name=${PRODUCT_NAME}" \
          -F "engagement_name=${ENGAGEMENT_NAME}" \
          -F "test_title=${TEST_TITLE}" \
          -F "active=true" \
          -F "verified=true" \
          -F "close_old_findings=true"
    '''
}