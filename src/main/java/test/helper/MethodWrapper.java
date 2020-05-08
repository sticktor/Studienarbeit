package test.helper;

import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;

import java.lang.reflect.Method;
import java.util.Objects;

public class MethodWrapper
{
	private Class<?> declaringClass;
	private Class<?> returnType;
	private Class<?>[] parameterTypes;
	private String name;

	public MethodWrapper(Method method) {
		this.declaringClass = method.getDeclaringClass();
		this.returnType = method.getReturnType();
		this.parameterTypes = method.getParameterTypes();
		this.name = method.getName();
	}

	public MethodWrapper(ClassDescriptor classDescriptor, MethodDescriptor methodDescriptor) throws ClassNotFoundException
	{
		this.declaringClass = Class.forName(classDescriptor.getDottedClassName());
		String[] signature = methodDescriptor.getSignature().replace("(", "").replace(")", "").split(";");
		parameterTypes = new Class<?>[signature.length-1];

		for (int i = 0; i < signature.length-1; i++) {
			String parameter = signature[i];
			switch (parameter) {
				case "I":
					parameterTypes[i] = int.class;
					break;
				case "Z":
					parameterTypes[i] = boolean.class;
					break;
				case "D":
					parameterTypes[i] = double.class;
					break;
				case "J":
					parameterTypes[i] = long.class;
					break;
				case "F":
					parameterTypes[i] = float.class;
					break;
				case "B":
					parameterTypes[i] = byte.class;
					break;
				case "S":
					parameterTypes[i] = short.class;
					break;
				case "C":
					parameterTypes[i] = char.class;
					break;
				default:
					parameterTypes[i] = Class.forName(parameter.substring(1).replace("/", "."));
					break;
			}
			//parameter = parameter.substring(1);
			//parameterTypes[i] = Class.forName(parameter.replace("/", "."));
		}
		this.name = methodDescriptor.getName();
		String parameter = signature[1];
		if (parameter.replace("/", ".").equals("V"))
		{
			returnType = Void.TYPE;
		}
		else
		{
			switch (parameter) {
				case "I":
					returnType = int.class;
					break;
				case "Z":
					returnType = boolean.class;
					break;
				case "D":
					returnType = double.class;
					break;
				case "J":
					returnType = long.class;
					break;
				case "F":
					returnType = float.class;
					break;
				case "B":
					returnType = byte.class;
					break;
				case "S":
					returnType = short.class;
					break;
				case "C":
					returnType = char.class;
					break;
				default:
					returnType = Class.forName(parameter.substring(1).replace("/", "."));
					break;
			}
			//returnType = Class.forName(parameter.substring(1).replace("/", "."));
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof MethodWrapper) {
			MethodWrapper other = (MethodWrapper)obj;
			if ((getDeclaringClass() == other.getDeclaringClass())
					&& (Objects.equals(getName(), other.getName()))) {
				if (!returnType.equals(other.getReturnType()))
					return false;
				return equalParamTypes(parameterTypes, other.parameterTypes);
			}
		}
		return false;
	}

	public boolean equalMethod(MethodWrapper obj)
	{
		if (obj != null) {
			if (Objects.equals(getName(), obj.getName())) {
				if (!Objects.equals(returnType, obj.getReturnType()))
					return false;
				return equalParamTypes(parameterTypes, obj.parameterTypes);
			}
		}
		return false;
	}

	private boolean equalParamTypes(Class<?>[] params1, Class<?>[] params2)
	{
		if (params1.length == params2.length) {
			for (int i = 0; i < params1.length; i++) {
				if (params1[i] != params2[i])
					return false;
			}
			return true;
		}
		return false;
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	public void setDeclaringClass(Class<?> declaringClass) {
		this.declaringClass = declaringClass;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
