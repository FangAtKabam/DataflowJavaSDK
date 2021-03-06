/*
 * Copyright (C) 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.cloud.dataflow.sdk.util;

import com.google.cloud.dataflow.sdk.values.TupleTag;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests of InstanceBuilder.
 */
@RunWith(JUnit4.class)
@SuppressWarnings("rawtypes")
public class InstanceBuilderTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @SuppressWarnings("unused")
  private static TupleTag createTag(String id) {
    return new TupleTag(id);
  }

  @Test
  public void testFullNameLookup() throws Exception {
    TupleTag tag = InstanceBuilder.ofType(TupleTag.class)
        .fromClassName(InstanceBuilderTest.class.getName())
        .fromFactoryMethod("createTag")
        .withArg(String.class, "hello world!")
        .build();

    Assert.assertEquals("hello world!", tag.getId());
  }

  @Test
  public void testConstructor() throws Exception {
    TupleTag tag = InstanceBuilder.ofType(TupleTag.class)
        .withArg(String.class, "hello world!")
        .build();

    Assert.assertEquals("hello world!", tag.getId());
  }

  @Test
  public void testBadMethod() throws Exception {
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage(
        Matchers.containsString("Unable to find factory method"));

    InstanceBuilder.ofType(String.class)
        .fromClassName(InstanceBuilderTest.class.getName())
        .fromFactoryMethod("nonexistantFactoryMethod")
        .withArg(String.class, "hello")
        .withArg(String.class, " world!")
        .build();
  }

  @Test
  public void testBadArgs() throws Exception {
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage(
        Matchers.containsString("Unable to find factory method"));

    InstanceBuilder.ofType(TupleTag.class)
        .fromClassName(InstanceBuilderTest.class.getName())
        .fromFactoryMethod("createTag")
        .withArg(String.class, "hello")
        .withArg(Integer.class, 42)
        .build();
  }

  @Test
  public void testBadReturnType() throws Exception {
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage(
        Matchers.containsString("must be assignable to String"));

    InstanceBuilder.ofType(String.class)
        .fromClassName(InstanceBuilderTest.class.getName())
        .fromFactoryMethod("createTag")
        .withArg(String.class, "hello")
        .build();
  }

  @Test
  public void testWrongType() throws Exception {
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage(
        Matchers.containsString("must be assignable to TupleTag"));

    InstanceBuilder.ofType(TupleTag.class)
        .fromClassName(InstanceBuilderTest.class.getName())
        .build();
  }
}
