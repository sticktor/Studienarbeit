package test.helper;

import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.FieldDescriptor;

import java.lang.reflect.Field;

public class FieldWrapper {
	private Class<?> declaringClass;
	private String name;
	private Class<?> type;

	public FieldWrapper(Field field)
	{
		declaringClass = field.getDeclaringClass();
		name = field.getName();
		type = field.getType();
	}

	public FieldWrapper(ClassDescriptor classDescriptor, FieldDescriptor fieldDescriptor) throws ClassNotFoundException {
		this.declaringClass = Class.forName(classDescriptor.getDottedClassName());

		this.name = fieldDescriptor.getName();
		this.type = Class.forName(fieldDescriptor.getClassDescriptor().getDottedClassName());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Field) {
			Field other = (Field)obj;
			return (getDeclaringClass() == other.getDeclaringClass())
					&& (getName() == other.getName())
					&& (getType() == other.getType());
		}
		return false;
	}

	public boolean equalField(FieldWrapper other)
	{
		if (other != null) {
			return (getDeclaringClass() == other.getDeclaringClass())
					&& (getName() == other.getName())
					&& (getType() == other.getType());
		}
		return false;
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	public void setDeclaringClass(Class<?> declaringClass) {
		this.declaringClass = declaringClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}
}
