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

		for (int i = 1; i < signature.length; i++) {
			String parameter = signature[i];
			parameter = parameter.substring(1);
			parameterTypes[i] = Class.forName(parameter.replace("/", "."));
		}
		this.name = methodDescriptor.getName();
		String parameter = signature[0];
		if (parameter.replace("/", ".").equals("V"))
		{
			returnType = Void.TYPE;
		}
		else
		{
			returnType = Class.forName(parameter.replace("/", "."));
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
