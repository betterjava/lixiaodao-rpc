package test.lixiaodao.registry;

import com.lixiaodao.registry.server.RpcServerRegistry;

public class RpcServerRegistryTest {

	public static void main(String[] args) {
		RpcServerRegistry serverRegistry = RpcServerRegistry.getInstance();
		try {
			serverRegistry.connectZookeeper("127.0.0.1:2181", 1000);
			serverRegistry.registerServer("lixiaodao", "127.0.0.1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (true) {

		}
	}
}
