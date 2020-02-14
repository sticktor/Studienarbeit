package test;

import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import org.apache.bcel.classfile.Method;

public class MethodOfClass
{
    public ClassDescriptor classDescriptor;
    public Method method;

    @Override
    public boolean equals(Object equal)
    {
        if (equal == null)
            return false;
        if (equal instanceof MethodOfClass)
        {
            MethodOfClass e = (MethodOfClass)equal;
            return this.classDescriptor.getClassName().equals(e.classDescriptor.getClassName())
                    && this.method.getSignature().equals(e.method.getSignature());
        }
        return false;
    }
}
