package com.cnu16.a9;

/**
 * Created by vaibhavtulsyan on 20/07/16.
 * Rule: findbugs:squid:S2178
 * (http://grepcode.com/file/repo1.maven.org/maven2/org.sonarsource.java/java-checks/3.4/org/sonar/l10n/java/rules/squid/S2178.html)
 *
 * Violation: Use of non-short-circuit logic in a  boolean context causes a mistake.
 *            non_compliant_f() checks the entire if condition and throws a NullPointerException
 *            compliant_f() evaluates x!=null as false and stops the evaluation further
 */

public class RuleA {
    public Boolean non_compliant_f() throws NullPointerException {
        String x = null;
        if(x != null & x.toLowerCase() == "abc") {
            return new Boolean(true);
        }
        return new Boolean(false);
    }

    public Boolean compliant_f() throws NullPointerException{
        String x = null;
        if(x != null && x.toLowerCase() == "abc") {
            return new Boolean(true);
        }
        return new Boolean(false);
    }

    public void tester() {
        Boolean value1 = non_compliant_f();
        Boolean value2 = compliant_f();
    }

}
