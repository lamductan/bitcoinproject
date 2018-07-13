import com.msgilligan.bitcoinj.rpc.BitcoinClient;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet2Params;

import java.net.URI;
import java.util.List;
import java.util.concurrent.*;

public class BitcoinUtils {

    private static BitcoinClient bitcoinClient = null;

    public static BitcoinClient getBitcoinClientInstance() {
        if (bitcoinClient == null) {
            String server = "http://192.168.1.115:18332";
            String username = "ndhy";
            String password = "12345";
            int dev = 1; // dev = 1 --> testnet
            try {
                NetworkParameters network;
                URI uri;
                if (dev == 0) {
                    network = new MainNetParams();
                    uri = new URI(server);

                } else {
                    network = new TestNet2Params();
                    uri = new URI(server);
                }
                System.out.println("bitcoinclient networkID" + network.getId());
                System.out.println("uri server bitcoin: " + uri.toString());
                bitcoinClient = new BitcoinClient(network, uri, username, password);
                return bitcoinClient;
            } catch (Exception e) {
                System.out.println("Can not connect to server and start bitcoinClient");
                e.printStackTrace();
                bitcoinClient = null;
            }
        }
        return bitcoinClient;
    }

    public static List<Transaction>  getTransactionInBlock(int currentBlock) throws ExecutionException {
        while (true) {
            ExecutorService executor = Executors.newCachedThreadPool();
            Callable<List<Transaction>> task = () -> bitcoinClient.getBlock(currentBlock).getTransactions();
            Future<List<Transaction>> future = executor.submit(task);
            try {
                return future.get(5, TimeUnit.SECONDS);
            } catch (TimeoutException | InterruptedException ex) {
                System.out.println("can not list transaction in block, retrying...");
                ex.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
                throw e;
            } finally {
                future.cancel(true);
            }
        }
    }

    public static int getBlockCount() throws ExecutionException {
        while (true) {
            ExecutorService executor = Executors.newCachedThreadPool();
            Callable<Integer> task = () -> bitcoinClient.getBlockCount();
            Future<Integer> future = executor.submit(task);
            try {
                return future.get(5, TimeUnit.SECONDS);
            } catch (TimeoutException | InterruptedException ex) {
                System.out.println("can not get Block Count detail, retrying...");
                ex.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
                throw e;
            } finally {
                future.cancel(true);
            }
        }
    }

}
