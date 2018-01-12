package fi.helsinki.opintoni.server;

import org.springframework.test.web.client.MockRestServiceServer;

public abstract class AbstractRestServiceServer {
    protected final MockRestServiceServer server;

    public AbstractRestServiceServer(MockRestServiceServer server) {
        this.server = server;
    }

    public void verify() {
        server.verify();
    }
}
