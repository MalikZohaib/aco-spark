/**
 * This file is part of choco-solver, http://choco-solver.org/
 *
 * Copyright (c) 2017, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.expression.discrete.logical;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.SolverException;
import org.chocosolver.solver.expression.discrete.relational.ReExpression;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import java.util.HashSet;
import java.util.Map;

/**
 * Binary arithmetic expression
 * <p/>
 * Project: choco-solver.
 *
 * @author Charles Prud'homme
 * @since 28/04/2016.
 */
public class UnLoExpression extends LoExpression {

    /**
     * The model in which the expression is declared
     */
    Model model;

    /**
     * Lazy creation of the underlying variable
     */
    BoolVar me = null;

    /**
     * Operator of the arithmetic expression
     */
    Operator op = null;

    /**
     * The first expression this expression relies on
     */
    private ReExpression e;
    /**
     * Builds a n-ary expression
     *
     * @param op an operator
     * @param e an expression
     */
    public UnLoExpression(Operator op, ReExpression e) {
        this.model = e.getModel();
        this.op = op;
        this.e = e;
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public BoolVar boolVar() {
        if (me == null) {
            BoolVar b = e.boolVar();
            switch (op) {
                case NOT:
                    me = model.boolNotView(b);
                    break;
                default:
                    throw new UnsupportedOperationException("Unary logical expressions does not support " + op.name());
            }
        }
        return me;
    }

    @Override
    public void extractVar(HashSet<IntVar> variables) {
        e.extractVar(variables);
    }

    @Override
    public Constraint decompose() {
        BoolVar v1 = e.boolVar();
        Model model = v1.getModel();
        switch (op) {
            case NOT:
                return model.arithm(v1, "<", 1);
        }
        throw new SolverException("Unexpected case");
    }

    @Override
    public boolean beval(int[] values, Map<IntVar, Integer> map) {
        return op.eval(e.beval(values, map), true);
    }

    @Override
    public String toString() {
        return op.name() + "(" + e.toString()+ ")";
    }
}
