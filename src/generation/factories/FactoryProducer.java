package generation.factories;

public class FactoryProducer {
	public static CopieFactory getFactory(String type) {
		if (type.toLowerCase().endsWith("corrige")) {
			return new CorrigeFactory();
		} else {
			return new BlankFactory();
		}
	}
}
