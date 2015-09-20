systemName = 'test'
endpoint {
    first {
        path = 'test'
        port = 8080
        protocol = 'http'
        address = 'localhost'
    }
    second {
        password = 'pass'
        protocol = 'ftp'
        address = 'localhost'
        port = 21
        user = 'admin'
    }
}
test.key = ['really': 'nested?'] as Properties