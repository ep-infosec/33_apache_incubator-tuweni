/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuweni.toml;

import org.apache.tuweni.toml.internal.TomlLexer;
import org.apache.tuweni.toml.internal.TomlParser;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

final class Parser {
  private Parser() {}

  static TomlParseResult parse(CharStream stream, TomlVersion version) {
    TomlLexer lexer = new TomlLexer(stream);
    TomlParser parser = new TomlParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    AccumulatingErrorListener errorListener = new AccumulatingErrorListener();
    parser.addErrorListener(errorListener);
    ParseTree tree = parser.toml();
    TomlTable table = tree.accept(new LineVisitor(errorListener, version));

    return new TomlParseResult() {
      @Override
      public int size() {
        return table.size();
      }

      @Override
      public boolean isEmpty() {
        return table.isEmpty();
      }

      @Override
      public Set<String> keySet() {
        return table.keySet();
      }

      @Override
      public Set<List<String>> keyPathSet(boolean includeTables) {
        return table.keyPathSet(includeTables);
      }

      @Override
      @Nullable
      public Object get(List<String> path) {
        return table.get(path);
      }

      @Override
      @Nullable
      public TomlPosition inputPositionOf(List<String> path) {
        return table.inputPositionOf(path);
      }

      @Override
      public Map<String, Object> toMap() {
        return table.toMap();
      }

      @Override
      public List<TomlParseError> errors() {
        return errorListener.errors();
      }
    };
  }

  static List<String> parseDottedKey(String dottedKey) {
    TomlLexer lexer = new TomlLexer(CharStreams.fromString(dottedKey));
    lexer.mode(TomlLexer.KeyMode);
    TomlParser parser = new TomlParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    AccumulatingErrorListener errorListener = new AccumulatingErrorListener();
    parser.addErrorListener(errorListener);
    List<String> keyList = parser.tomlKey().accept(new KeyVisitor());
    List<TomlParseError> errors = errorListener.errors();
    if (!errors.isEmpty()) {
      TomlParseError e = errors.get(0);
      throw new IllegalArgumentException("Invalid key: " + e.getMessage(), e);
    }
    return keyList;
  }
}
