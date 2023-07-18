package se.haxtrams.launchpad.backend.integration.video.player.mpv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.File;
import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.haxtrams.launchpad.backend.integration.video.player.mpv.model.CommandRequest;
import se.haxtrams.launchpad.backend.integration.video.player.mpv.model.MpvCommand;

public class MpvClient {
    private static final String SOCKET_PATH = "/tmp/launchpad-mpv.socket";
    private final JsonMapper jsonMapper = new JsonMapper();
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private SocketChannel mpvSocketChannel;

    public void send(MpvCommand command, Object... params) {
        if (isConnected()) {
            send(toJsonRequest(command, params));
        }
    }

    private void send(String json) {
        try {
            var buffer = ByteBuffer.allocate(1024);
            buffer.put(json.getBytes(StandardCharsets.UTF_8));
            buffer.putChar('\n');
            buffer.flip();
            while (buffer.hasRemaining()) {
                mpvSocketChannel.write(buffer);
            }

            log.debug("Message sent: {}", json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        try {
            if (Objects.nonNull(mpvSocketChannel)) {
                mpvSocketChannel.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // mpv doesn't remove the socket.
            new File(SOCKET_PATH).delete();
        }
    }

    public void connect() {
        if (isConnected()) return;

        try {
            waitForSocketCreated(Duration.ofSeconds(5));
            mpvSocketChannel = SocketChannel.open(StandardProtocolFamily.UNIX);
            mpvSocketChannel.connect(UnixDomainSocketAddress.of(SOCKET_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isConnected() {
        return Optional.ofNullable(mpvSocketChannel)
                .map(SocketChannel::isConnected)
                .orElse(false);
    }

    private void waitForSocketCreated(Duration timeout) {
        var abortTime = Instant.now().plus(timeout);
        do {
            if (new File(SOCKET_PATH).canWrite()) {
                return;
            }
            sleep(1);
        } while (Instant.now().isBefore(abortTime));
        throw new RuntimeException("Could not find unix socket within time limit");
    }

    private String toJsonRequest(MpvCommand command, Object... params) {
        try {
            var cmdArgs = Stream.concat(Stream.of(command), Stream.of(params)).toList();
            return jsonMapper.writeValueAsString(new CommandRequest(cmdArgs));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
