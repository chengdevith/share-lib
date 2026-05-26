def call(Map config = [:]) {
    def severity  = config.severity ?: 'HIGH,CRITICAL'
    def trivyPath = config.trivyPath ?: env.TRIVY_PATH ?: error('securityGate: trivyPath is required')
    def fullImage = config.fullImage ?: env.FULL_IMAGE ?: error('securityGate: fullImage is required')

    sh """
        docker compose -f ${trivyPath} exec -T trivy \
          trivy image \
          --severity ${severity} \
          --exit-code 1 \
          --no-progress \
          ${fullImage}
    """
}
