package test;

import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;

import java.lang.reflect.Method;


public class MethodUsage implements Usage
{
    private ClassDescriptor classDescriptor;
    private MethodDescriptor methodDescriptor;

    public MethodDescriptor getMethodDescriptor()
    {
        return methodDescriptor;
    }

    public void setMethodDescriptor(MethodDescriptor methodDescriptor)
    {
        this.methodDescriptor = methodDescriptor;
    }

    public ClassDescriptor getClassDescriptor()
    {
        return classDescriptor;
    }

    public void setClassDescriptor(ClassDescriptor classDescriptor)
    {
        this.classDescriptor = classDescriptor;
    }
}