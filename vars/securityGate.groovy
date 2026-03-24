def call(Map config = [:]) {
    def severity = config.severity ?: 'HIGH,CRITICAL'

    sh """
        docker compose -f ${TRIVY_PATH} exec -T trivy \
          trivy image \
          --severity ${severity} \
          --exit-code 1 \
          --no-progress \
          ${FULL_IMAGE}
    """
}