package tech.geekcity.blackhole.ssh;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.keyverifier.DefaultKnownHostsServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.junit.Test;

import java.io.IOException;
import java.util.EnumSet;

public class TestSsh {
    @Test
    public void test() throws IOException {
        SshClient client = SshClient.setUpDefaultClient();
        client.setServerKeyVerifier(new DefaultKnownHostsServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE));
        client.start();
        try (ClientSession session = client.connect("ben.wangz", "node01", 22)
                .verify()
                .getSession()) {
            session.addPasswordIdentity("12345678");
            session.auth().verify();

            try (ClientChannel channel = session.createExecChannel("hostname")) {
                channel.setOut(System.out);
                channel.setErr(System.err);
                channel.open().verify();
                channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 0L);
            }
        }
        client.stop();
        client.close();
    }
}