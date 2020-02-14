package test;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.OpcodeStack;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import org.apache.bcel.classfile.Method;

import java.util.*;

public class MyDetector extends OpcodeStackDetector {
    private final BugReporter bugReporter;

    public MyDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }
    static List<Integer> list = new ArrayList<>();
    static boolean inMethod;
    static MethodOfClass methodOfClass;
    static Method method;
    static Class firstParameter;
    static Map<Method, List<Parameter>> parametersPerMethod = new HashMap <>();
    static Map<Method, Map<Parameter, List<Usage>>> UsagesPerAttributePerMethod;
    @Override
    public void sawOpcode(int seen) {
        try
        {
            list.add(seen);
            try
            {
                //System.out.println(seen);
                Method oldMethod = method;
                method = getMethod();
                if (method.getName().equals("method"))
                {
                    System.out.println("IN THE REAL");
                }
                if (oldMethod != method)
                {
                    onLeaveMethod(oldMethod);
                    String firstParameterOfCurrentMethod;
                    System.out.println(method.getName());
                    if (method.getName().equals("<init>"))
                    {
                        return;
                    }
                    String[] signature = method.getSignature().replace("(", "").replace(")", "").split(";");
                    firstParameterOfCurrentMethod = signature[0];
                    firstParameterOfCurrentMethod = firstParameterOfCurrentMethod.substring(1);
                    firstParameter = Class.forName(firstParameterOfCurrentMethod.replace("/","."));
                    if (firstParameter != null)
                    {
                        int i = 1;
                        while (true)
                        {
                            OpcodeStack.Item item = getStack().getLVValue(i++);
                            if (item.equals(new OpcodeStack.Item()))
                            {
                                break;
                            }
                            Parameter parameter = new Parameter();
                            parameter.registerNumber = item.getRegisterNumber();
                            String clazz = item.getSignature().substring(1, item.getSignature().length() - 1);
                            Class c = Class.forName(clazz.replace("/", "."));
                            parameter.setClazz(c);
                            if (parametersPerMethod.containsKey(method))
                            {
                                parametersPerMethod.get(method).add(parameter);
                            }
                            else
                            {
                                List<Parameter> parameters = new ArrayList <>();
                                parameters.add(parameter);
                                parametersPerMethod.put(method,parameters);
                            }
                        }
                        /*if (seen == Const.INVOKEVIRTUAL)
                        {
                            OpcodeStack.Item i = getStack().getStackItem(0);
                            String sig = i.getSignature();
                            int r = i.getRegisterNumber();
                        }*/
                    }
                }
                /*ClassDescriptor c = getClassDescriptorOperand();
                MethodOfClass moc = new MethodOfClass();
                moc.classDescriptor = c;
                moc.method = m;
                if (moc.equals(methodOfClass))
                {
                    inMethod = true;
                }
                else
                {
                    inMethod = true;
                    methodOfClass = moc;
                    System.out.println(moc.method.getSignature());
                }*/
            }
            catch (Exception e)
            {
                System.out.println(e);
                methodOfClass = null;
                inMethod = false;
                return;
            }

            /*if (getClassConstantOperand().equals("java/lang/System")
                    && getNameConstantOperand().equals("out"))
            {
                // report bug when System.out is used in code
                BugInstance bug = new BugInstance(this, "MY_BUG", NORMAL_PRIORITY)
                        .addClassAndMethod(this)
                        .addSourceLine(this, getPC());
                bugReporter.reportBug(bug);
            }*/
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    @Override
    public void sawMethod()
    {
        System.out.println(isMethodCall());
        System.out.println(getClassDescriptorOperand());
        System.out.println(getMethodDescriptorOperand());
    }

    @Override
    public void sawField()
    {
        System.out.println(getClassDescriptorOperand().getClassName());
        System.out.println(getFieldDescriptorOperand().getName());
    }
    @Override
    public void sawClass()
    {

    }

    @Override
    public void sawString(String seen)
    {
    }

    public void onLeaveMethod(Method method)
    {
        System.out.println("HELS");
    }

}
