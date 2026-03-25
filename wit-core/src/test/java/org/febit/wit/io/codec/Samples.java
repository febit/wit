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
package org.febit.wit.io.codec;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Samples {

    static final String TEXT_UTF8 = "UTF-8\n"
            + "ASCII: abcdefghijklmnopqrstuvwxyz\n"
            + "Symbols: !@#$%^&*()_+-=~`|\\;:'\",.<>/?\n"
            + "Chinese: 中文字符\n"
            + "emoji: \uD83D\uDE00\uD83D\uDE02\uD83D\uDE09\n"
            // 装饰符号
            + "Decorators: \uD83C\uDFF3\uFE0F\u200D\uD83C\uDF08\uD83C\uDF1E\uFE0F\uD83C\uDF1F\uFE0F\n"
            // 4字节字符
            + "4-byte chars: \uD83D\uDE00\uD83D\uDE02\uD83D\uDE09\n"
            // 3字节字符
            + "3-byte chars: \u4E2D\u6587\u5B57\u7B26\n";

}
