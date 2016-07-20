package com.cnu16.a9;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaibhavtulsyan on 20/07/16.
 * Rule: findbugs:IL_INFINITE_LOOP
 * (http://grepcode.com/file/repo1.maven.org/maven2/net.sf.sanity4j/sanity4j/1.1.1/resources/report/rules/findbugs/IL_INFINITE_LOOP.html)
 * Violation: An apparent infinite loop (IL_INFINITE_LOOP). This loop doesn't seem to have a way to terminate (other than by perhaps
 *            throwing an exception).
 */

public class RuleB {
    public void f() {
        List<Integer> l = new ArrayList<Integer>();
        l.add(1);
        l.add(2);
        int i = 0;
        while(i < l.size()) {
            System.out.println(i);
            l.add(i);
            i += 1;
        }
    }
}
