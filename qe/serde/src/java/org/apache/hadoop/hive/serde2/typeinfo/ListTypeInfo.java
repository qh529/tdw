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

package org.apache.hadoop.hive.serde2.typeinfo;

import java.io.Serializable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;

public class ListTypeInfo extends TypeInfo implements Serializable {

  private static final long serialVersionUID = 1L;
  TypeInfo listElementTypeInfo;

  public ListTypeInfo() {
  }

  public String getTypeName() {
    return org.apache.hadoop.hive.serde.Constants.LIST_TYPE_NAME + "<"
        + listElementTypeInfo.getTypeName() + ">";
  }

  public void setListElementTypeInfo(TypeInfo listElementTypeInfo) {
    this.listElementTypeInfo = listElementTypeInfo;
  }

  ListTypeInfo(TypeInfo elementTypeInfo) {
    this.listElementTypeInfo = elementTypeInfo;
  }

  public Category getCategory() {
    return Category.LIST;
  }

  public TypeInfo getListElementTypeInfo() {
    return listElementTypeInfo;
  }

  public boolean equals(Object other) {
    if (this == other)
      return true;
    if (!(other instanceof ListTypeInfo)) {
      return false;
    }
    ListTypeInfo o = (ListTypeInfo) other;
    return o.getCategory().equals(getCategory())
        && o.getListElementTypeInfo().equals(getListElementTypeInfo());
  }

  public int hashCode() {
    return listElementTypeInfo.hashCode();
  }

}
