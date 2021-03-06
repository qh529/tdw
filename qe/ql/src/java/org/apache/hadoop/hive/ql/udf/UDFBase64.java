/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.ql.udf;

import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.exec.description;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;

@description(name = "base64",
    value = "_FUNC_(bin) - Convert the argument from binary to a base 64 string")
public class UDFBase64 extends UDF {
  private final transient Text result = new Text();

  public Text evaluate(Text b){
    if (b == null) {
      return null;
    }
  
    byte[] bytes = new byte[b.getLength()];
    System.arraycopy(b.getBytes(), 0, bytes, 0, b.getLength());
    result.set(Base64.encodeBase64(bytes));
    return result;
  }
}
