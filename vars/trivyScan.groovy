def call(Map config = [:]) {
    def fullImage   = config.fullImage   ?: error('trivyScan: fullImage is required')
    def trivyPath   = config.trivyPath   ?: '/home/enz/trivy/docker-compose.yml'
    def reportPath  = config.reportPath  ?: '/home/enz/trivy/reports/trivy-report.json'
    def gateSeverity = config.gateSeverity ?: 'HIGH,CRITICAL'

    def reportDir = reportPath.substring(0, reportPath.lastIndexOf('/'))

    sh """
        mkdir -p ${reportDir}
        docker compose -f ${trivyPath} exec --no-TTY trivy \
            trivy image \
            --scanners vuln,secret,misconfig \
            --exit-code 1 \
            --severity ${gateSeverity} \
            --format json \
            -o /reports/trivy-report.json \
            ${fullImage}
    """
}