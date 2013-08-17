// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.asm;

import webit.script.asm.AsmResolverGenerator;
import org.junit.Test;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.resolvers.SetResolver;
import static org.junit.Assert.*;

/**
 *
 * @author Zqq
 */
public class AsmResolverGeneratorTest {

    public static class Foo {

        public String f1 = "foo:f1";
        private String f2 = "foo:f2";
        public final String f3 = "foo:f3";
        public int f4 = 4;
        public String bG = "foo:bG"; // hashcode 3109
        public String af = "foo:af"; // hashcode 3109

        public String getF2() {
            return f2;
        }

        public void setF2(String f2) {
            this.f2 = f2;
        }
    }

    @Test
    public void test() throws Exception {

        AsmResolverGenerator generator = new AsmResolverGenerator();
        Class clazz = generator.generateResolver(Foo.class);

        Foo foo = new Foo();
        
        GetResolver resolver = (GetResolver) clazz.newInstance();
        SetResolver setResolver = (SetResolver) resolver;


        assertEquals(resolver.getMatchClass(), Foo.class);
        assertEquals(resolver.getMatchMode(), MatchMode.EQUALS);

        assertEquals(resolver.get(foo, "f1"), "foo:f1");
        assertEquals(resolver.get(foo, "f2"), "foo:f2");
        assertEquals(resolver.get(foo, "f3"), "foo:f3");
        assertEquals(resolver.get(foo, "f4"), 4);
        assertEquals(resolver.get(foo, "bG"), "foo:bG");
        assertEquals(resolver.get(foo, "af"), "foo:af");


        setResolver.set(foo, "f1", "new:f1");
        setResolver.set(foo, "f2", "new:f2");
        //setResolver.set(foo, "f3", "new:f3"); //exception
        setResolver.set(foo, "f4", 8);
        setResolver.set(foo, "bG", "new:bG");
        setResolver.set(foo, "af", "new:af");

        assertEquals(resolver.get(foo, "f1"), "new:f1");
        assertEquals(resolver.get(foo, "f2"), "new:f2");
        assertEquals(resolver.get(foo, "f3"), "foo:f3");
        assertEquals(resolver.get(foo, "f4"), 8);
        assertEquals(resolver.get(foo, "bG"), "new:bG");
        assertEquals(resolver.get(foo, "af"), "new:af");

    }
}
