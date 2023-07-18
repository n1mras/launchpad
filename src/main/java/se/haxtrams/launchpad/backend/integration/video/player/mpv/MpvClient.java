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
            waitForSocketCreated(Duration.ofMillis(500));
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

    private boolean waitForSocketCreated(Duration timeout) {
        var abortTime = Instant.now().plus(timeout);
        do {
            if (new File(SOCKET_PATH).canWrite()) {
                return true;
            }
            sleep(1);
        } while (Instant.now().isBefore(abortTime));
        log.warn("Could not find socket in time.");
        return false;
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
