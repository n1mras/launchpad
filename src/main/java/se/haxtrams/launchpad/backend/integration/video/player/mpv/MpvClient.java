package se.haxtrams.launchpad.backend.integration.video.player.mpv;

import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.File;
import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.haxtrams.launchpad.backend.integration.video.player.mpv.model.CommandRequest;

public class MpvClient {
    private static final String SOCKET_PATH = "/tmp/launchpad-mpv.socket";
    private final JsonMapper jsonMapper = new JsonMapper();
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private SocketChannel mpvSocketChannel;

    public void send(CommandRequest commandRequest) {
        var socket = getSocket();
        if (socket.isEmpty()) {
            return;
        }

        try {
            var buffer = ByteBuffer.allocate(1024);
            var json = jsonMapper.writeValueAsString(commandRequest);
            buffer.put(json.getBytes(StandardCharsets.UTF_8));
            buffer.putChar('\n');
            buffer.flip();
            while (buffer.hasRemaining()) {
                socket.get().write(buffer);
            }

            log.info("Message sent: {}", json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeSocket() {
        try {
            if (Objects.nonNull(mpvSocketChannel)) {
                log.info("closing mpv socket");
                mpvSocketChannel.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            new File(SOCKET_PATH).delete();
            mpvSocketChannel = null;
        }
    }

    private Optional<SocketChannel> getSocket() {
        try {
            if (mpvSocketChannel == null && Files.exists(Paths.get(SOCKET_PATH))) {
                mpvSocketChannel = SocketChannel.open(StandardProtocolFamily.UNIX);
                mpvSocketChannel.connect(UnixDomainSocketAddress.of(SOCKET_PATH));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(mpvSocketChannel);
    }
}
