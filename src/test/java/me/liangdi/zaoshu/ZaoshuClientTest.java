package me.liangdi.zaoshu;

import lombok.extern.slf4j.Slf4j;
import me.liangdi.zaoshu.model.Wallet;
import org.junit.Test;

/**
 * Created by liangdi on 6/27/17.
 */
@Slf4j
public class ZaoshuClientTest {

    @Test
    public void testClient() throws ApiException {
        String apiKey = "76a509404e0247359f03f12d5933211d";
        String secret = "90bdfe7a832c54c62175364e527f6bf43a663cdab3b6a9b545273f2208b73d69";

        ZaoshuClient client = ZaoshuClient.getInstance();

        Wallet wallet = client.user().wallet();

        log.info("wallet:{}",wallet);
    }
}
