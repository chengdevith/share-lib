def call(Map config = [:]) {
    sh '''
        mkdir -p /home/enz/trivy/reports
        docker compose -f ${TRIVY_PATH} exec -T trivy \
        trivy image \
        --scanners vuln,secret,misconfig \
        --format json \
        -o /reports/trivy-report.json \
        ${FULL_IMAGE}
    '''
}