package test.lixiaodao.rpc.tcp.nett4;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class XmlTest {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"RpcClient.xml");
	}
}
