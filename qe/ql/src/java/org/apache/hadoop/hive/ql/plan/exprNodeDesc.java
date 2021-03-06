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

package org.apache.hadoop.hive.ql.plan;

import java.io.Serializable;
import java.util.List;

import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;

public abstract class exprNodeDesc implements Serializable, Node {
  private static final long serialVersionUID = 1L;
  TypeInfo typeInfo;

  public exprNodeDesc() {
  }

  public exprNodeDesc(TypeInfo typeInfo) {
    this.typeInfo = typeInfo;
    if (typeInfo == null) {
      throw new RuntimeException("typeInfo cannot be null!");
    }
  }

  public abstract exprNodeDesc clone();

  public abstract boolean isSame(Object o);

  public TypeInfo getTypeInfo() {
    return this.typeInfo;
  }

  public void setTypeInfo(TypeInfo typeInfo) {
    this.typeInfo = typeInfo;
  }

  public String getExprString() {
    assert (false);
    return null;
  }

  @explain(displayName = "type")
  public String getTypeString() {
    return typeInfo.getTypeName();
  }

  public List<String> getCols() {
    return null;
  }

  @Override
  public List<exprNodeDesc> getChildren() {
    return null;
  }

  @Override
  public String getName() {
    return this.getClass().getName();
  }

}
