package test.helper;

/***
 * Helper class to tune down some code lines
 */
public class ClassHelper {
	private ClassHelper(){}


	public static Class<?> GetClassFromString(String classString) throws ClassNotFoundException {
		switch (classString) {
			case "I":
				return int.class;
			case "Z":
				return boolean.class;
			case "D":
				return double.class;
			case "J":
				return long.class;
			case "F":
				return float.class;
			case "B":
				return byte.class;
			case "S":
				return short.class;
			case "C":
				return char.class;
			default:
				return Class.forName(classString.substring(1).replace("/", "."));
		}
	}

	public static Class<?> GetPrimitiveTypeFromString(String primitive)
	{
		switch (primitive) {
			case "I":
				return int.class;
			case "Z":
				return boolean.class;
			case "D":
				return double.class;
			case "J":
				return long.class;
			case "F":
				return float.class;
			case "B":
				return byte.class;
			case "S":
				return short.class;
			case "C":
				return char.class;
			default:
				return null;
		}
	}
}
