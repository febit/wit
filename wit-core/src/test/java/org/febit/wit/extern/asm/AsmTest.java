/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.extern.asm;

import org.febit.wit_shaded.asm.ClassWriter;
import org.febit.wit_shaded.asm.Constants;
import org.febit.wit_shaded.asm.Label;
import org.febit.wit_shaded.asm.MethodWriter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AsmTest implements Constants {

    @Test
    void test() throws Exception {
        ClassWriter classWriter = new ClassWriter(Constants.V1_5, Constants.ACC_PUBLIC,
                "x/Example", "java/lang/Object", null);

        AsmUtils.visitConstructor(classWriter);

        MethodWriter m = classWriter.visitMethod(ACC_PUBLIC, "test",
                "([Ljava/lang/Object;)Ljava/lang/Object;", null);

        Label toException = new Label();
        m.visitVarInsn(Constants.ALOAD, 1);
        m.visitJumpInsn(Constants.IFNULL, toException);
        m.visitVarInsn(Constants.ALOAD, 1);
        m.visitInsn(Constants.ARRAYLENGTH);
        m.visitJumpInsn(Constants.IFEQ, toException);
        m.visitVarInsn(Constants.ALOAD, 1);
        m.visitInsn(Constants.ICONST_0);

        m.visitInsn(Constants.AALOAD);
        m.visitJumpInsn(Constants.IFNULL, toException);
        m.visitVarInsn(Constants.ALOAD, 1);
        m.visitInsn(Constants.ICONST_0);
        m.visitInsn(Constants.AALOAD);
        m.visitTypeInsn(Constants.CHECKCAST, "java/lang/String");
        m.visitMethodInsn(Constants.INVOKEVIRTUAL, "java/lang/String", "length", "()I");
        AsmUtils.visitBoxIfNeed(m, int.class);
        m.visitInsn(Constants.ARETURN);
        m.visitLabel(toException);
        AsmUtils.visitScriptEvaluateException(m, "First argument can't be null.");
        m.visitMaxs();

        Class<?> exampleClass = AsmUtils.loadClass("x.Example", classWriter);

        Object obj = exampleClass.getConstructor().newInstance();
        Object result = exampleClass.getMethods()[0].invoke(obj, new Object[]{new Object[]{""}});
        assertEquals(0, result);
    }
}
