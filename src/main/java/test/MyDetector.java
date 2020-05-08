package test;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.OpcodeStack;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.*;
import test.helper.*;

import java.util.*;

public class MyDetector extends OpcodeStackDetector {
    private final BugReporter bugReporter;

    public MyDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }
    static List<Integer> list = new ArrayList<>();
    static Method method;
    static Map<Method, List<Parameter>> parametersPerMethod = new HashMap <>();
    static HashMap<Method, HashMap<Parameter, ArrayList<Usage>>> UsagesPerAttributePerMethod = new HashMap <>();
    static OpcodeStack.Item item;
    static int paras;
    @Override
    public void sawOpcode(int seen) {
        try
        {
            if (seen == Const.INVOKEVIRTUAL || seen == Const.GETFIELD)
            {
                item = getStack().getStackItem(0);
                if (method.isStatic())
                {
                    if (item.getRegisterNumber() >= paras)
                    {
                        sawMethod();
                    }
                }
                else if (item.getRegisterNumber() > paras)
                {
                    sawMethod();
                }
            }
            else if (seen == Const.PUTFIELD)
            {
                item = getStack().getStackItem(1);
            }
            else if (seen == Const.INVOKEINTERFACE)
            {
                item = getStack().getStackItem(0);
                sawMethod();
            }
            else
            {
                item = null;
            }

            if (    seen == Const.RETURN ||
                    seen == Const.ARETURN ||
                    seen == Const.DRETURN ||
                    seen == Const.FRETURN ||
                    seen == Const.IRETURN ||
                    seen == Const.LRETURN)
            {
                int pc = getPC();
                LineNumberTable table = getMethod().getLineNumberTable();
                LineNumber last = table.getLineNumberTable()[table.getTableLength()-1];
                int pcOfLast = last.getStartPC();
                if (pc == pcOfLast) {
                    onLeaveMethod(getMethod());
                }
            }

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
                    paras = 0;
                    System.out.println(method.getName());
                    if (method.getName().equals("<init>"))
                    {
                        return;
                    }
                    int i = 1;
                    while (true)
                    {
                        OpcodeStack.Item item = getStack().getLVValue(i++);
                        if (item.equals(new OpcodeStack.Item()))
                        {
                            break;
                        }
                        paras++;
                        Parameter parameter = new Parameter();
                        parameter.registerNumber = item.getRegisterNumber();
                        Class<?> c = null;
                        if (item.getSignature().length() == 1)
                        {
                            switch (item.getSignature()) {
                                case "I":
                                    c = int.class;
                                    break;
                                case "Z":
                                    c = boolean.class;
                                    break;
                                case "D":
                                    c = double.class;
                                    break;
                                case "J":
                                    c = long.class;
                                    break;
                                case "F":
                                    c = float.class;
                                    break;
                                case "B":
                                    c = byte.class;
                                    break;
                                case "S":
                                    c = short.class;
                                    break;
                                case "C":
                                    c = char.class;
                                    break;
                            }
                        }
                        else {
                            String clazz = item.getSignature().substring(1, item.getSignature().length() - 1);
                            c = Class.forName(clazz.replace("/", "."));
                        }
                        parameter.setClazz(c);
                        parameter.setLineNumber(getMethod().getLineNumberTable().getLineNumberTable()[0].getLineNumber());
                        if (parametersPerMethod.containsKey(method))
                        {
                            parametersPerMethod.get(method).add(parameter);
                        }
                        else {
                            List<Parameter> parameters = new ArrayList <>();
                            parameters.add(parameter);
                            parametersPerMethod.put(method,parameters);
                        }
                    }
                    LocalVariableTable table = method.getLocalVariableTable();
                    if (method.isStatic())
                        i--;
                    for (int j = i-1; j < table.getLocalVariableTable().length; j++) {
                        int finalJ = j;
                        Optional<LocalVariable> optional = Arrays.stream(table.getLocalVariableTable()).filter(e -> e.getIndex() == finalJ).findFirst();
                        if (optional.isPresent()) {
                            LocalVariable variable = optional.get();
                            String signature = variable.getSignature();
                            Parameter p = new Parameter();
                            p.registerNumber = j;
                            p.setClazz(Class.forName(signature.substring(1, signature.length() - 1).replace("/", ".")));
                            if (parametersPerMethod.containsKey(method)) {
                                parametersPerMethod.get(method).add(p);
                            } else {
                                List<Parameter> parameters = new ArrayList<>();
                                parameters.add(p);
                                parametersPerMethod.put(method, parameters);
                            }
                        }
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
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void sawMethod()
    {
        List<Parameter> p = parametersPerMethod.get(getMethod());
        if (p == null)
        {
            return;
        }

        if (item == null)
        {
            return;
        }
        Optional<Parameter> optionalParameter = p.stream().filter(e -> e.registerNumber == item.getRegisterNumber()).findFirst();
        Parameter parameter = optionalParameter.orElse(null);
        if (parameter == null)
        {
            return;
        }
        MethodUsage mu = new MethodUsage();
        mu.setClassDescriptor(getClassDescriptorOperand());
        mu.setMethodDescriptor(getMethodDescriptorOperand());
        if (UsagesPerAttributePerMethod.containsKey(method))
        {
            HashMap<Parameter, ArrayList<Usage>> usage = UsagesPerAttributePerMethod.get(method);
            if (!usage.containsKey(parameter)) {
                usage.put(parameter, new ArrayList<>());
            }
            usage.get(parameter).add(mu);
        }
        else
        {
            UsagesPerAttributePerMethod.put(method, new HashMap <>());
            HashMap<Parameter, ArrayList<Usage>> h = UsagesPerAttributePerMethod.get(method);
            h.put(parameter, new ArrayList<>());
            ArrayList<Usage> f = h.get(parameter);
            f.add(mu);
        }
        item = null;
    }

    @Override
    public void sawField()
    {
        List<Parameter> p = parametersPerMethod.get(getMethod());
        if (p == null)
        {
            return;
        }

        if (item == null || item.getRegisterNumber() == 0)
        {
            return;
        }
        Optional<Parameter> optionalParameter = p.stream().filter(e -> e.registerNumber == item.getRegisterNumber()).findFirst();
        Parameter parameter = optionalParameter.orElse(null);
        if (parameter == null)
        {
            return;
        }
        FieldUsage fu = new FieldUsage();
        fu.setClassDescriptor(getClassDescriptorOperand());
        fu.setFieldDescriptor(getFieldDescriptorOperand());
        if (UsagesPerAttributePerMethod.containsKey(method))
        {
            HashMap<Parameter, ArrayList<Usage>> usage = UsagesPerAttributePerMethod.get(method);
            if (!usage.containsKey(parameter)) {
                usage.put(parameter, new ArrayList<>());
            }
            usage.get(parameter).add(fu);
        }
        else
        {
            UsagesPerAttributePerMethod.put(method, new HashMap <>());
            HashMap<Parameter, ArrayList<Usage>> h = UsagesPerAttributePerMethod.get(method);
            h.put(parameter, new ArrayList <>());
            ArrayList<Usage> f = h.get(parameter);
            f.add(fu);
        }
        item = null;
    }

    public void onLeaveMethod(Method method)
    {
        HashMap<Parameter, ArrayList<Usage>> occurences = UsagesPerAttributePerMethod.get(method);
        if (occurences == null)
        {
            return;
        }
        outer:
        for (Map.Entry<Parameter, ArrayList<Usage>> entry : occurences.entrySet())
        {
            Class<?> clazz = entry.getKey().getClazz();
            Class<?>[] interfaces = clazz.getInterfaces();
            interfaceLoop:
            for (Class<?> inter: interfaces)
            {
                ArrayList<Boolean> booleans = new ArrayList<>();
                for (Usage usage : entry.getValue())
                {
                    if (usage instanceof MethodUsage)
                    {
                        FindMethodUsage(inter, booleans, (MethodUsage) usage);
                    }
                    else if (usage instanceof FieldUsage)
                    {
                        break interfaceLoop;
                    }
                }
                if (booleans.stream().allMatch(e -> e))
                {
                    BugInstance bug = new BugInstance(this, "MY_BUG", HIGH_PRIORITY)
                            .addClassAndMethod(this)
                            .addSourceLine(this, method.getLineNumberTable().getLineNumberTable()[0].getLineNumber());
                    bugReporter.reportBug(bug);
                    continue outer;
                }
            }

            Class<?> extension = clazz.getSuperclass();
            if (extension != null)
            {
                ArrayList<Boolean> booleans = new ArrayList<>();
                for (Usage usage : entry.getValue())
                {
                    if (usage instanceof MethodUsage)
                    {
                        FindMethodUsage(extension, booleans, (MethodUsage) usage);
                    }
                    else if (usage instanceof FieldUsage)
                    {
                        FindFieldUsage(extension, booleans, (FieldUsage) usage);
                    }
                }
                if (booleans.stream().allMatch(e -> e))
                {
                    BugInstance bug = new BugInstance(this, "MY_BUG", HIGH_PRIORITY)
                            .addClassAndMethod(this)
                            .addSourceLine(this, method.getLineNumberTable().getLineNumberTable()[0].getLineNumber());
                    bugReporter.reportBug(bug);
                    continue outer;
                }
            }
        }
    }

    private static void FindMethodUsage(Class<?> extension, ArrayList<Boolean> booleans, MethodUsage usage) {
        MethodWrapper methodWrapper = null;
        try {
            methodWrapper = new MethodWrapper(usage.getClassDescriptor(), usage.getMethodDescriptor());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (methodWrapper == null)
        {
            return;
        }
        boolean once = false;
        for (java.lang.reflect.Method m : extension.getMethods())
        {
            MethodWrapper interMethodWrapper = new MethodWrapper(m);
            if (methodWrapper.equalMethod(interMethodWrapper))
            {
                once = true;
                break;
            }
        }
        booleans.add(once);
    }

    private static void FindFieldUsage(Class<?> extension, ArrayList<Boolean> booleans, FieldUsage usage) {
        FieldWrapper fieldWrapper = null;
        try {
            fieldWrapper = new FieldWrapper(usage.getClassDescriptor(), usage.getFieldDescriptor());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (fieldWrapper == null)
        {
            return;
        }
        boolean once = false;
        for (java.lang.reflect.Field f : extension.getFields())
        {
            FieldWrapper extensionFieldWrapper = new FieldWrapper(f);
            if (fieldWrapper.equalField(extensionFieldWrapper))
            {
                once = true;
                break;
            }
        }
        booleans.add(once);
    }
}
