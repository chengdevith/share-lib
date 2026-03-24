def call(Map config = [:]) {
    sh '''
        ls -la
        docker build -t ${FULL_IMAGE} .
    '''
}