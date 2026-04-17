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
package org.febit.wit.parser;

import org.febit.wit.exception.ScriptParseException;
import org.febit.wit.ir.Position;
import org.febit.wit.ir.TextPosition;
import org.febit.wit.parser.support.ClassNameRope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClassManagerTest {

    private static final Position DUMMY_POS = TextPosition.of(1, 1);

    private ClassManager classManager;

    @BeforeEach
    void setUp() {
        classManager = new ClassManager();
    }

    private static ClassNameRope rope(String s, String... more) {
        var rope = new ClassNameRope(s);
        for (var p : more) {
            rope.append(p);
        }
        return rope;
    }

    @Test
    void imports_should_add_class() {
        classManager.imports(rope("java", "util", "List"), DUMMY_POS);
        assertThat(classManager.load("List")).isEqualTo(List.class);
    }

    @Test
    void imports_should_ignore_same_import() {
        classManager.imports(rope("java", "util", "List"), DUMMY_POS);
        classManager.imports(rope("java", "util", "List"), DUMMY_POS); // No exception
        assertThat(classManager.load("List")).isEqualTo(List.class);
    }

    @Test
    void imports_should_throw_on_ambiguous_import() {
        classManager.imports(rope("java", "util", "List"), DUMMY_POS);
        assertThatThrownBy(() -> classManager.imports(rope("java", "awt", "List"), DUMMY_POS))
                .isInstanceOf(ScriptParseException.class)
                .hasMessageContaining("Ambiguous import for class name: List");
    }

    @Test
    void imports_should_throw_on_primitive_import() {
        assertThatThrownBy(() -> classManager.imports(rope("int"), DUMMY_POS))
                .isInstanceOf(ScriptParseException.class)
                .hasMessageContaining("Cannot import primitive type: int");
    }

    @Test
    void resolve_should_resolve_by_full_name() {
        var rope = rope("java", "util", "Map");
        assertThat(classManager.resolve(rope, DUMMY_POS))
                .isEqualTo(Map.class);
        rope.increaseArrayDepth();
        assertThat(classManager.resolve(rope, DUMMY_POS))
                .isEqualTo(Map[].class);
        rope.increaseArrayDepth();
        assertThat(classManager.resolve(rope, DUMMY_POS))
                .isEqualTo(Map[][].class);
    }

    @Test
    void resolve_should_resolve_imported_class() {
        classManager.imports(rope("java", "util", "List"), DUMMY_POS);
        assertThat(classManager.resolve(rope("List"), DUMMY_POS))
                .isEqualTo(List.class);
        assertThat(classManager.resolve(rope("List").increaseArrayDepth(), DUMMY_POS))
                .isEqualTo(List[].class);
    }

    @Test
    void resolve_should_resolve_java_lang_class() {
        assertThat(classManager.resolve(rope("String"), DUMMY_POS))
                .isEqualTo(String.class);
        assertThat(classManager.resolve(rope("String").increaseArrayDepth(), DUMMY_POS))
                .isEqualTo(String[].class);
    }

    @Test
    void resolve_should_resolve_primitive_type() {
        assertThat(classManager.resolve(rope("int"), DUMMY_POS))
                .isEqualTo(int.class);
        assertThat(classManager.resolve(rope("int").increaseArrayDepth(), DUMMY_POS))
                .isEqualTo(int[].class);
        assertThat(classManager.resolve(rope("boolean"), DUMMY_POS))
                .isEqualTo(boolean.class);
    }

    @Test
    void resolve_should_throw_on_class_not_found() {
        assertThatThrownBy(() -> classManager.resolve(rope("com", "example", "NonExistent"), DUMMY_POS))
                .isInstanceOf(ScriptParseException.class)
                .hasMessageContaining("Class<?> not found: com.example.NonExistent");
    }

    @Test
    void load_should_load_by_full_name() {
        assertThat(classManager.load("java.util.Map")).isEqualTo(Map.class);
    }

    @Test
    void load_should_load_java_lang_class() {
        assertThat(classManager.load("String")).isEqualTo(String.class);
        assertThat(classManager.load("Integer")).isEqualTo(Integer.class);
    }

    @Test
    void load_should_load_primitive_type() {
        assertThat(classManager.load("int")).isEqualTo(int.class);
        assertThat(classManager.load("long")).isEqualTo(long.class);
    }

    @Test
    void load_should_load_array_types() {
        classManager.imports(rope("java", "util", "Map"), DUMMY_POS);
        assertThat(classManager.load("String[]")).isEqualTo(String[].class);
        assertThat(classManager.load("int[][]")).isEqualTo(int[][].class);
        assertThat(classManager.load("Map[]")).isEqualTo(Map[].class);
        assertThat(classManager.load("java.util.List[]")).isEqualTo(List[].class);
    }

    @Test
    void load_should_handle_whitespace_in_array_type() {
        assertThat(classManager.load("String [ ]")).isEqualTo(String[].class);
        assertThat(classManager.load("int [ ] [ ]")).isEqualTo(int[][].class);
    }

    @Test
    void load_should_throw_on_class_not_found() {
        assertThatThrownBy(() -> classManager.load("NonExistent"))
                .isInstanceOf(ScriptParseException.class)
                .hasMessageContaining("Class<?> not found: NonExistent");

        assertThatThrownBy(() -> classManager.load("com.example.NonExistent"))
                .isInstanceOf(ScriptParseException.class)
                .hasMessageContaining("Class<?> not found: com.example.NonExistent");
    }
}
