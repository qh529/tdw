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

package org.apache.hadoop.hive.ql.optimizer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.hadoop.hive.ql.exec.FileSinkOperator;
import org.apache.hadoop.hive.ql.exec.Operator;
import org.apache.hadoop.hive.ql.exec.PartitionerOperator;
import org.apache.hadoop.hive.ql.exec.ScriptOperator;
import org.apache.hadoop.hive.ql.exec.SelectOperator;
import org.apache.hadoop.hive.ql.lib.DefaultGraphWalker;
import org.apache.hadoop.hive.ql.lib.DefaultRuleDispatcher;
import org.apache.hadoop.hive.ql.lib.Dispatcher;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.lib.GraphWalker;
import org.apache.hadoop.hive.ql.lib.NodeProcessor;
import org.apache.hadoop.hive.ql.lib.Rule;
import org.apache.hadoop.hive.ql.lib.RuleRegExp;
import org.apache.hadoop.hive.ql.parse.OpParseContext;
import org.apache.hadoop.hive.ql.parse.ParseContext;
import org.apache.hadoop.hive.ql.parse.RowResolver;
import org.apache.hadoop.hive.ql.parse.SemanticException;

public class ColumnPruner implements Transform {
  protected ParseContext pGraphContext;
  private HashMap<Operator<? extends Serializable>, OpParseContext> opToParseCtxMap;

  public ColumnPruner() {
    pGraphContext = null;
  }

  @SuppressWarnings("nls")
  private Operator<? extends Serializable> putOpInsertMap(
      Operator<? extends Serializable> op, RowResolver rr) {
    OpParseContext ctx = new OpParseContext(rr);
    pGraphContext.getOpParseCtx().put(op, ctx);
    return op;
  }

  public ParseContext transform(ParseContext pactx) throws SemanticException {
    this.pGraphContext = pactx;
    this.opToParseCtxMap = pGraphContext.getOpParseCtx();

    ColumnPrunerProcCtx cppCtx = new ColumnPrunerProcCtx(opToParseCtxMap);

    Map<Rule, NodeProcessor> opRules = new LinkedHashMap<Rule, NodeProcessor>();
    opRules.put(new RuleRegExp("R1", "FIL%"),
        ColumnPrunerProcFactory.getFilterProc());
    opRules.put(new RuleRegExp("R2", "GBY%"),
        ColumnPrunerProcFactory.getGroupByProc());
    opRules.put(new RuleRegExp("R3", "RS%"),
        ColumnPrunerProcFactory.getReduceSinkProc());
    opRules.put(new RuleRegExp("R4", "SEL%"),
        ColumnPrunerProcFactory.getSelectProc());
    opRules.put(new RuleRegExp("R5", "JOIN%"),
        ColumnPrunerProcFactory.getJoinProc());
    opRules.put(new RuleRegExp("R6", "MAPJOIN%"),
        ColumnPrunerProcFactory.getMapJoinProc());
    opRules.put(new RuleRegExp("R7", "TS%"),
        ColumnPrunerProcFactory.getTableScanProc());
    opRules.put(new RuleRegExp("R8", "ANA%"),
        ColumnPrunerProcFactory.getAnalysisProc());
    opRules.put(new RuleRegExp("R10", "LVJ%"),
        ColumnPrunerProcFactory.getLateralViewJoinProc());
    opRules.put(new RuleRegExp("R11", "ROWEXTEND%"),
        ColumnPrunerProcFactory.getRowExtendGroupByProc());
    Dispatcher disp = new DefaultRuleDispatcher(
        ColumnPrunerProcFactory.getDefaultProc(), opRules, cppCtx);
    GraphWalker ogw = new ColumnPrunerWalker(disp);

    ArrayList<Node> topNodes = new ArrayList<Node>();
    topNodes.addAll(pGraphContext.getTopOps().values());
    ogw.startWalking(topNodes, null);
    return pGraphContext;
  }

  public static class ColumnPrunerWalker extends DefaultGraphWalker {

    public ColumnPrunerWalker(Dispatcher disp) {
      super(disp);
    }

    @Override
    public void walk(Node nd) throws SemanticException {
      boolean walkChildren = true;
      opStack.push(nd);

      if (nd instanceof SelectOperator) {
        for (Node child : nd.getChildren()) {
          if ((child instanceof FileSinkOperator)
              || child instanceof PartitionerOperator
              || (child instanceof ScriptOperator))
            walkChildren = false;
        }
      }

      if ((nd.getChildren() == null)
          || getDispatchedList().containsAll(nd.getChildren()) || !walkChildren) {
        dispatch(nd, opStack);
        opStack.pop();
        return;
      }
      getToWalk().removeAll(nd.getChildren());
      getToWalk().addAll(0, nd.getChildren());
      getToWalk().add(nd);
      opStack.pop();
    }
  }
}
