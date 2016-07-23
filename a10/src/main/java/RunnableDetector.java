import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BytecodeScanningDetector;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;

public class RunnableDetector extends BytecodeScanningDetector {

    protected BugReporter bugReporter;
    protected boolean entity;


    DescriptorFactory df;
    private static final ClassDescriptor THREAD_CLASS = DescriptorFactory.createClassDescriptor(Thread.class);
    private static final ClassDescriptor RUNNABLE_CLASS = DescriptorFactory.createClassDescriptor(Runnable.class);
    private String RUN_STR = "run";


    public RunnableDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    boolean isRunnableInstance(ClassDescriptor c) {
        if(c == null) return false;

        try {
            if(c.getXClass().getClassDescriptor().equals(RUNNABLE_CLASS)) return true;
            ClassDescriptor[] interfaceList = c.getXClass().getInterfaceDescriptorList();
            for(ClassDescriptor descriptor : interfaceList) {
                return descriptor.getXClass().equals(RUNNABLE_CLASS) | isRunnableInstance(descriptor.getXClass().getSuperclassDescriptor());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void sawMethod() {

        MethodDescriptor invokedMethod = getMethodDescriptorOperand();
        ClassDescriptor invokedObject = getClassDescriptorOperand();

        try {
            ClassDescriptor curClass = invokedObject.getXClass().getClassDescriptor();
            ClassDescriptor curSuper = invokedObject.getXClass().getSuperclassDescriptor();
            String methodname = invokedMethod.getName();

            System.out.println(curClass.getXClass().getSource());
            if(methodname.equals(RUN_STR)) {
                ClassDescriptor[] interfaceList = invokedObject.getXClass().getInterfaceDescriptorList();
                for (ClassDescriptor descriptor : interfaceList) {

                    // TODO: call isRunnableInstance(descriptor)
                    if (descriptor.equals(RUNNABLE_CLASS)) {
                        bugReporter.reportBug(
                                new BugInstance(this, "RUN_INCONSISTENCY", 2)
                                        .addClassAndMethod(this).addSourceLine(this));
                        break;
                    }

                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}